package com.github.c5fr7q.playground.domain.usecase.place

import com.github.c5fr7q.playground.domain.repository.PlaceRepository
import com.github.c5fr7q.util.filterIterable
import kotlinx.coroutines.flow.distinctUntilChanged
import javax.inject.Inject

class GetBlockedPlacesUseCase @Inject constructor(
	private val placeRepository: PlaceRepository
) {
	operator fun invoke() = placeRepository.allPlaces.filterIterable { it.isBlocked }.distinctUntilChanged()

}