package com.github.c5fr7q.playground.domain.usecase.place

import android.util.Log
import com.github.c5fr7q.playground.domain.entity.Place
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import javax.inject.Inject

class GetFavoritePlacesForCategoriesUseCase @Inject constructor(
	private val getAvailablePlacesForCategoriesUseCase: GetAvailablePlacesForCategoriesUseCase,
	private val getFavoritePlacesUseCase: GetFavoritePlacesUseCase
) {
	fun execute(categories: List<Place.Category>): Flow<List<Place>> {
		return getAvailablePlacesForCategoriesUseCase
			.execute(categories)
			.combine(getFavoritePlacesUseCase.execute()) { available, favorite ->
				Log.d("VVA", "favorite=$favorite")
				available.filter { it in favorite }
			}
	}
}