package com.github.c5fr7q.playground.domain.usecase.place

import com.github.c5fr7q.playground.domain.entity.Place
import com.github.c5fr7q.playground.domain.repository.PlaceRepository
import javax.inject.Inject

class DislikePlaceUseCase @Inject constructor(
	private val placeRepository: PlaceRepository
) {
	fun execute(place: Place) = placeRepository.dislikePlace(place)
}