package com.github.c5fr7q.playground.presentation.ui.screen.blocked

import com.github.c5fr7q.playground.presentation.ui.base.BaseIntent

sealed class BlockedIntent: BaseIntent {
	object Click: BlockedIntent()
}