package com.github.c5fr7q.playground.presentation.ui.screen.main

import com.github.c5fr7q.playground.domain.entity.Place

sealed class MainIntent {
	object LoadMore : MainIntent()
	object ClickSettings : MainIntent()
	object ClickRefresh : MainIntent()
	object ClickLike : MainIntent()
	data class ToggleCategory(val category: Place.Category) : MainIntent()
	data class ToggleItemFavorite(val place: Place) : MainIntent()
	data class ClickBlock(val place: Place) : MainIntent()
	data class ClickShowInMaps(val place: Place) : MainIntent()
}