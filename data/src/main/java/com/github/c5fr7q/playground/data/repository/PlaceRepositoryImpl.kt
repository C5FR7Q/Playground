package com.github.c5fr7q.playground.data.repository

import com.github.c5fr7q.playground.data.GeneralCoroutineScope
import com.github.c5fr7q.playground.data.manager.ILocationManager
import com.github.c5fr7q.playground.data.repository.mapper.PlaceDtoMapper
import com.github.c5fr7q.playground.data.repository.mapper.SygicPlaceMapper
import com.github.c5fr7q.playground.data.source.local.Storage
import com.github.c5fr7q.playground.data.source.local.database.dao.PlaceDao
import com.github.c5fr7q.playground.data.source.remote.sygic.SygicService
import com.github.c5fr7q.playground.data.source.remote.unsplash.UnsplashPhotoProvider
import com.github.c5fr7q.playground.domain.entity.Place
import com.github.c5fr7q.playground.domain.entity.Position
import com.github.c5fr7q.playground.domain.entity.base.Resource
import com.github.c5fr7q.playground.domain.repository.PlaceRepository
import com.github.c5fr7q.util.mapIterable
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
	private val placeDaoFlow: Flow<@JvmSuppressWildcards PlaceDao>,
	storage: Storage,
	@GeneralCoroutineScope private val generalScope: CoroutineScope
) : PlaceRepository {

	private val loadedPlacesIds = MutableStateFlow(Resource.success(emptyList<String>()))

	private var currentPosition: Position? = null

	private var requestedPosition: Position? = null

	private var requestedCategories: List<Place.Category>? = null
	private var requestedRadius: Int? = null
	private var placesPackCount: Int? = null

	private var placesMetersRadius: Int? = null

	private var prefetchedPlaces = true

	private var allPlaces: List<Place>? = null

	private var placeDao: PlaceDao? = null

	init {
		placeDaoFlow
			.onEach { placeDao = it }
			.launchIn(generalScope)

		storage.getPlacesPackCount()
			.onEach { placesPackCount = it }
			.launchIn(generalScope)

		storage.getPlacesRadius()
			.onEach { placesMetersRadius = it }
			.launchIn(generalScope)

		placeDaoFlow.flatMapLatest { placeDao ->
			storage.getDataCachingTime()
				.take(1)
				.onEach {
					placeDao.deleteOutdatedPlaces(Date.from(Instant.now()).time - it.toMillis())
				}

		}.launchIn(generalScope)

		getAllPlaces().take(1)
			.onEach { places -> loadedPlacesIds.value = Resource.success(places.map { it.id }) }
			.launchIn(generalScope)
	}

	override fun blockPlace(place: Place) {
		generalScope.launch {
			placeDao?.blockPlace(place.id)
		}
	}

	override fun unblockPlaces(places: List<Place>) {
		generalScope.launch {
			placeDao?.unblockPlaces(places.map { it.id })
		}
	}

	override fun likePlace(place: Place) {
		generalScope.launch {
			placeDao?.likePlace(place.id)
		}
	}

	override fun dislikePlaces(places: List<Place>) {
		generalScope.launch {
			placeDao?.dislikePlaces(places.map { it.id })
		}
	}

	override fun loadMorePlaces() {
		generalScope.launch {
			if (requestedCategories != null && requestedRadius != null && !prefetchedPlaces) {
				fetchPlaces(
					requestedCategories!!,
					requestedRadius!!,
					loadedPlacesIds.value.data?.size ?: 0
				)
			}
		}
	}

	override fun reloadPlaces(categories: List<Place.Category>) {
		generalScope.launch {
			currentPosition = locationManager.getLastKnownLocation()

			if (currentPosition == null || placesMetersRadius == null) return@launch

			val canRefreshPlaces = requestedCategories != categories ||
					requestedRadius != placesMetersRadius ||
					!(requestedPosition.almostTheSameAs(currentPosition))
			if (canRefreshPlaces) {
				requestedCategories = categories
				requestedRadius = placesMetersRadius
				requestedPosition = currentPosition
				fetchPlaces(categories, placesMetersRadius!!, 0)
			}
		}
	}

	override fun getLoadedPlaces(): Flow<Resource<List<Place>>> {
		return getAllPlaces()
			.combine(loadedPlacesIds) { allPlaces, loadedIds ->
				Resource(
					loadState = loadedIds.loadState,
					data = allPlaces.filter { it.id in (loadedIds.data ?: emptyList()) },
					message = loadedIds.message
				)
			}
	}

	override fun getAllPlaces(): Flow<List<Place>> {
		return placeDaoFlow.flatMapLatest { placeDao ->
			placeDao.getAllPlaces().mapIterable { placeDtoMapper.mapDtoToPlace(it) }
		}
	}

	private suspend fun fetchPlaces(categories: List<Place.Category>, radius: Int, offset: Int) {
		try {
			if (currentPosition == null || placesPackCount == null) return

			loadedPlacesIds.value = Resource.loading(loadedPlacesIds.value.data)
			val placesResponse = sygicService.getPlaces(
				sygicPlaceMapper.mapCategoriesToString(categories),
				sygicPlaceMapper.mapArea(currentPosition!!.lat, currentPosition!!.lon, radius),
				placesPackCount!!,
				offset
			)
			val newPlaces = sygicPlaceMapper
				.mapResponse(placesResponse)
				.populateWithPhotos()
				.updateFavorites()
			newPlaces.map { placeDtoMapper.mapPlaceToDto(it) }.let { placeDao?.addPlaces(it) }
			loadedPlacesIds.value = Resource.success(
				when {
					offset != 0 -> {
						val data = loadedPlacesIds.value.data ?: emptyList()
						data + newPlaces.map { it.id }
					}
					else -> newPlaces.map { it.id }
				}
			)
		} catch (e: Exception) {
			requestedCategories = null
			requestedRadius = null
			requestedPosition = null
			loadedPlacesIds.value = Resource.error(data = emptyList())
		}
	}

	private fun List<Place>.updateFavorites(): List<Place> {
		if (allPlaces == null) return this
		val favoritePlacesIds = allPlaces!!.filter { it.isFavorite }.map { it.id }
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
		val photos = unsplashPhotoProvider.getPhotos(countToRequest).toMutableList()
		return map { place ->
			when {
				place.imageUrl.isEmpty() -> place.copy(imageUrl = photos.removeFirstOrNull() ?: "")
				else -> place
			}
		}
	}

	private fun Position?.almostTheSameAs(other: Position?): Boolean {
		fun Position?.dropDigits(): Position? {
			return this?.run {
				Position(
					lon = round(lon * 100.0f) / 100.0f,
					lat = round(lat * 100.0f) / 100.0f
				)
			}
		}

		return dropDigits() == other.dropDigits()
	}

}