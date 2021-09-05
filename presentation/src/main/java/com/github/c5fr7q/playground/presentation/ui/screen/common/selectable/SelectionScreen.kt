package com.github.c5fr7q.playground.presentation.ui.screen.common.selectable

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.github.c5fr7q.playground.domain.entity.Place
import com.github.c5fr7q.playground.presentation.R
import com.github.c5fr7q.playground.presentation.ui.widget.DefaultTopAppBar
import com.github.c5fr7q.playground.presentation.ui.widget.SelectCategoriesRow
import com.github.c5fr7q.playground.presentation.ui.widget.SelectablePlaceItem
import kotlinx.coroutines.flow.StateFlow

@Composable
fun SelectionScreen(
	@DrawableRes applySelectionIconResId: Int,
	@StringRes titleResId: Int,
	viewModel: SelectionViewModel
) {
	SelectionScreen(
		applySelectionIconResId = applySelectionIconResId,
		titleResId = titleResId,
		stateFlow = viewModel.state,
		onApplySelectionClick = { viewModel.produceIntent(SelectionIntent.ClickApplySelection) },
		onCategoryToggle = { viewModel.produceIntent(SelectionIntent.ToggleCategory(it)) },
		onPlaceSelectionToggle = { viewModel.produceIntent(SelectionIntent.TogglePlaceSelection(it)) }
	)
}

@Composable
private fun SelectionScreen(
	@DrawableRes applySelectionIconResId: Int,
	@StringRes titleResId: Int,
	stateFlow: StateFlow<SelectionState>,
	onApplySelectionClick: () -> Unit,
	onCategoryToggle: (Place.Category) -> Unit,
	onPlaceSelectionToggle: (Place) -> Unit
) {
	val state by stateFlow.collectAsState()

	Scaffold(
		topBar = {
			TopBar(
				applySelectionIconResId = applySelectionIconResId,
				titleResId = titleResId,
				placesSelected = state.selectedPlaces.isNotEmpty(),
				selectedCategories = state.selectedCategories,
				onApplySelectionClick = onApplySelectionClick,
				onCategoryToggle = onCategoryToggle
			)
		}
	) { innerPadding ->
		Box(
			modifier = Modifier
				.padding(innerPadding)
				.fillMaxSize()
		) {
			if (state.places.isEmpty()) {
				Text(
					modifier = Modifier.align(Alignment.Center),
					text = stringResource(id = R.string.nothing_found),
					style = MaterialTheme.typography.h5
				)
			} else {
				LazyColumn {
					items(state.places) { place ->
						key(place.id) {
							SelectablePlaceItem(
								place = place,
								isSelected = state.selectedPlaces.contains(place),
								onPlaceClick = { onPlaceSelectionToggle(place) }
							)
						}
					}
				}
			}
		}
	}
}

@Composable
private fun TopBar(
	@DrawableRes applySelectionIconResId: Int,
	@StringRes titleResId: Int,
	placesSelected: Boolean,
	selectedCategories: List<Place.Category>,
	onApplySelectionClick: () -> Unit,
	onCategoryToggle: (Place.Category) -> Unit
) {
	var filterIsActive by remember { mutableStateOf(true) }
	MaterialTheme(
		colors = MaterialTheme.colors.copy(
			secondaryVariant = animateColorAsState(if (placesSelected) MaterialTheme.colors.secondary else MaterialTheme.colors.secondaryVariant).value
		)
	) {
		Column(
			modifier = Modifier
				.background(color = MaterialTheme.colors.surface)
				.animateContentSize()
		) {
			DefaultTopAppBar(
				titleResId = titleResId,
				actions = {
					IconButton(onClick = { filterIsActive = !filterIsActive }) {
						Icon(painter = painterResource(id = R.drawable.ic_filter_list_24), contentDescription = null)
					}
					if (placesSelected) {
						IconButton(onClick = onApplySelectionClick) {
							Icon(painter = painterResource(id = applySelectionIconResId), contentDescription = null)
						}
					}
				},
				backgroundColor = Color.Transparent,
				contentColor = MaterialTheme.colors.onSurface,
				elevation = 0.dp,
			)
			Divider()
			if (filterIsActive) {
				SelectCategoriesRow(selectedCategories = selectedCategories, onCategoryToggle = onCategoryToggle)
				Divider()
			}
		}
	}
}

