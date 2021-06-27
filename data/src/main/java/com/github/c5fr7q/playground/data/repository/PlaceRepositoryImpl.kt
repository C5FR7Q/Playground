package com.github.c5fr7q.playground.data.repository

import com.github.c5fr7q.playground.data.GeneralCoroutineScope
import com.github.c5fr7q.playground.data.manager.ILocationManager
import com.github.c5fr7q.playground.data.repository.mapper.PlaceDtoMapper
import com.github.c5fr7q.playground.data.repository.mapper.SygicPlaceMapper
import com.github.c5fr7q.playground.data.source.local.Storage
import com.github.c5fr7q.playground.data.source.local.database.dao.PlaceDao
import com.github.c5fr7q.playground.data.source.local.database.entity.PlaceDto
import com.github.c5fr7q.playground.data.source.remote.sygic.SygicService
import com.github.c5fr7q.playground.data.source.remote.unsplash.UnsplashPhotoProvider
import com.github.c5fr7q.playground.domain.entity.Place
import com.github.c5fr7q.playground.domain.entity.Position
import com.github.c5fr7q.playground.domain.entity.UpdatedPlacesStatus
import com.github.c5fr7q.playground.domain.repository.PlaceRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.time.Instant
import java.util.*
import javax.inject.Inject
import kotlin.math.round

class PlaceRepositoryImpl @Inject constructor(
	private val sygicService: SygicService,
	private val unsplashPhotoProvider: UnsplashPhotoProvider,
	private val locationManager: ILocationManager,
	private val sygicPlaceMapper: SygicPlaceMapper,
	private val placeDtoMapper: PlaceDtoMapper,
	private val placeDao: PlaceDao,
	storage: Storage,
	@GeneralCoroutineScope private val generalScope: CoroutineScope
) : PlaceRepository {

	private val requestedPlaces = MutableSharedFlow<List<Place>>(1)
	private val placesStatus = MutableSharedFlow<UpdatedPlacesStatus>(1).apply { tryEmit(UpdatedPlacesStatus.LOADED) }

	private var currentPosition = Position(0f, 0f)

	private var requestedPosition: Position? = null
	private var requestedCategories: List<Place.Category>? = null
	private var requestedRadius: Int? = null

	private var placesPackCount: Int = 0
	private var placesMetersRadius: Int = 0

	init {
		storage.getPlacesPackCount()
			.onEach { placesPackCount = it }
			.launchIn(generalScope)

		storage.getPlacesRadius()
			.onEach { placesMetersRadius = it }
			.launchIn(generalScope)

		storage.getDataCachingTime()
			.take(1)
			.onEach { placeDao.deleteOutdated(Date.from(Instant.now()).time - it.toMillis()) }
			.launchIn(generalScope)

		placeDao.getAllPlaces()
			.take(1)
			.onEach { places ->
				if (places.indexOfFirst { it.imageUrl.isEmpty() } == -1) return@onEach

				placeDao.updatePlaces(places.populateWithPhotos())
			}
			.launchIn(generalScope)
	}

	override fun getPreviousPlaces(): Flow<List<Place>> {
		return placeDao
			.getAllPlaces()
			.map { list ->
				list
					.map { placeDtoMapper.mapDtoToPlace(it) }
					.filter { !it.isBlocked }
			}
	}

	override fun updatePlaces(categories: List<Place.Category>) {
		generalScope.launch {
			currentPosition = locationManager.getLastKnownLocation() ?: currentPosition
			val canRefreshPlaces = requestedCategories == null ||
					requestedRadius == null ||
					requestedPosition == null ||
					requestedCategories != categories ||
					requestedRadius != placesMetersRadius ||
					!(requestedPosition!!.almostTheSameAs(currentPosition))
			if (canRefreshPlaces) {
				requestedCategories = categories
				requestedRadius = placesMetersRadius
				requestedPosition = currentPosition
				fetchPlaces(categories, placesMetersRadius, 0)
			} else {
				placesStatus.emit(placesStatus.replayCache[0])
			}
		}
	}

	override fun loadMorePlaces() {
		generalScope.launch {
			fetchPlaces(
				requestedCategories!!,
				requestedRadius!!,
				requestedPlaces.replayCache[0].size
			)
		}
	}

	override fun getUpdatedPlaces(): Flow<List<Place>> {
		return requestedPlaces.flatMapLatest { places ->
			getPreviousPlaces().map { previousPlaces ->
				val favoritePlacesIds = previousPlaces.filter { it.isFavorite }.map { it.id }
				places.updateFavorites(favoritePlacesIds)
			}
		}.combine(getBlockedPlaces()) { places, blockedPlaces ->
			val blockedPlacesIds = blockedPlaces.map { it.id }
			places.filter { !blockedPlacesIds.contains(it.id) }
		}
	}

	override fun getUpdatedPlacesStatus() = placesStatus.asSharedFlow()

	override fun toggleFavoriteState(place: Place) {
		generalScope.launch {
			placeDao.run {
				if (place.isFavorite) {
					removePlaceFromFavorite(place.id)
				} else {
					addPlaceToFavorite(place.id)
				}
			}
		}
	}

	override fun getFavoritePlaces() = getPreviousPlaces().map { list -> list.filter { it.isFavorite } }

	private suspend fun fetchPlaces(categories: List<Place.Category>, radius: Int, offset: Int) {
		try {
			placesStatus.emit(UpdatedPlacesStatus.LOADING)
			val placesResponse = sygicService.getPlaces(
				sygicPlaceMapper.mapCategoriesToString(categories),
				sygicPlaceMapper.mapArea(currentPosition.lat, currentPosition.lon, radius),
				placesPackCount,
				offset
			)
			val places = sygicPlaceMapper.mapResponse(placesResponse).populateWithPhotos()
			places.map { placeDtoMapper.mapPlaceToDto(it) }.let { placeDao.addPlaces(it) }
			requestedPlaces.emit(
				when {
					offset != 0 -> requestedPlaces.replayCache[0] + places
					else -> places
				}
			)
			placesStatus.emit(UpdatedPlacesStatus.LOADED)
		} catch (e: Exception) {
			requestedCategories = null
			requestedRadius = null
			requestedPosition = null
			requestedPlaces.emit(emptyList())
			placesStatus.emit(UpdatedPlacesStatus.FAILED)
		}
	}

	override fun blockPlace(place: Place) {
		generalScope.launch {
			placeDao.addPlaceToBlocked(place.id)
		}
	}

	override fun unblockPlaces(places: List<Place>) {
		generalScope.launch {
			placeDao.removePlacesFromBlocked(places.map { it.id })
		}
	}

	override fun getBlockedPlaces(): Flow<List<Place>> {
		return placeDao
			.getAllPlaces()
			.map { list ->
				list
					.map { placeDtoMapper.mapDtoToPlace(it) }
					.filter { it.isBlocked }
			}
	}

	private fun List<Place>.updateFavorites(favoritePlacesIds: List<String>): List<Place> {
		return map { place ->
			if (place.id in favoritePlacesIds) {
				place.copy(isFavorite = true)
			} else {
				place
			}
		}
	}

	private suspend fun List<Place>.populateWithPhotos(): List<Place> {
		val countToRequest = count { it.imageUrl.isEmpty() }
		if (countToRequest == 0) return this
		val photos = unsplashPhotoProvider.getPhotos(placesPackCount).toMutableList()
		return map { place ->
			when {
				place.imageUrl.isEmpty() -> place.copy(imageUrl = photos.removeFirstOrNull() ?: "")
				else -> place
			}
		}
	}

	@JvmName("populateWithPhotosPlaceDto")
	private suspend fun List<PlaceDto>.populateWithPhotos(): List<PlaceDto> {
		val countToRequest = count { it.imageUrl.isEmpty() }
		if (countToRequest == 0) return this
		val photos = unsplashPhotoProvider.getPhotos(placesPackCount).toMutableList()
		return map { place ->
			when {
				place.imageUrl.isEmpty() -> place.copy(imageUrl = photos.removeFirstOrNull() ?: "")
				else -> place
			}
		}
	}

	private fun Position.almostTheSameAs(other: Position): Boolean {
		fun Position.dropDigits(): Position {
			return Position(
				lon = round(lon * 100.0f) / 100.0f,
				lat = round(lat * 100.0f) / 100.0f
			)
		}

		return dropDigits() == other.dropDigits()
	}

}