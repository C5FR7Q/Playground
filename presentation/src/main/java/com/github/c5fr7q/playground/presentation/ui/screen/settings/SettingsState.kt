package com.github.c5fr7q.playground.presentation.ui.screen.settings

data class SettingsState(
	val packCount: Int = 0,
	val cachingDays: Int = 0,
	val radius: Int = 0,
	val inputReceiver: InputReceiver = InputReceiver.NONE
)