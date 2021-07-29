package com.github.c5fr7q.playground.presentation.ui.screen.settings

import androidx.lifecycle.viewModelScope
import com.github.c5fr7q.playground.domain.usecase.settings.*
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
	private val getDataCachingTime: GetDataCachingTimeUseCase,
	private val getPlacesPackCount: GetPlacesPackCountUseCase,
	private val getPlacesRadius: GetPlacesRadiusUseCase,
	private val setDataCachingTime: SetDataCachingTimeUseCase,
	private val setPlacesPackCount: SetPlacesPackCountUseCase,
	private val setPlacesRadius: SetPlacesRadiusUseCase,
	private val resourceHelper: ResourceHelper
) : BaseViewModel<SettingsState, Unit, SettingsIntent>() {
	override val defaultState = SettingsState()

	override fun handleIntent(intent: BaseIntent.Default) {
		super.handleIntent(intent)
		if (intent is BaseIntent.Default.Init) {
			getDataCachingTime()
				.onEach { updateState { copy(cachingDays = it.toDays().toInt()) } }
				.launchIn(viewModelScope)

			getPlacesPackCount()
				.onEach { updateState { copy(packCount = it) } }
				.launchIn(viewModelScope)

			getPlacesRadius()
				.onEach { updateState { copy(radius = it) } }
				.launchIn(viewModelScope)
		}
	}

	override fun handleIntent(intent: SettingsIntent) {
		when (intent) {
			SettingsIntent.ClickBlocked -> {
				navigationManager.openBlocked()
			}
			SettingsIntent.ClickLiked -> {
				navigationManager.openLiked()
			}
			SettingsIntent.ClickCachingDays -> {
				navigationManager.openInputDialog(
					title = resourceHelper.getString(R.string.caching_days),
					defaultValue = state.value.cachingDays,
					onApplyValue = { setDataCachingTime(Duration.ofDays(it.toLong())) }
				)
			}
			SettingsIntent.ClickPackCount -> {
				navigationManager.openInputDialog(
					title = resourceHelper.getString(R.string.pack_count),
					defaultValue = state.value.packCount,
					onApplyValue = { setPlacesPackCount(it) }
				)
			}
			SettingsIntent.ClickRadius -> {
				navigationManager.openInputDialog(
					title = resourceHelper.getString(R.string.radius),
					defaultValue = state.value.radius,
					onApplyValue = { setPlacesRadius(it) }
				)
			}
		}
	}
}