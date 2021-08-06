package com.github.c5fr7q.playground.presentation.ui.screen.main

import androidx.lifecycle.viewModelScope
import com.github.c5fr7q.playground.domain.usecase.*
import com.github.c5fr7q.playground.domain.usecase.place.*
import com.github.c5fr7q.playground.presentation.R
import com.github.c5fr7q.playground.presentation.manager.NavigationManager
import com.github.c5fr7q.playground.presentation.manager.NetworkStateManager
import com.github.c5fr7q.playground.presentation.manager.PermissionManager
import com.github.c5fr7q.playground.presentation.ui.base.BaseViewModel
import com.github.c5fr7q.util.ResourceHelper
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
	private val blockPlace: BlockPlaceUseCase,
	private val dislikePlaces: DislikePlacesUseCase,
	private val getAvailablePlacesForCategories: GetAvailablePlacesForCategoriesUseCase,
	private val getFavoritePlacesForCategories: GetFavoritePlacesForCategoriesUseCase,
	getPlacesLoadState: GetPlacesLoadStateUseCase,
	private val likePlace: LikePlaceUseCase,
	private val loadMorePlaces: LoadMorePlacesUseCase,
	private val reloadPlaces: ReloadPlacesUseCase,
	private val permissionManager: PermissionManager,
	private val networkStateManager: NetworkStateManager,
	private val navigationManager: NavigationManager,
	private val resourceHelper: ResourceHelper,
) : BaseViewModel<MainState, MainSideEffect, MainIntent>() {
	private var refreshing = false
	override val defaultState = MainState()

	init {
		val placesLoadState = getPlacesLoadState().shareIn(viewModelScope, SharingStarted.Lazily)

		placesLoadState
			.map { it.isLoading() }
			.onEach { updateState { copy(isLoading = it) } }
			.launchIfActive()

		placesLoadState
			.filter { it.isFailed() }
			.shareIn(viewModelScope, SharingStarted.Lazily)
			.onEach { produceSideEffect(MainSideEffect.ShowError(resourceHelper.getString(R.string.something_went_wrong))) }
			.launchIfActive()

		placesLoadState
			.filter { it.isSucceed() && refreshing }
			.shareIn(viewModelScope, SharingStarted.Lazily)
			.onEach { updateState { copy(likedOnly = false) } }
			.launchIfActive()

		state
			.map { it.likedOnly }
			.distinctUntilChanged()
			.shareIn(viewModelScope, SharingStarted.Lazily)
			.onEach { produceSideEffect(MainSideEffect.ScrollToTop) }
			.launchIfActive()

		state
			.map { it.selectedCategories }
			.distinctUntilChanged()
			.shareIn(viewModelScope, SharingStarted.Lazily)
			.onEach { produceSideEffect(MainSideEffect.ScrollToTop) }
			.launchIfActive()

		state.map { it.selectedCategories to it.likedOnly }
			.distinctUntilChanged()
			.flatMapLatest { (categories, likedOnly) ->
				if (likedOnly) {
					getFavoritePlacesForCategories(categories)
				} else {
					getAvailablePlacesForCategories(categories)
				}
			}
			.distinctUntilChanged()
			.onEach {
				updateState {
					copy(
						places = it,
						likedOnly = if (it.isEmpty()) false else likedOnly
					)
				}
			}
			.launchIfActive()
	}

	override fun handleIntent(intent: MainIntent) {
		when (intent) {
			MainIntent.LoadMore -> {
				refreshing = false
				loadMorePlaces()
			}
			MainIntent.ClickSettings -> {
				navigationManager.openSettings()
			}
			MainIntent.ClickRefresh -> {
				viewModelScope.launch { // TODO: 16.07.2021 Use case candidate?
					val hasNetworkConnection = networkStateManager.isConnected.first()

					if (!hasNetworkConnection) {
						produceSideEffect(MainSideEffect.ShowError(resourceHelper.getString(R.string.no_internet_connection)))
						return@launch
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
							refreshing = true
							reloadPlaces(selectedCategories)
						}
					}
				}
			}
			MainIntent.ClickLike -> {
				updateState { copy(likedOnly = !likedOnly) }
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
				intent.place.run {
					if (isFavorite) {
						dislikePlaces(listOf(this))
					} else {
						likePlace(this)
					}
				}
			}
			is MainIntent.ClickBlock -> {
				blockPlace(intent.place)
			}
			is MainIntent.ClickShowInMaps -> {
				navigationManager.openMaps(intent.place.position)
			}
		}
	}
}