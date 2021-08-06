package com.github.c5fr7q.playground.presentation.ui.base

import androidx.annotation.CallSuper
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import timber.log.Timber

@Suppress("UNCHECKED_CAST")
abstract class BaseViewModel<State, SideEffect, Intent> : ViewModel() {
	private val intentFlow = MutableSharedFlow<Intent>()
	private val sideEffectFlow = MutableSharedFlow<SideEffect>()

	private val mutableState by lazy { MutableStateFlow(defaultState) }

	protected abstract val defaultState: State

	val state: StateFlow<State> get() = mutableState.asStateFlow()
	val sideEffect: SharedFlow<SideEffect> get() = sideEffectFlow.asSharedFlow()

	private val isActive = MutableStateFlow(false)

	init {
		intentFlow
			.onEach { intent ->
				Timber.v("handleIntent: $intent")
				handleIntent(intent)
			}
			.launchIfActive()
	}

	@CallSuper
	open fun attach() {
		isActive.value = true
	}

	@CallSuper
	open fun detach() {
		isActive.value = false
	}

	fun produceIntent(intent: Intent) {
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
		mutableState.value = mutableState.value.update()
//			.also { Timber.v("updateState: $it") }
	}

	protected open fun handleIntent(intent: Intent) {}

	protected fun <T> Flow<T>.launchIfActive(): Job = viewModelScope.launch {
		isActive.flatMapLatest { active ->
			if (active) {
				this@launchIfActive
			} else {
				emptyFlow()
			}
		}.collect()
	}

}