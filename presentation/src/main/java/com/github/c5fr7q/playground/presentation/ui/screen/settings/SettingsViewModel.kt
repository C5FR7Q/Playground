package com.github.c5fr7q.playground.presentation.ui.screen.settings

import com.github.c5fr7q.playground.presentation.manager.NavigationManager
import com.github.c5fr7q.playground.presentation.ui.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
	private val navigationManager: NavigationManager
) : BaseViewModel<SettingsState, SettingsIntent>() {
	override val mutableState = MutableStateFlow(SettingsState())

	override fun attach() {
		super.attach()
		produceIntent(SettingsIntent.Init)
	}

	override fun handleIntent(intent: SettingsIntent) {
		when (intent) {
			SettingsIntent.Init -> {
			}
			SettingsIntent.ClickBack -> {
				navigationManager.closeScreen()
			}
		}
	}

}