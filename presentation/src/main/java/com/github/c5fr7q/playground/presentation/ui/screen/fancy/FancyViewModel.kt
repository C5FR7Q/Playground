package com.github.c5fr7q.playground.presentation.ui.screen.fancy

import androidx.lifecycle.viewModelScope
import com.github.c5fr7q.playground.presentation.manager.NavigationManager
import com.github.c5fr7q.playground.presentation.ui.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import javax.inject.Inject

//@HiltViewModel
class FancyViewModel /*@Inject constructor(
	private val fancyRepository: FancyRepository,
	private val userRepository: UserRepository,
	private val navigationManager: NavigationManager
) : BaseViewModel<FancyState, FancyIntent>() {
	override val mutableState: MutableStateFlow<FancyState> = MutableStateFlow(FancyState())

	override fun attach() {
		super.attach()
		produceIntent(FancyIntent.Init)
	}

	override fun handleIntent(intent: FancyIntent) {
		when (intent) {
			is FancyIntent.Init -> {
				fancyRepository.getFancyData()
					.onEach { updateState { copy(name = it.name) } }
					.launchIn(viewModelScope)

				fancyRepository.getNumbersList()
					.combine(
						userRepository.getAllUsers().map { list -> list.map { "${it.name}___${it.age}" } }
					) { numbers, users -> users + numbers }
					.onEach { updateState { copy(dataList = it) } }
					.launchIn(viewModelScope)

				fancyRepository.getCount().take(1)
					.onEach { updateState { copy(currentResult = it) } }
					.launchIn(viewModelScope)
			}
			is FancyIntent.ClickItem -> {
				userRepository.addUser(intent.userId)
				navigationManager.openProfile(intent.userId)
			}
			is FancyIntent.LoadMore -> {
				fancyRepository.requestMoreNumbers()
			}
		}
	}
}*/