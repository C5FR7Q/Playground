package com.github.c5fr7q.playground.presentation.ui.screen.fancy

import androidx.lifecycle.viewModelScope
import com.github.c5fr7q.playground.domain.repository.FancyRepository
import com.github.c5fr7q.playground.presentation.manager.NavigationManager
import com.github.c5fr7q.playground.presentation.ui.util.StatefulViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.take
import javax.inject.Inject

@HiltViewModel
class FancyViewModel @Inject constructor(
	private val fancyRepository: FancyRepository,
	private val navigationManager: NavigationManager
) : StatefulViewModel<FancyState>() {
	override val mutableState: MutableStateFlow<FancyState> = MutableStateFlow(FancyState())

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

	fun onItemClicked(userId: String) {
		navigationManager.openProfile(userId)
	}
}