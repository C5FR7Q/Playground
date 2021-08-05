package com.github.c5fr7q.playground.domain.repository

import com.github.c5fr7q.playground.domain.entity.Place
import com.github.c5fr7q.playground.domain.entity.base.Resource
import kotlinx.coroutines.flow.Flow

interface PlaceRepository {
	val allPlaces: Flow<List<Place>>
	val loadedPlaces: Flow<Resource<List<Place>>>

	fun blockPlace(place: Place)
	fun unblockPlaces(places: List<Place>)
	fun likePlace(place: Place)
	fun dislikePlaces(places: List<Place>)
	fun loadMorePlaces()
	fun reloadPlaces(categories: List<Place.Category>)
}