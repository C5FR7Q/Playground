package com.github.c5fr7q.playground.presentation.ui.screen.blocked

import com.github.c5fr7q.playground.presentation.manager.NavigationManager
import com.github.c5fr7q.playground.presentation.ui.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import javax.inject.Inject

@HiltViewModel
class BlockedViewModel @Inject constructor(
	private val navigationManager: NavigationManager
) : BaseViewModel<BlockedState, BlockedIntent>() {
	override val mutableState = MutableStateFlow(BlockedState())

	override fun attach() {
		super.attach()
		produceIntent(BlockedIntent.Init)
	}

	override fun handleIntent(intent: BlockedIntent) {
		when (intent) {
			BlockedIntent.Init -> {
			}
			BlockedIntent.ClickBack -> {
				navigationManager.closeScreen()
			}
		}
	}

}