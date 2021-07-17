package com.github.c5fr7q.playground.domain.usecase.place

import com.github.c5fr7q.playground.domain.repository.PlaceRepository
import javax.inject.Inject

class LoadMorePlacesUseCase @Inject constructor(
	private val placeRepository: PlaceRepository
) {
	fun execute() = placeRepository.loadMorePlaces()
}