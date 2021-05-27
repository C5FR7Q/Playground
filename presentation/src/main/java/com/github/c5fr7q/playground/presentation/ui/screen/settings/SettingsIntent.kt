package com.github.c5fr7q.playground.presentation.ui.screen.settings

sealed class SettingsIntent {
	object Init : SettingsIntent()
	object ClickBack : SettingsIntent()
}