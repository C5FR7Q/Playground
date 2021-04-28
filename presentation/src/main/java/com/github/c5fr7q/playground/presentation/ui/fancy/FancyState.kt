package com.github.c5fr7q.playground.presentation.ui.fancy

data class FancyState(
	val name: String,
	val numbers: List<Int>,
	val currentResult: Int
) {
	companion object {
		val Default = FancyState("", emptyList(), 0)
	}
}