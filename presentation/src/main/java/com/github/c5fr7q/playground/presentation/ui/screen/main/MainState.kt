package com.github.c5fr7q.playground.presentation.ui.screen.main

import com.github.c5fr7q.playground.domain.entity.Place

data class MainState(
	val isLoading: Boolean = false,
	val likedOnly: Boolean = false,
	val places: List<Place> = emptyList(),
	val selectedCategories: List<Place.Category> = emptyList()
)