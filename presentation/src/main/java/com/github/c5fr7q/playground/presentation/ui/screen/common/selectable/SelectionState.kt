package com.github.c5fr7q.playground.presentation.ui.screen.common.selectable

import com.github.c5fr7q.playground.domain.entity.Place

data class SelectionState(
	val places: List<Place> = emptyList(),
	val selectedPlaces: List<Place> = emptyList(),
	val selectedCategories: List<Place.Category> = emptyList()
)