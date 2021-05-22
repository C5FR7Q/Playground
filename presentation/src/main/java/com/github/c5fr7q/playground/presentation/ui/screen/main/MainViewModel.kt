package com.github.c5fr7q.playground.presentation.ui.screen.main

import android.util.Log
import androidx.lifecycle.viewModelScope
import com.github.c5fr7q.playground.domain.entity.PlacesStatus
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
				}
				shouldUsePreviousPlaces
					.flatMapLatest { if (it) flow { emit(placeRepository.getPreviousPlaces()) } else emptyFlow() }
					.onEach {
						if (it.isNotEmpty()) {
							updateState {
								copy(places = it, usesPreviousPlaces = true, isLoading = false)
							}
						} else {
							updateState { copy(isLoading = false) }
							// TODO: 21.05.2021 Display nothing prev found?
						}
					}.launchIn(viewModelScope)

				shouldUsePreviousPlaces
					.flatMapLatest { if (it) emptyFlow() else placeRepository.getPlacesStatus() }
					.combine(placeRepository.getPlaces()) { status, places -> status to places }
					.onEach { (status, places) ->
						when (status) {
							PlacesStatus.LOADED -> {
								updateState {
									copy(
										isLoading = false,
										places = places,
										usesPreviousPlaces = false
										// TODO: 21.05.2021 Show empty results if need
									)
								}
							}
							PlacesStatus.FAILED -> {
								updateState { copy(isLoading = false) }
								// TODO: 21.05.2021 Show something went wrong
							}
							PlacesStatus.LOADING -> Unit
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
			MainIntent.ClickPrevious -> {
				shouldUsePreviousPlaces.value = true
			}
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