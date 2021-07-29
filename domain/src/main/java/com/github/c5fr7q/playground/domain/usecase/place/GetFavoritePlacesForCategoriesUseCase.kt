package com.github.c5fr7q.playground.domain.usecase.place

import com.github.c5fr7q.playground.domain.entity.Place
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import javax.inject.Inject

class GetFavoritePlacesForCategoriesUseCase @Inject constructor(
	private val getAvailablePlacesForCategories: GetAvailablePlacesForCategoriesUseCase,
	private val getFavoritePlaces: GetFavoritePlacesUseCase
) {
	operator fun invoke(categories: List<Place.Category>): Flow<List<Place>> {
		return getAvailablePlacesForCategories(categories)
			.combine(getFavoritePlaces()) { available, favorite -> available.filter { it in favorite } }
	}
}