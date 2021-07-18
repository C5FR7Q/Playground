package com.github.c5fr7q.playground.domain.repository

import com.github.c5fr7q.playground.domain.entity.LoadPlacesStatus
import com.github.c5fr7q.playground.domain.entity.Place
import kotlinx.coroutines.flow.Flow

interface PlaceRepository {
	fun blockPlace(place: Place)
	fun unblockPlaces(places: List<Place>)
	fun likePlace(place: Place)
	fun dislikePlaces(places: List<Place>)
	fun loadMorePlaces()
	fun reloadPlaces(categories: List<Place.Category>)
	fun getLoadPlacesStatus(): Flow<LoadPlacesStatus>
	fun getLoadedPlaces(): Flow<List<Place>>
	fun getAllPlaces(): Flow<List<Place>>
}