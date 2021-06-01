package com.github.c5fr7q.playground.presentation.ui.screen.settings

import androidx.lifecycle.viewModelScope
import com.github.c5fr7q.playground.domain.repository.SettingsRepository
import com.github.c5fr7q.playground.presentation.ui.base.BaseIntent
import com.github.c5fr7q.playground.presentation.ui.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import java.time.Duration
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
	private val settingsRepository: SettingsRepository
) : BaseViewModel<SettingsState, SettingsIntent>() {
	override fun handleIntent(intent: BaseIntent.Default) {
		super.handleIntent(intent)
		if (intent is BaseIntent.Default.Init) {
			settingsRepository.getDataCachingTime()
				.onEach { updateState { copy(cachingDays = it.toDays().toInt()) } }
				.launchIn(viewModelScope)

			settingsRepository.getPlacesPackCount()
				.onEach { updateState { copy(packCount = it) } }
				.launchIn(viewModelScope)

			settingsRepository.getPlacesRadius()
				.onEach { updateState { copy(radius = it) } }
				.launchIn(viewModelScope)
		}
	}

	override fun handleIntent(intent: SettingsIntent) {
		when (intent) {
			SettingsIntent.ClickCachingDays -> {
				updateState { copy(inputReceiver = InputReceiver.CACHING_DAYS) }
			}
			SettingsIntent.ClickPackCount -> {
				updateState { copy(inputReceiver = InputReceiver.PACK_COUNT) }
			}
			SettingsIntent.ClickRadius -> {
				updateState { copy(inputReceiver = InputReceiver.RADIUS) }
			}
			SettingsIntent.DismissDialog -> {
				updateState { copy(inputReceiver = InputReceiver.NONE) }
			}
			is SettingsIntent.InputValue -> {
				when (state.value.inputReceiver) {
					InputReceiver.CACHING_DAYS -> settingsRepository.setDataCachingTime(Duration.ofDays(intent.value.toLong()))
					InputReceiver.PACK_COUNT -> settingsRepository.setPlacesPackCount(intent.value)
					InputReceiver.RADIUS -> settingsRepository.setPlacesRadius(intent.value)
					else -> Unit
				}
			}
		}
	}
}