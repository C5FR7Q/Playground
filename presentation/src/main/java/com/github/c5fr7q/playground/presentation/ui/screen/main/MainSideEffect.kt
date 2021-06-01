package com.github.c5fr7q.playground.presentation.ui.screen.main

sealed class MainSideEffect {
	object ScrollToTop : MainSideEffect()
	data class ShowError(val text: String) : MainSideEffect()
}