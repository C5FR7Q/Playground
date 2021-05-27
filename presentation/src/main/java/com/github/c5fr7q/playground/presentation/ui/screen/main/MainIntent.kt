package com.github.c5fr7q.playground.presentation.ui.screen.main

import com.github.c5fr7q.playground.domain.entity.Place

sealed class MainIntent {
	object Init : MainIntent()
	object LoadMore : MainIntent()
	object ClickLike : MainIntent()
	object ClickPrevious : MainIntent()
	object ClickBlocked : MainIntent()
	object ClickSettings : MainIntent()
	object ClickRefresh : MainIntent()
	data class ToggleCategory(val category: Place.Category) : MainIntent()
	data class ToggleItemFavorite(val place: Place) : MainIntent()
}