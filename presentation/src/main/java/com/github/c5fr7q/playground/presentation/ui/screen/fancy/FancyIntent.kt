package com.github.c5fr7q.playground.presentation.ui.screen.fancy

sealed class FancyIntent {
	object Init : FancyIntent()
	object LoadMore : FancyIntent()
	data class ClickItem(val userId: String) : FancyIntent()
}