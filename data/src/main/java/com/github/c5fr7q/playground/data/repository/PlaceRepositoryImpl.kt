package com.github.c5fr7q.playground.data.repository

import com.github.c5fr7q.playground.data.GeneralCoroutineScope
import com.github.c5fr7q.playground.data.repository.mapper.PlaceDtoMapper
import com.github.c5fr7q.playground.data.repository.mapper.SygicPlaceMapper
import com.github.c5fr7q.playground.data.source.local.Storage
import com.github.c5fr7q.playground.data.source.local.database.dao.PlaceDao
import com.github.c5fr7q.playground.data.source.remote.sygic.SygicService
import com.github.c5fr7q.playground.domain.entity.Place
import com.github.c5fr7q.playground.domain.entity.PlacesStatus
import com.github.c5fr7q.playground.domain.entity.Position
import com.github.c5fr7q.playground.domain.repository.PlaceRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.math.*

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
	private val placesStatus = MutableStateFlow(PlacesStatus.LOADED)

	private var requestedPosition: Position? = null
	private var requestedCategories: List<Place.Category>? = null
	private var requestedRadius: Int? = null

	private var placesPackCount: Int = 0
	private var placesMetersCallThreshold: Int = 0
	private var placesMetersRadius: Int = 0

	init {
		storage.getPlacesPackCount()
			.onEach { placesPackCount = it }
			.launchIn(generalScope)

		storage.getPlacesMetersCallThreshold()
			.onEach { placesMetersCallThreshold = it }
			.launchIn(generalScope)

		storage.getPlacesRadius()
			.onEach { placesMetersCallThreshold = it }
			.launchIn(generalScope)

		// TODO: 21.05.2021
/*
		storage.getDataCachingTime()
			.take(1)
			.onEach { placeDao.deleteOutdated(Date.from(Instant.now()).time + it.toMillis()) }
			.launchIn(generalScope)
*/
	}

	override suspend fun getPreviousPlaces() = placeDao.getAllPlacesOnce().map { placeDtoMapper.mapDtoToPlace(it) }

	override fun getPlaces() = requestedPlaces.asSharedFlow()

	override fun getPlacesStatus() = placesStatus.asStateFlow()

	override suspend fun tryRefreshPlaces(categories: List<Place.Category>): Boolean {
		val canRefreshPlaces = requestedCategories == null ||
				requestedRadius == null ||
				requestedPosition == null ||
				distanceThresholdReached() ||
				requestedCategories != categories ||
				requestedRadius != placesMetersRadius
		if (canRefreshPlaces) {
			requestedCategories = categories
			requestedRadius = placesMetersRadius
			requestedPosition = currentPosition.value
			generalScope.launch {
				fetchPlaces(categories, placesMetersRadius, 0)
			}
		}
		return canRefreshPlaces
	}

	override fun loadMorePlaces() {
		generalScope.launch {
			fetchPlaces(requestedCategories!!, requestedRadius!!, requestedPlaces.replayCache[0].size)
		}
	}

	private suspend fun fetchPlaces(categories: List<Place.Category>, radius: Int, offset: Int) {
		try {
			placesStatus.value = PlacesStatus.LOADING
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
			placesStatus.value = PlacesStatus.LOADED
		} catch (e: Exception) {
			requestedCategories = null
			requestedRadius = null
			requestedPosition = null
			requestedPlaces.emit(emptyList())
			placesStatus.value = PlacesStatus.FAILED
		}
	}

	private fun distanceThresholdReached() = positionDistance(requestedPosition!!, currentPosition.value) > placesMetersCallThreshold

	private fun positionDistance(position1: Position, position2: Position): Int {
		val earthRadius = 6371 /* Kilometres */

		val dLat = (position2.lat - position1.lat).toRad()
		val dLon = (position2.lon - position1.lon).toRad()

		val a = sin(dLat / 2) * sin(dLat / 2) + cos(position1.lat.toRad()) * cos(position2.lat.toRad()) * sin(dLon / 2) * sin(dLon / 2)
		val c = 2 * atan2(sqrt(a), sqrt(1 - a))
		return (earthRadius * c * 1000).roundToInt()
	}

	private fun Float.toRad() = this * (Math.PI / 180).toFloat()

}