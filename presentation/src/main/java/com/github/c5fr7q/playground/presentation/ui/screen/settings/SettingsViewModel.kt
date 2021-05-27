package com.github.c5fr7q.playground.presentation.ui.screen.settings

import com.github.c5fr7q.playground.presentation.ui.base.BaseIntent
import com.github.c5fr7q.playground.presentation.ui.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor() : BaseViewModel<SettingsState, SettingsIntent>() {
	override val mutableState = MutableStateFlow(SettingsState())

	override fun handleIntent(intent: BaseIntent.Default) {
		super.handleIntent(intent)
		if (intent is BaseIntent.Default.Init) {

		}
	}

	override fun handleIntent(intent: SettingsIntent) {
		when (intent) {
			SettingsIntent.Click -> {
			}
		}
	}
}