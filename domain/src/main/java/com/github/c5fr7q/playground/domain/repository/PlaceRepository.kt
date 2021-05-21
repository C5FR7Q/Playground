package com.github.c5fr7q.playground.domain.repository

import com.github.c5fr7q.playground.domain.entity.Place
import kotlinx.coroutines.flow.Flow

interface PlaceRepository {
	suspend fun getPreviousPlaces(): List<Place>
	suspend fun tryRefreshPlaces(categories: List<Place.Category>): Boolean

	fun getPlaces(): Flow<List<Place>>
	fun loadMorePlaces()
}