package com.github.c5fr7q.playground.presentation.ui.base

import androidx.annotation.CallSuper
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.c5fr7q.playground.presentation.manager.NavigationManager
import kotlinx.coroutines.cancelChildren
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

abstract class BaseViewModel<State, Intent : BaseIntent> : ViewModel() {
	@Inject
	lateinit var navigationManager: NavigationManager

	private val intentFlow = MutableSharedFlow<BaseIntent>()

	protected abstract val mutableState: MutableStateFlow<State>
	val state: StateFlow<State> get() = mutableState

	@Suppress("UNCHECKED_CAST")
	@CallSuper
	open fun attach() {
		viewModelScope.launch {
			intentFlow.collect { intent ->
				try {
					(intent as? BaseIntent.Default)?.let { handleIntent(intent) }
				} catch (ignored: Exception) {
				}
				try {
					(intent as? Intent)?.let { handleIntent(it) }
				} catch (ignored: Exception) {
				}
			}
		}
		produceIntent(BaseIntent.Default.Init)
	}

	@CallSuper
	open fun detach() {
		viewModelScope.coroutineContext.cancelChildren()
	}

	fun produceIntent(intent: Intent) {
		viewModelScope.launch {
			intentFlow.emit(intent)
		}
	}

	fun produceIntent(intent: BaseIntent.Default) {
		viewModelScope.launch {
			intentFlow.emit(intent)
		}
	}

	protected fun updateState(update: State.() -> State) {
		mutableState.value = mutableState.value.update()
	}

	protected open fun handleIntent(intent: Intent) {}

	protected open fun handleIntent(intent: BaseIntent.Default) {
		when (intent) {
			BaseIntent.Default.Init -> Unit
			BaseIntent.Default.ClickBack -> {
				navigationManager.closeScreen()
			}
		}
	}

}