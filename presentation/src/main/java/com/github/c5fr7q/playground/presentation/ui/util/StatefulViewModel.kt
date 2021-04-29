package com.github.c5fr7q.playground.presentation.ui.util

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

abstract class StatefulViewModel<State> : ViewModel() {
	protected abstract val mutableState: MutableStateFlow<State>
	val state: StateFlow<State> get() = mutableState

	protected fun updateState(update: State.() -> State) {
		mutableState.value = mutableState.value.update()
	}

}