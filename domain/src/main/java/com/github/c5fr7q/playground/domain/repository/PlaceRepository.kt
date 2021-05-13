package com.github.c5fr7q.playground.domain.repository

import com.github.c5fr7q.playground.domain.entity.Place
import kotlinx.coroutines.flow.Flow

interface PlaceRepository {
	fun getPlaces(categories: List<Place.Category>, radius: Int): Flow<List<Place>>
	fun loadMorePlaces()
}