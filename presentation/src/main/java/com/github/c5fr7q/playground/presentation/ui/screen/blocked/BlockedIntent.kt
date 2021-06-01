package com.github.c5fr7q.playground.presentation.ui.screen.blocked

import com.github.c5fr7q.playground.domain.entity.Place
import com.github.c5fr7q.playground.presentation.ui.base.BaseIntent

sealed class BlockedIntent : BaseIntent {
	object ClickDelete : BlockedIntent()
	data class TogglePlaceSelection(val place: Place) : BlockedIntent()
	data class ToggleCategory(val category: Place.Category) : BlockedIntent()
}