package com.github.c5fr7q.playground.presentation.ui.screen.main

import androidx.lifecycle.viewModelScope
import com.github.c5fr7q.playground.domain.entity.Place
import com.github.c5fr7q.playground.domain.repository.PlaceRepository
import com.github.c5fr7q.playground.presentation.manager.NavigationManager
import com.github.c5fr7q.playground.presentation.ui.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
	private val placeRepository: PlaceRepository,
	private val navigationManager: NavigationManager
) : BaseViewModel<MainState, MainIntent>() {
	override val mutableState: MutableStateFlow<MainState> = MutableStateFlow(MainState())

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
				}

				placeRepository.getPreviousPlaces()
					.take(1)
					.onEach {
						if (it.isNotEmpty()) {
							updateState {
								copy(places = it, shouldUsePreviousPlaces = true, usesPreviousPlaces = true, isLoading = false)
							}
						} else {
							updateState { copy(isLoading = false) }
						}
					}
					.launchIn(viewModelScope)

				state.map { it.shouldUsePreviousPlaces to it.selectedCategories }
					.distinctUntilChanged()
					.flatMapLatest { (shouldUsePreviousPlaces, selectedCategories) ->
						if (!shouldUsePreviousPlaces /* && selectedCategories.isNotEmpty()*/) {
//								placeRepository.getPlaces(selectedCategories)
							placeRepository.getPlaces(listOf(Place.Category.EATING))
						} else {
							emptyFlow()
						}
					}
					.onEach {
						if (it.isNotEmpty()) {
							updateState {
								copy(
									isLoading = false,
									places = it,
									usesPreviousPlaces = false
								)
							}
						}
					}
					.launchIn(viewModelScope)
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
				// TODO: 21.05.2021 Request update according to current Position
				updateState {
					if (usesPreviousPlaces) {
						copy(shouldUsePreviousPlaces = false, isLoading = true)
					} else {
						this
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