package com.github.c5fr7q.playground.presentation.ui.screen.settings

import com.github.c5fr7q.playground.domain.usecase.settings.*
import com.github.c5fr7q.playground.presentation.R
import com.github.c5fr7q.playground.presentation.ui.base.BaseViewModel
import com.github.c5fr7q.util.ResourceHelper
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.onEach
import java.time.Duration
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
	getDataCachingTime: GetDataCachingTimeUseCase,
	getPlacesPackCount: GetPlacesPackCountUseCase,
	getPlacesRadius: GetPlacesRadiusUseCase,
	private val setDataCachingTime: SetDataCachingTimeUseCase,
	private val setPlacesPackCount: SetPlacesPackCountUseCase,
	private val setPlacesRadius: SetPlacesRadiusUseCase,
	private val resourceHelper: ResourceHelper
) : BaseViewModel<SettingsState, Unit, SettingsIntent>() {
	override val defaultState = SettingsState()

	init {
		getDataCachingTime()
			.onEach { updateState { copy(cachingDays = it.toDays().toInt()) } }
			.launchIfActive()

		getPlacesPackCount()
			.onEach { updateState { copy(packCount = it) } }
			.launchIfActive()

		getPlacesRadius()
			.onEach { updateState { copy(radius = it) } }
			.launchIfActive()
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