package com.github.c5fr7q.playground.presentation.ui.screen.blocked

import androidx.lifecycle.viewModelScope
import com.github.c5fr7q.playground.domain.usecase.place.GetBlockedPlacesUseCase
import com.github.c5fr7q.playground.domain.usecase.place.UnblockPlacesUseCase
import com.github.c5fr7q.playground.presentation.ui.base.BaseIntent
import com.github.c5fr7q.playground.presentation.ui.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import javax.inject.Inject

@HiltViewModel
class BlockedViewModel @Inject constructor(
	private val getBlockedPlacesUseCase: GetBlockedPlacesUseCase,
	private val unblockPlacesUseCase: UnblockPlacesUseCase
) : BaseViewModel<BlockedState, Unit, BlockedIntent>() {
	override fun handleIntent(intent: BaseIntent.Default) {
		super.handleIntent(intent)
		if (intent is BaseIntent.Default.Init) {
			getBlockedPlacesUseCase
				.execute()
				.flatMapLatest { places ->
					state
						.map { it.selectedCategories }
						.distinctUntilChanged()
						.map { categories -> places.filter { it.categories.containsAll(categories) } }
				}
				.onEach { newPlaces ->
					updateState {
						val newPlacesIds = newPlaces.map { it.id }
						val newSelectedPlaces = selectedPlaces.filter { newPlacesIds.contains(it.id) }
						copy(
							places = newPlaces,
							selectedPlaces = newSelectedPlaces
						)
					}
				}
				.launchIn(viewModelScope)
		}
	}

	override fun handleIntent(intent: BlockedIntent) {
		when (intent) {
			BlockedIntent.ClickDelete -> {
				unblockPlacesUseCase.execute(state.value.selectedPlaces)
			}
			is BlockedIntent.ToggleCategory -> {
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
			is BlockedIntent.TogglePlaceSelection -> {
				updateState {
					val newPlaces = selectedPlaces.toMutableList().apply {
						intent.place.let {
							if (contains(it)) {
								remove(it)
							} else {
								add(it)
							}
						}
					}
					copy(selectedPlaces = newPlaces)
				}
			}
		}
	}
}