package com.github.c5fr7q.playground.presentation.ui.base

interface BaseIntent {
	sealed class Default : BaseIntent {
		object Init : Default()
		object ClickBack : Default()
	}
}