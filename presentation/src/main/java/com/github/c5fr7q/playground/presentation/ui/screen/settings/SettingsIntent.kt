package com.github.c5fr7q.playground.presentation.ui.screen.settings

sealed class SettingsIntent {
	object ClickLiked : SettingsIntent()
	object ClickBlocked : SettingsIntent()
	object ClickPackCount : SettingsIntent()
	object ClickCachingDays : SettingsIntent()
	object ClickRadius : SettingsIntent()
}