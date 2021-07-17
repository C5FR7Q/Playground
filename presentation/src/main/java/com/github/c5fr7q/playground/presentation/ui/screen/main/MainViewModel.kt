package com.github.c5fr7q.playground.presentation.ui.screen.main

import androidx.lifecycle.viewModelScope
import com.github.c5fr7q.playground.domain.entity.LoadPlacesStatus
import com.github.c5fr7q.playground.domain.usecase.*
import com.github.c5fr7q.playground.domain.usecase.place.*
import com.github.c5fr7q.playground.presentation.R
import com.github.c5fr7q.playground.presentation.manager.NetworkStateManager
import com.github.c5fr7q.playground.presentation.manager.PermissionManager
import com.github.c5fr7q.playground.presentation.ui.base.BaseIntent
import com.github.c5fr7q.playground.presentation.ui.base.BaseViewModel
import com.github.c5fr7q.util.ResourceHelper
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
	private val blockPlaceUseCase: BlockPlaceUseCase,
	private val dislikePlaceUseCase: DislikePlaceUseCase,
	private val getAvailablePlacesForCategoriesUseCase: GetAvailablePlacesForCategoriesUseCase,
	private val getFavoritePlacesForCategoriesUseCase: GetFavoritePlacesForCategoriesUseCase,
	private val getLoadPlacesStatusUseCase: GetLoadPlacesStatusUseCase,
	private val likePlaceUseCase: LikePlaceUseCase,
	private val loadMorePlacesUseCase: LoadMorePlacesUseCase,
	private val reloadPlacesUseCase: ReloadPlacesUseCase,
	private val permissionManager: PermissionManager,
	private val networkStateManager: NetworkStateManager,
	private val resourceHelper: ResourceHelper,
) : BaseViewModel<MainState, MainSideEffect, MainIntent>() {
	private var refreshing = false

	override fun handleIntent(intent: BaseIntent.Default) {
		if (intent is BaseIntent.Default.Init) {
			if (state.value.places.isEmpty()) {
				updateState {
					copy(isLoading = true)
				}
			}

			state
				.map { it.likedOnly }
				.distinctUntilChanged()
				.shareIn(viewModelScope, SharingStarted.WhileSubscribed())
				.onEach { produceSideEffect(MainSideEffect.ScrollToTop) }
				.launchIn(viewModelScope)

			getLoadPlacesStatusUseCase.execute()
				.map { it == LoadPlacesStatus.LOADING }
				.onEach { updateState { copy(isLoading = it) } }
				.launchIn(viewModelScope)

			getLoadPlacesStatusUseCase.execute()
				.filter { it == LoadPlacesStatus.FAILED }
				.shareIn(viewModelScope, SharingStarted.WhileSubscribed())
				.onEach { produceSideEffect(MainSideEffect.ShowError(resourceHelper.getString(R.string.something_went_wrong))) }
				.launchIn(viewModelScope)

			getLoadPlacesStatusUseCase.execute()
				.filter { it == LoadPlacesStatus.LOADED && refreshing }
				.shareIn(viewModelScope, SharingStarted.WhileSubscribed())
				.onEach { updateState { copy(likedOnly = false) } }
				.launchIn(viewModelScope)

			state.map { it.selectedCategories to it.likedOnly }
				.distinctUntilChanged()
				.flatMapLatest { (categories, likedOnly) ->
					if (likedOnly) {
						getFavoritePlacesForCategoriesUseCase.execute(categories)
					} else {
						getAvailablePlacesForCategoriesUseCase.execute(categories)
					}
				}
				.onEach {
					updateState {
						copy(
							places = it,
							likedOnly = if (it.isEmpty()) false else likedOnly
						)
					}
				}
				.launchIn(viewModelScope)
		}
	}

	override fun handleIntent(intent: MainIntent) {
		when (intent) {
			MainIntent.LoadMore -> {
				refreshing = false
				loadMorePlacesUseCase.execute()
			}
			MainIntent.ClickSettings -> {
				navigationManager.openSettings()
			}
			MainIntent.ClickRefresh -> {
				viewModelScope.launch { // TODO: 16.07.2021 Use case candidate?
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
									refreshing = true
									reloadPlacesUseCase.execute(selectedCategories)
								}
							}
						}
						.launchIn(viewModelScope)
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
						dislikePlaceUseCase.execute(this)
					} else {
						likePlaceUseCase.execute(this)
					}
				}
			}
			is MainIntent.ClickBlock -> {
				blockPlaceUseCase.execute(intent.place)
			}
			is MainIntent.ClickShowInMaps -> {
				navigationManager.openMaps(intent.place.position)
			}
		}
	}
}