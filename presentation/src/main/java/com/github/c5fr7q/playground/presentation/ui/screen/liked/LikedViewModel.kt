package com.github.c5fr7q.playground.presentation.ui.screen.liked

import com.github.c5fr7q.playground.domain.entity.Place
import com.github.c5fr7q.playground.domain.usecase.place.DislikePlacesUseCase
import com.github.c5fr7q.playground.domain.usecase.place.GetFavoritePlacesUseCase
import com.github.c5fr7q.playground.presentation.ui.screen.common.selectable.SelectionViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class LikedViewModel @Inject constructor(
	private val getFavoritePlacesUseCase: GetFavoritePlacesUseCase,
	private val dislikePlacesUseCase: DislikePlacesUseCase
) : SelectionViewModel() {
	override fun getPlaces() = getFavoritePlacesUseCase.execute()
	override fun applySelection(places: List<Place>) = dislikePlacesUseCase.execute(places)
}