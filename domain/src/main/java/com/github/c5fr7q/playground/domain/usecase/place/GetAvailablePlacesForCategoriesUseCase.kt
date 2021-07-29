package com.github.c5fr7q.playground.domain.usecase.place

import com.github.c5fr7q.playground.domain.entity.Place
import com.github.c5fr7q.playground.domain.repository.PlaceRepository
import com.github.c5fr7q.util.filterIterable
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class GetAvailablePlacesForCategoriesUseCase @Inject constructor(
	private val placeRepository: PlaceRepository,
	private val getBlockedPlaces: GetBlockedPlacesUseCase
) {
	operator fun invoke(categories: List<Place.Category>): Flow<List<Place>> {
		return placeRepository
			.getLoadedPlaces()
			.map { it.data ?: emptyList() }
			.combine(getBlockedPlaces()) { loaded, blocked -> loaded - blocked }
			.filterIterable { it.categories.containsAll(categories) }
	}
}