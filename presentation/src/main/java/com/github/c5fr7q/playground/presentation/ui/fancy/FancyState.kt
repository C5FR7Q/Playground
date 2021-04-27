package com.github.c5fr7q.playground.presentation.ui.fancy

data class FancyState(
	val name: String,
	val numbers: List<Int>
) {
	companion object {
		val Default = FancyState("", emptyList())
	}
}