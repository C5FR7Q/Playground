package com.github.c5fr7q.playground.domain.usecase.place

import com.github.c5fr7q.playground.domain.repository.PlaceRepository
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class GetPlacesLoadStateUseCase @Inject constructor(
	private val placeRepository: PlaceRepository
) {
	operator fun invoke() = placeRepository.loadedPlaces.map { it.loadState }
}