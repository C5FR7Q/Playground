package com.github.c5fr7q.playground.presentation.ui.util

import androidx.annotation.CallSuper
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.cancelChildren
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

abstract class BaseViewModel<State> : ViewModel() {
	protected abstract val mutableState: MutableStateFlow<State>
	val state: StateFlow<State> get() = mutableState

	protected fun updateState(update: State.() -> State) {
		mutableState.value = mutableState.value.update()
	}

	open fun attach() {
	}

	@CallSuper
	open fun detach() {
		viewModelScope.coroutineContext.cancelChildren()
	}

}