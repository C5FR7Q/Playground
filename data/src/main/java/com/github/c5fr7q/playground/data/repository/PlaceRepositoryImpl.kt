package com.github.c5fr7q.playground.data.repository

import android.util.Log
import com.github.c5fr7q.playground.data.manager.ILocationManager
import com.github.c5fr7q.playground.data.repository.mapper.PlaceDtoMapper
import com.github.c5fr7q.playground.data.source.local.database.dao.PlaceDao
import com.github.c5fr7q.playground.data.source.remote.resourceprocessor.PlaceProcessor
import com.github.c5fr7q.playground.domain.entity.Place
import com.github.c5fr7q.playground.domain.entity.base.Resource
import com.github.c5fr7q.playground.domain.repository.PlaceRepository
import com.github.c5fr7q.util.mapIterable
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

class PlaceRepositoryImpl @Inject constructor(
	private val locationManager: ILocationManager,
	private val placeDtoMapper: PlaceDtoMapper,
	private val placeDaoFlow: Flow<@JvmSuppressWildcards PlaceDao>,
	private val placeProcessor: PlaceProcessor,
	private val generalScope: CoroutineScope
) : PlaceRepository {
	private var placeDao: PlaceDao? = null

	override val allPlaces = placeDaoFlow
		.flatMapLatest { placeDao ->
			placeDao
				.getAllPlaces()
				.mapIterable { placeDtoMapper.mapDtoToPlace(it) }
		}.stateIn(generalScope, SharingStarted.WhileSubscribed(), emptyList())

	override val loadedPlaces = allPlaces
		.combine(
			placeProcessor.resource.map { resource ->
				resource.run {
					Resource(loadState, data?.map { it.id } ?: emptyList())
				}
			}
		) { allPlaces, loadedIds ->
			val data = loadedIds.data
			Resource(
				loadState = loadedIds.loadState,
				data = when (data) {
					null -> allPlaces
					else -> data.mapNotNull { id -> allPlaces.firstOrNull { it.id == id } }
				},
				message = loadedIds.message
			)
		}.stateIn(generalScope, SharingStarted.WhileSubscribed(), Resource.success(emptyList()))

	override fun blockPlace(place: Place) {
		generalScope.launch {
			getPlaceDao().blockPlace(place.id)
		}
	}

	override fun unblockPlaces(places: List<Place>) {
		generalScope.launch {
			getPlaceDao().unblockPlaces(places.map { it.id })
		}
	}

	override fun likePlace(place: Place) {
		generalScope.launch {
			getPlaceDao().likePlace(place.id)
		}
	}

	override fun dislikePlaces(places: List<Place>) {
		generalScope.launch {
			getPlaceDao().dislikePlaces(places.map { it.id })
		}
	}

	override fun loadMorePlaces() {
		generalScope.launch {
			placeProcessor.process(
				PlaceProcessor.Request.LoadMore
			)
		}
	}

	override fun reloadPlaces(categories: List<Place.Category>) {
		generalScope.launch {
			locationManager.getLastKnownLocation()?.let { position ->
				placeProcessor.process(
					PlaceProcessor.Request.Reload(
						position = position,
						categories = categories
					)
				)
			}
		}
	}

	private suspend fun getPlaceDao(): PlaceDao {
		if (placeDao == null) {
			placeDao = placeDaoFlow.first()
		}
		return placeDao!!
	}
}