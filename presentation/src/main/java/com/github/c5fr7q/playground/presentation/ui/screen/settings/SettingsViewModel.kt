package com.github.c5fr7q.playground.presentation.ui.screen.settings

import androidx.lifecycle.viewModelScope
import com.github.c5fr7q.playground.domain.repository.SettingsRepository
import com.github.c5fr7q.playground.presentation.R
import com.github.c5fr7q.playground.presentation.ui.base.BaseIntent
import com.github.c5fr7q.playground.presentation.ui.base.BaseViewModel
import com.github.c5fr7q.util.ResourceHelper
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import java.time.Duration
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
	private val settingsRepository: SettingsRepository,
	private val resourceHelper: ResourceHelper
) : BaseViewModel<SettingsState, Unit, SettingsIntent>() {
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
				navigationManager.openInputDialog(
					title = resourceHelper.getString(R.string.caching_days),
					defaultValue = state.value.cachingDays,
					onApplyValue = { settingsRepository.setDataCachingTime(Duration.ofDays(it.toLong())) }
				)
			}
			SettingsIntent.ClickPackCount -> {
				navigationManager.openInputDialog(
					title = resourceHelper.getString(R.string.pack_count),
					defaultValue = state.value.packCount,
					onApplyValue = { settingsRepository.setPlacesPackCount(it) }
				)
			}
			SettingsIntent.ClickRadius -> {
				navigationManager.openInputDialog(
					title = resourceHelper.getString(R.string.radius),
					defaultValue = state.value.radius,
					onApplyValue = { settingsRepository.setPlacesRadius(it) }
				)
			}
		}
	}
}