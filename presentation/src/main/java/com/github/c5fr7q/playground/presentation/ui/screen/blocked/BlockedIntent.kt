package com.github.c5fr7q.playground.presentation.ui.screen.blocked

sealed class BlockedIntent {
	object Init : BlockedIntent()
	object ClickBack : BlockedIntent()
}