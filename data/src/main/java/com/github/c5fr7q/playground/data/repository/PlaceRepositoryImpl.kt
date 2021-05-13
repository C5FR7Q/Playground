package com.github.c5fr7q.playground.data.repository

import com.github.c5fr7q.playground.data.GeneralCoroutineScope
import com.github.c5fr7q.playground.data.repository.mapper.SygicPlaceMapper
import com.github.c5fr7q.playground.data.source.remote.sygic.SygicService
import com.github.c5fr7q.playground.domain.entity.Place
import com.github.c5fr7q.playground.domain.entity.Position
import com.github.c5fr7q.playground.domain.repository.PlaceRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.math.*

class PlaceRepositoryImpl @Inject constructor(
	private val sygicService: SygicService,
	private val sygicPlaceMapper: SygicPlaceMapper,
	@GeneralCoroutineScope private val generalScope: CoroutineScope
) : PlaceRepository {
	companion object {
		private const val DISTANCE_METERS_CALL_THRESHOLD = 100
		private const val PLACES_PACK_COUNT = 10
	}

	private val currentPosition = MutableStateFlow(Position(84.98107413147059f, 56.481796f)) // TODO: 10.05.2021 0f, 0f
	private val requestedPlaces = MutableStateFlow(emptyList<Place>())

	private var requestedPosition: Position? = null
	private var requestedCategories: List<Place.Category>? = null
	private var requestedRadius: Int? = null

	override fun getPlaces(categories: List<Place.Category>, radius: Int): Flow<List<Place>> {
		val wasDistanceReached = requestedPosition == null || distanceThresholdReached()
		if (
			requestedCategories == null || requestedRadius == null ||
			wasDistanceReached || requestedCategories != categories || requestedRadius != radius
		) {
			requestedCategories = categories
			requestedRadius = radius
			requestedPosition = currentPosition.value
			generalScope.launch {
				fetchPlaces(categories, radius, 0)
			}
		}
		return requestedPlaces
	}

	override fun loadMorePlaces() {
		generalScope.launch {
			fetchPlaces(requestedCategories!!, requestedRadius!!, requestedPlaces.value.size)
		}
	}

	private suspend fun fetchPlaces(categories: List<Place.Category>, radius: Int, offset: Int) {
		val placesResponse = sygicService.getPlaces(
			sygicPlaceMapper.mapCategoriesToString(categories),
			sygicPlaceMapper.mapArea(currentPosition.value.lat, currentPosition.value.lon, radius),
			PLACES_PACK_COUNT,
			offset
		)
		requestedPlaces.emit(sygicPlaceMapper.mapResponse(placesResponse))
	}

	private fun distanceThresholdReached() = positionDistance(requestedPosition!!, currentPosition.value) > DISTANCE_METERS_CALL_THRESHOLD

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