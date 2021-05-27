package com.github.c5fr7q.playground.presentation.ui.screen.main

import androidx.lifecycle.viewModelScope
import com.github.c5fr7q.playground.domain.entity.UpdatedPlacesStatus
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

	private val placesSource = MutableStateFlow(MainState.ContentType.PREVIOUS)

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
				placesSource
					.map { it == MainState.ContentType.PREVIOUS }
					.distinctUntilChanged()
					.flatMapLatest { if (it) placeRepository.getPreviousPlaces() else emptyFlow() }
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
					.flatMapLatest { if (it) placeRepository.getFavoritePlaces() else emptyFlow() }
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
					.flatMapLatest { if (it) placeRepository.getUpdatedPlacesStatus() else emptyFlow() }
					.combine(placeRepository.getUpdatedPlaces()) { status, places -> status to places }
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
			MainIntent.LoadMore -> {
				if (state.value.contentType == MainState.ContentType.NEAR) {
					placeRepository.loadMorePlaces()
					updateState { copy(isLoading = true) }
				}
			}
			MainIntent.ClickLike -> {
				placesSource.value = MainState.ContentType.FAVORITE
			}
			MainIntent.ClickSettings -> TODO("")
			MainIntent.ClickPrevious -> {
				placesSource.value = MainState.ContentType.PREVIOUS
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