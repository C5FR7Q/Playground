package com.github.c5fr7q.playground.presentation.ui.base

import androidx.annotation.CallSuper
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.c5fr7q.playground.presentation.manager.NavigationManager
import kotlinx.coroutines.cancelChildren
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@Suppress("UNCHECKED_CAST")
abstract class BaseViewModel<State, SideEffect, Intent : BaseIntent> : ViewModel() {
	@Inject
	lateinit var navigationManager: NavigationManager

	private val intentFlow = MutableSharedFlow<BaseIntent>()
	private val sideEffectFlow = MutableSharedFlow<SideEffect>()

	private val mutableState by lazy { MutableStateFlow(defaultState) }

	protected abstract val defaultState: State

	val state: StateFlow<State> get() = mutableState.asStateFlow()
	val sideEffect: SharedFlow<SideEffect> get() = sideEffectFlow.asSharedFlow()

	@CallSuper
	open fun attach() {
		viewModelScope.launch {
			intentFlow.collect { intent ->
				Timber.v("handleIntent: $intent")
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

	protected fun produceSideEffect(sideEffect: SideEffect) {
		Timber.v("produceSideEffect: $sideEffect")
		viewModelScope.launch {
			sideEffectFlow.emit(sideEffect)
		}
	}

	protected fun updateState(update: State.() -> State) {
		mutableState.value = mutableState.value
			.update()
			.also { Timber.v("updateState: $it") }
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