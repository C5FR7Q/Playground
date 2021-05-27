package com.github.c5fr7q.playground.presentation.ui.screen.settings

import com.github.c5fr7q.playground.presentation.ui.base.BaseIntent

sealed class SettingsIntent : BaseIntent {
	object Click : SettingsIntent()
}