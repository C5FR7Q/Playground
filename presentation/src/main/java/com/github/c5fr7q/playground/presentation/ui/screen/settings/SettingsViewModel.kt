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
	private val getDataCachingTimeUseCase: GetDataCachingTimeUseCase,
	private val getPlacesPackCountUseCase: GetPlacesPackCountUseCase,
	private val getPlacesRadiusUseCase: GetPlacesRadiusUseCase,
	private val setDataCachingTimeUseCase: SetDataCachingTimeUseCase,
	private val setPlacesPackCountUseCase: SetPlacesPackCountUseCase,
	private val setPlacesRadiusUseCase: SetPlacesRadiusUseCase,
	private val resourceHelper: ResourceHelper
) : BaseViewModel<SettingsState, Unit, SettingsIntent>() {
	override val defaultState = SettingsState()

	override fun handleIntent(intent: BaseIntent.Default) {
		super.handleIntent(intent)
		if (intent is BaseIntent.Default.Init) {
			getDataCachingTimeUseCase.execute()
				.onEach { updateState { copy(cachingDays = it.toDays().toInt()) } }
				.launchIn(viewModelScope)

			getPlacesPackCountUseCase.execute()
				.onEach { updateState { copy(packCount = it) } }
				.launchIn(viewModelScope)

			getPlacesRadiusUseCase.execute()
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
					onApplyValue = { setDataCachingTimeUseCase.execute(Duration.ofDays(it.toLong())) }
				)
			}
			SettingsIntent.ClickPackCount -> {
				navigationManager.openInputDialog(
					title = resourceHelper.getString(R.string.pack_count),
					defaultValue = state.value.packCount,
					onApplyValue = { setPlacesPackCountUseCase.execute(it) }
				)
			}
			SettingsIntent.ClickRadius -> {
				navigationManager.openInputDialog(
					title = resourceHelper.getString(R.string.radius),
					defaultValue = state.value.radius,
					onApplyValue = { setPlacesRadiusUseCase.execute(it) }
				)
			}
		}
	}
}