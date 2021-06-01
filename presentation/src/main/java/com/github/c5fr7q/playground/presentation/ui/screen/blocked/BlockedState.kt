package com.github.c5fr7q.playground.presentation.ui.screen.blocked

import com.github.c5fr7q.playground.domain.entity.Place

data class BlockedState(
	val places: List<Place> = emptyList(),
	val selectedPlaces: List<Place> = emptyList(),
	val selectedCategories: List<Place.Category> = emptyList()
)