package com.github.c5fr7q.playground.domain.usecase.place

import com.github.c5fr7q.playground.domain.entity.Place
import com.github.c5fr7q.playground.domain.repository.PlaceRepository
import javax.inject.Inject

class DislikePlacesUseCase @Inject constructor(
	private val placeRepository: PlaceRepository
) {
	fun execute(places: List<Place>) = placeRepository.dislikePlaces(places)
}