package com.github.c5fr7q.playground.domain.usecase.place

import com.github.c5fr7q.playground.domain.entity.Place
import com.github.c5fr7q.playground.domain.repository.PlaceRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class GetAvailablePlacesUseCase @Inject constructor(
	private val placeRepository: PlaceRepository,
	private val getBlockedPlaces: GetBlockedPlacesUseCase
) {
	operator fun invoke(): Flow<List<Place>> {
		return placeRepository
			.getLoadedPlaces()
			.map { it.data ?: emptyList() }
			.combine(getBlockedPlaces()) { loaded, blocked -> loaded - blocked }
	}
}