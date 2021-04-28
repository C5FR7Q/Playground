package com.github.c5fr7q.playground.presentation.ui.fancy

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.c5fr7q.playground.domain.repository.FancyRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import javax.inject.Inject

@HiltViewModel
class FancyViewModel @Inject constructor(
	private val savedStateHandle: SavedStateHandle,
	private val fancyRepository: FancyRepository
) : ViewModel() {

	private val _state: MutableStateFlow<FancyState> = MutableStateFlow(FancyState.Default)
	val state: StateFlow<FancyState> = _state

	init {
		fancyRepository.getFancyData()
			.onEach { updateState { copy(name = it.name) } }
			.launchIn(viewModelScope)

		fancyRepository.getNumbersList()
			.onEach { updateState { copy(numbers = it) } }
			.launchIn(viewModelScope)

		fancyRepository.getCount().take(1)
			.onEach { updateState { copy(currentResult = it) } }
			.launchIn(viewModelScope)
	}

	fun loadMore() {
		fancyRepository.requestMoreNumbers()
	}

	private fun updateState(update: FancyState.() -> FancyState) {
		_state.value = _state.value.update()
	}
}