package com.github.c5fr7q.playground.presentation.ui.screen.common.selectable

import com.github.c5fr7q.playground.domain.entity.Place
import com.github.c5fr7q.playground.presentation.ui.base.BaseViewModel
import kotlinx.coroutines.flow.*

abstract class SelectionViewModel : BaseViewModel<SelectionState, Unit, SelectionIntent>() {
	abstract fun getPlaces(): Flow<List<Place>>
	abstract fun applySelection(places: List<Place>)

	init {
		getPlaces()
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
			.launchIfActive()
	}

	override fun handleIntent(intent: SelectionIntent) {
		when (intent) {
			SelectionIntent.ClickApplySelection -> {
				applySelection(state.value.selectedPlaces)
			}
			is SelectionIntent.ToggleCategory -> {
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
			is SelectionIntent.TogglePlaceSelection -> {
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