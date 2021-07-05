package com.github.c5fr7q.playground.presentation.ui.screen.main

import androidx.lifecycle.viewModelScope
import com.github.c5fr7q.playground.domain.entity.UpdatedPlacesStatus
import com.github.c5fr7q.playground.domain.repository.PlaceRepository
import com.github.c5fr7q.playground.presentation.R
import com.github.c5fr7q.playground.presentation.manager.NetworkStateManager
import com.github.c5fr7q.playground.presentation.manager.PermissionManager
import com.github.c5fr7q.playground.presentation.ui.base.BaseIntent
import com.github.c5fr7q.playground.presentation.ui.base.BaseViewModel
import com.github.c5fr7q.util.ResourceHelper
import com.github.c5fr7q.util.flatMapLatestOnTrue
import com.github.c5fr7q.util.flatMapLatestWith
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
	private val placeRepository: PlaceRepository,
	private val permissionManager: PermissionManager,
	private val networkStateManager: NetworkStateManager,
	private val resourceHelper: ResourceHelper,
) : BaseViewModel<MainState, MainSideEffect, MainIntent>() {
	private val placesSource = MutableStateFlow(MainState.ContentType.PREVIOUS)

	override fun handleIntent(intent: BaseIntent.Default) {
		if (intent is BaseIntent.Default.Init) {
			if (state.value.places.isEmpty()) {
				updateState {
					copy(isLoading = true)
				}
			}

			placeRepository
				.getBlockedPlaces()
				.map { it.isNotEmpty() }
				.distinctUntilChanged()
				.onEach { updateState { copy(hasBlockedPlaces = it) } }
				.launchIn(viewModelScope)

			placesSource
				.map { it == MainState.ContentType.PREVIOUS }
				.distinctUntilChanged()
				.flatMapLatestOnTrue(
					placeRepository
						.getPreviousPlaces()
						.flatMapLatest { places ->
							state
								.map { it.selectedCategories }
								.distinctUntilChanged()
								.map { categories -> places.filter { it.categories.containsAll(categories) } }
						})
				.onEach {
					updateState {
						if (contentType != MainState.ContentType.PREVIOUS) {
							produceSideEffect(MainSideEffect.ScrollToTop)
						}
						copy(places = it, contentType = MainState.ContentType.PREVIOUS, isLoading = false)
					}
				}.launchIn(viewModelScope)

			placesSource
				.map { it == MainState.ContentType.FAVORITE }
				.distinctUntilChanged()
				.flatMapLatestOnTrue(
					placeRepository
						.getFavoritePlaces()
						.flatMapLatest { places ->
							state
								.map { it.selectedCategories }
								.distinctUntilChanged()
								.map { categories -> places.filter { it.categories.containsAll(categories) } }
						}
				)
				.onEach {
					updateState {
						if (contentType != MainState.ContentType.FAVORITE) {
							produceSideEffect(MainSideEffect.ScrollToTop)
						}
						copy(places = it, contentType = MainState.ContentType.FAVORITE, isLoading = false)
					}
				}.launchIn(viewModelScope)

			placesSource
				.map { it == MainState.ContentType.NEAR }
				.distinctUntilChanged()
				.flatMapLatestOnTrue(
					placeRepository
						.getUpdatedPlacesStatus()
						.flatMapLatestWith(placeRepository.getUpdatedPlaces())
				)
				.onEach { (status, places) ->
					when (status) {
						UpdatedPlacesStatus.LOADED -> {
							updateState {
								if (contentType != MainState.ContentType.NEAR) {
									produceSideEffect(MainSideEffect.ScrollToTop)
								}
								copy(
									isLoading = false,
									places = places,
									contentType = MainState.ContentType.NEAR
								)
							}
						}
						UpdatedPlacesStatus.FAILED -> {
							updateState { copy(isLoading = false) }
							placesSource.value = state.value.contentType
							produceSideEffect(MainSideEffect.ShowError(resourceHelper.getString(R.string.something_went_wrong)))
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
					networkStateManager.isConnected
						.take(1).onEach { hasNetworkConnection ->
							if (!hasNetworkConnection) {
								produceSideEffect(MainSideEffect.ShowError(resourceHelper.getString(R.string.no_internet_connection)))
								return@onEach
							}
							val selectedCategories = state.value.selectedCategories
							if (selectedCategories.isNotEmpty()) {
								val permissionsGranted = permissionManager.requestPermissions(
									permissions = arrayOf(
										android.Manifest.permission.ACCESS_FINE_LOCATION,
										android.Manifest.permission.ACCESS_COARSE_LOCATION
									),
									rationaleMessage = resourceHelper.getString(R.string.refresh_rationale_message),
									createFallbackMessage = { resourceHelper.getString(R.string.refresh_fallback_message) }
								)

								if (permissionsGranted) {
									placeRepository.updatePlaces(selectedCategories)
									updateState { copy(isLoading = true) }
									placesSource.value = MainState.ContentType.NEAR
								}
							}
						}
						.launchIn(viewModelScope)
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
			is MainIntent.ClickBlock -> {
				placeRepository.blockPlace(intent.place)
			}
			is MainIntent.ClickShowInMaps -> {
				navigationManager.openMaps(intent.place.position)
			}
		}
	}
}