package com.github.c5fr7q.playground.presentation.ui.base

import androidx.annotation.CallSuper
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.cancelChildren
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

abstract class BaseViewModel<State, Intent> : ViewModel() {
	private val intentChannel = Channel<Intent>(Channel.UNLIMITED)

	protected abstract val mutableState: MutableStateFlow<State>
	val state: StateFlow<State> get() = mutableState

	@CallSuper
	open fun attach() {
		viewModelScope.launch {
			intentChannel
				.receiveAsFlow()
				.collect { handleIntent(it) }
		}
	}

	@CallSuper
	open fun detach() {
		viewModelScope.coroutineContext.cancelChildren()
	}

	fun produceIntent(intent: Intent) {
		viewModelScope.launch {
			intentChannel.send(intent)
		}
	}

	protected fun updateState(update: State.() -> State) {
		mutableState.value = mutableState.value.update()
	}

	protected open fun handleIntent(intent: Intent) {}

}