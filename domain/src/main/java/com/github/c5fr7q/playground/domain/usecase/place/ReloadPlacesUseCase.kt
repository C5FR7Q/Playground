package com.github.c5fr7q.playground.domain.usecase.place

import com.github.c5fr7q.playground.domain.entity.Place
import com.github.c5fr7q.playground.domain.repository.PlaceRepository
import javax.inject.Inject

class ReloadPlacesUseCase @Inject constructor(
	private val placeRepository: PlaceRepository
) {
	fun execute(categories: List<Place.Category>) = placeRepository.reloadPlaces(categories)
}