package com.github.c5fr7q.playground.domain.usecase.place

import com.github.c5fr7q.playground.domain.repository.PlaceRepository
import javax.inject.Inject

class GetLoadPlacesStatusUseCase @Inject constructor(
	private val placeRepository: PlaceRepository
) {
	operator fun invoke() = placeRepository.getLoadPlacesStatus()
}