package com.github.c5fr7q.playground.data.repository

import com.github.c5fr7q.playground.data.GeneralCoroutineScope
import com.github.c5fr7q.playground.data.repository.mapper.PlaceDtoMapper
import com.github.c5fr7q.playground.data.repository.mapper.SygicPlaceMapper
import com.github.c5fr7q.playground.data.source.local.Storage
import com.github.c5fr7q.playground.data.source.local.database.dao.PlaceDao
import com.github.c5fr7q.playground.data.source.remote.sygic.SygicService
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

class PlaceRepositoryImpl @Inject constructor(
	private val sygicService: SygicService,
	private val sygicPlaceMapper: SygicPlaceMapper,
	private val placeDtoMapper: PlaceDtoMapper,
	private val placeDao: PlaceDao,
	storage: Storage,
	@GeneralCoroutineScope private val generalScope: CoroutineScope
) : PlaceRepository {

	private val currentPosition = MutableStateFlow(Position(84.98107413147059f, 56.481796f)) // TODO: 10.05.2021 0f, 0f
	private val requestedPlaces = MutableSharedFlow<List<Place>>(1)
	private val placesStatus = MutableStateFlow(UpdatedPlacesStatus.LOADED)

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
	}

	override fun getPreviousPlaces() = placeDao.getAllPlaces().map { list -> list.map { placeDtoMapper.mapDtoToPlace(it) } }

	override fun updatePlaces(categories: List<Place.Category>) {
		val canRefreshPlaces = requestedCategories == null ||
				requestedRadius == null ||
				requestedPosition == null ||
				requestedCategories != categories ||
				requestedRadius != placesMetersRadius ||
				requestedPosition != currentPosition.value
		if (canRefreshPlaces) {
			requestedCategories = categories
			requestedRadius = placesMetersRadius
			requestedPosition = currentPosition.value
			generalScope.launch {
				fetchPlaces(categories, placesMetersRadius, 0)
			}
		}
	}

	override fun loadMorePlaces() {
		generalScope.launch {
			fetchPlaces(requestedCategories!!, requestedRadius!!, requestedPlaces.replayCache[0].size)
		}
	}

	override fun getUpdatedPlaces(): Flow<List<Place>> {
		return requestedPlaces.flatMapLatest { places ->
			getPreviousPlaces().map { previousPlaces ->
				val favoritePlacesIds = previousPlaces.filter { it.isFavorite }.map { it.id }
				places.updateFavorites(favoritePlacesIds)
			}
		}
	}

	override fun getUpdatedPlacesStatus() = placesStatus.asStateFlow()

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
			placesStatus.value = UpdatedPlacesStatus.LOADING
			val placesResponse = sygicService.getPlaces(
				sygicPlaceMapper.mapCategoriesToString(categories),
				sygicPlaceMapper.mapArea(currentPosition.value.lat, currentPosition.value.lon, radius),
				placesPackCount,
				offset
			)
			val places = sygicPlaceMapper.mapResponse(placesResponse)
			places.map { placeDtoMapper.mapPlaceToDto(it) }.let { placeDao.addPlaces(it) }
			requestedPlaces.emit(
				when {
					offset != 0 -> requestedPlaces.replayCache[0] + places
					else -> places
				}
			)
			placesStatus.value = UpdatedPlacesStatus.LOADED
		} catch (e: Exception) {
			requestedCategories = null
			requestedRadius = null
			requestedPosition = null
			requestedPlaces.emit(emptyList())
			placesStatus.value = UpdatedPlacesStatus.FAILED
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

}