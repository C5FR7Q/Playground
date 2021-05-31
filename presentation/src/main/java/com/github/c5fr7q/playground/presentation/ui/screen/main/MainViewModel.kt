package com.github.c5fr7q.playground.presentation.ui.screen.main

import androidx.lifecycle.viewModelScope
import com.github.c5fr7q.playground.domain.entity.UpdatedPlacesStatus
import com.github.c5fr7q.playground.domain.repository.PlaceRepository
import com.github.c5fr7q.playground.presentation.ui.base.BaseIntent
import com.github.c5fr7q.playground.presentation.ui.base.BaseViewModel
import com.github.c5fr7q.playground.presentation.util.combine
import com.github.c5fr7q.playground.presentation.util.flatMapLatestOnTrue
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
	private val placeRepository: PlaceRepository
) : BaseViewModel<MainState, MainIntent>() {
	private val placesSource = MutableStateFlow(MainState.ContentType.PREVIOUS)

	override fun handleIntent(intent: BaseIntent.Default) {
		if (intent is BaseIntent.Default.Init) {
			if (state.value.places.isEmpty()) {
				updateState {
					copy(isLoading = true)
				}
			}
			placesSource
				.map { it == MainState.ContentType.PREVIOUS }
				.distinctUntilChanged()
				.flatMapLatestOnTrue(placeRepository.getPreviousPlaces())
				.flatMapLatest { places ->
					state
						.map { it.selectedCategories }
						.distinctUntilChanged()
						.map { categories -> places.filter { it.categories.containsAll(categories) } }
				}
				.onEach {
					updateState {
						copy(places = it, contentType = MainState.ContentType.PREVIOUS, isLoading = false)
					}
					// TODO: 21.05.2021 Display nothing prev found?
				}.launchIn(viewModelScope)

			placesSource
				.map { it == MainState.ContentType.FAVORITE }
				.distinctUntilChanged()
				.flatMapLatestOnTrue(placeRepository.getFavoritePlaces())
				.flatMapLatest { places ->
					state
						.map { it.selectedCategories }
						.distinctUntilChanged()
						.map { categories -> places.filter { it.categories.containsAll(categories) } }
				}
				.onEach {
					updateState {
						copy(places = it, contentType = MainState.ContentType.FAVORITE, isLoading = false)
					}
					// TODO: 21.05.2021 Display nothing favorite found?
				}.launchIn(viewModelScope)

			placesSource
				.map { it == MainState.ContentType.NEAR }
				.distinctUntilChanged()
				.flatMapLatestOnTrue(placeRepository.getUpdatedPlacesStatus())
				.combine(placeRepository.getUpdatedPlaces())
				.onEach { (status, places) ->
					when (status) {
						UpdatedPlacesStatus.LOADED -> {
							updateState {
								copy(
									isLoading = false,
									places = places,
									contentType = MainState.ContentType.NEAR
									// TODO: 21.05.2021 Show empty results if need
								)
							}
						}
						UpdatedPlacesStatus.FAILED -> {
							updateState { copy(isLoading = false) }
							placesSource.value = state.value.contentType
							// TODO: 21.05.2021 Show something went wrong
						}
						UpdatedPlacesStatus.LOADING -> Unit
					}
				}.launchIn(viewModelScope)
		}
	}

	override fun handleIntent(intent: MainIntent) {
		when (intent) {
			MainIntent.LoadMore -> {
				if (state.value.contentType == MainState.ContentType.NEAR) {
					placeRepository.loadMorePlaces()
					updateState { copy(isLoading = true) }
				}
			}
			MainIntent.ClickLike -> {
				placesSource.value = MainState.ContentType.FAVORITE
			}
			MainIntent.ClickSettings -> {
				navigationManager.openSettings()
			}
			MainIntent.ClickPrevious -> {
				placesSource.value = MainState.ContentType.PREVIOUS
			}
			MainIntent.ClickBlocked -> {
				navigationManager.openBlocked()
			}
			MainIntent.ClickRefresh -> {
				viewModelScope.launch {
					val selectedCategories = state.value.selectedCategories
					if (selectedCategories.isNotEmpty()) {
						placeRepository.updatePlaces(selectedCategories)
						updateState { copy(isLoading = true) }
						placesSource.value = MainState.ContentType.NEAR
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
			is MainIntent.ToggleItemFavorite -> {
				placeRepository.toggleFavoriteState(intent.place)
			}
		}
	}
}