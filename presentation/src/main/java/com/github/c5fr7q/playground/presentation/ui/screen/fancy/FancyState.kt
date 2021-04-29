package com.github.c5fr7q.playground.presentation.ui.screen.fancy

data class FancyState(
	val name: String = "",
	val numbers: List<Int> = emptyList(),
	val currentResult: Int = 0
)