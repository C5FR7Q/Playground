package com.github.c5fr7q.playground.domain.usecase.place

import com.github.c5fr7q.playground.domain.entity.Place
import com.github.c5fr7q.playground.domain.repository.PlaceRepository
import com.github.c5fr7q.util.filterIterable
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import javax.inject.Inject

class GetFavoritePlacesUseCase @Inject constructor(
	private val repository: PlaceRepository,
	private val getBlockedPlaces: GetBlockedPlacesUseCase
) {
	operator fun invoke(): Flow<List<Place>> {
		return repository
			.allPlaces
			.filterIterable { it.isFavorite }
			.combine(getBlockedPlaces()) { favorite, blocked -> favorite.filter { it !in blocked } }
	}
}