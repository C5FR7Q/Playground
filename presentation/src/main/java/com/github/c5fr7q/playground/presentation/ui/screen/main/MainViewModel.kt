package com.github.c5fr7q.playground.presentation.ui.screen.main

import androidx.lifecycle.viewModelScope
import com.github.c5fr7q.playground.domain.repository.PlaceRepository
import com.github.c5fr7q.playground.presentation.manager.NavigationManager
import com.github.c5fr7q.playground.presentation.ui.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
	private val placeRepository: PlaceRepository,
	private val navigationManager: NavigationManager
) : BaseViewModel<MainState, MainIntent>() {
	override val mutableState: MutableStateFlow<MainState> = MutableStateFlow(MainState())

	private val shouldUsePreviousPlaces = MutableStateFlow(true)

	override fun attach() {
		super.attach()
		produceIntent(MainIntent.Init)
	}

	override fun handleIntent(intent: MainIntent) {
		when (intent) {
			MainIntent.Init -> {
				if (state.value.places.isEmpty()) {
					updateState {
						copy(isLoading = true)
					}
					if (shouldUsePreviousPlaces.value) {
						viewModelScope.launch {
							val previousPlaces = placeRepository.getPreviousPlaces()
							if (previousPlaces.isNotEmpty()) {
								updateState {
									copy(places = previousPlaces, usesPreviousPlaces = true, isLoading = false)
								}
							} else {
								updateState { copy(isLoading = false) }
								// TODO: 21.05.2021 Display nothing prev found?
							}
						}
					}
				}
				shouldUsePreviousPlaces
					.flatMapLatest { if (!it) placeRepository.getPlaces() else emptyFlow() }
					.onEach {
						updateState {
							copy(
								isLoading = false,
								places = it,
								usesPreviousPlaces = false
								// TODO: 21.05.2021 Show empty results if need
							)
						}
					}.launchIn(viewModelScope)
			}
			MainIntent.LoadMore -> {
				if (!state.value.usesPreviousPlaces) {
					placeRepository.loadMorePlaces()
					updateState { copy(isLoading = true) }
				}
			}
			MainIntent.ClickLike -> TODO("")
			MainIntent.ClickSettings -> TODO("")
			MainIntent.ClickPrevious -> TODO("")
			MainIntent.ClickRefresh -> {
				viewModelScope.launch {
					val selectedCategories = state.value.selectedCategories
					if (selectedCategories.isNotEmpty()) {
						if (placeRepository.tryRefreshPlaces(selectedCategories)) {
							updateState { copy(isLoading = true) }
							shouldUsePreviousPlaces.value = false
						} else {
							// TODO: 21.05.2021 Display "You're too close"
						}
					}
				}
			}
			is MainIntent.ToggleCategory -> {
				updateState {
					val newCategories = selectedCategories.toMutableList().apply {
						intent.category.let {
							if (contains(it)) {
								remove(it)
							} else {
								add(it)
							}
						}
					}
					copy(selectedCategories = newCategories)
				}
			}
		}
	}
}