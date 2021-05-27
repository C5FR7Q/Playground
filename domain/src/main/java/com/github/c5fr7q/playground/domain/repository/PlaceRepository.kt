package com.github.c5fr7q.playground.domain.repository

import com.github.c5fr7q.playground.domain.entity.Place
import com.github.c5fr7q.playground.domain.entity.UpdatedPlacesStatus
import kotlinx.coroutines.flow.Flow

interface PlaceRepository {
	fun getPreviousPlaces(): Flow<List<Place>>

	suspend fun updatePlaces(categories: List<Place.Category>): Boolean
	fun loadMorePlaces()
	fun getUpdatedPlacesStatus(): Flow<UpdatedPlacesStatus>
	fun getUpdatedPlaces(): Flow<List<Place>>

	fun toggleFavoriteState(place: Place)
	fun getFavoritePlaces(): Flow<List<Place>>
}