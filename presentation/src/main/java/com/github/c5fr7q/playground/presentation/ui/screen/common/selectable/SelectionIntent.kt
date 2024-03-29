package com.github.c5fr7q.playground.presentation.ui.screen.common.selectable

import com.github.c5fr7q.playground.domain.entity.Place

sealed class SelectionIntent {
	object ClickApplySelection : SelectionIntent()
	data class TogglePlaceSelection(val place: Place) : SelectionIntent()
	data class ToggleCategory(val category: Place.Category) : SelectionIntent()
}