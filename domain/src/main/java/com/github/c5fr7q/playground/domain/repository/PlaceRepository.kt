package com.github.c5fr7q.playground.domain.repository

import com.github.c5fr7q.playground.domain.entity.Place
import kotlinx.coroutines.flow.Flow

interface PlaceRepository {
	fun getPreviousPlaces(): Flow<List<Place>>
	fun getPlaces(categories: List<Place.Category>): Flow<List<Place>>
	fun loadMorePlaces()
}