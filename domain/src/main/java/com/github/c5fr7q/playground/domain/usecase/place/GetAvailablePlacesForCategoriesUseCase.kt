package com.github.c5fr7q.playground.domain.usecase.place

import com.github.c5fr7q.playground.domain.entity.Place
import com.github.c5fr7q.playground.domain.repository.PlaceRepository
import com.github.c5fr7q.util.filterIterable
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import javax.inject.Inject

class GetAvailablePlacesForCategoriesUseCase @Inject constructor(
	private val placeRepository: PlaceRepository,
	private val getBlockedPlacesUseCase: GetBlockedPlacesUseCase
) {
	fun execute(categories: List<Place.Category>): Flow<List<Place>> {
		return placeRepository
			.getLoadedPlaces()
			.combine(getBlockedPlacesUseCase.execute()) { loaded, blocked -> loaded - blocked }
			.filterIterable { it.categories.containsAll(categories) }
	}
}