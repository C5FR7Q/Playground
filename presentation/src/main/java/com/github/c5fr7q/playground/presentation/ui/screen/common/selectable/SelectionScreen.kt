package com.github.c5fr7q.playground.presentation.ui.screen.common.selectable

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
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
import com.github.c5fr7q.playground.presentation.ui.base.BaseIntent
import com.github.c5fr7q.playground.presentation.ui.widget.SelectCategoriesRow
import com.github.c5fr7q.playground.presentation.ui.widget.SelectablePlaceItem
import com.google.accompanist.insets.statusBarsHeight

@Composable
fun SelectionScreen(
	@DrawableRes applySelectionIconResId: Int,
	@StringRes titleResId: Int,
	viewModel: SelectionViewModel
) {
	val state by viewModel.state.collectAsState()
	SelectionScreen(
		applySelectionIconResId = applySelectionIconResId,
		titleResId = titleResId,
		state = state,
		onBackClick = { viewModel.produceIntent(BaseIntent.Default.ClickBack) },
		onApplySelectionClick = { viewModel.produceIntent(SelectionIntent.ClickApplySelection) },
		onCategoryToggle = { viewModel.produceIntent(SelectionIntent.ToggleCategory(it)) },
		onPlaceSelectionToggle = { viewModel.produceIntent(SelectionIntent.TogglePlaceSelection(it)) }
	)
}

@Composable
private fun SelectionScreen(
	@DrawableRes applySelectionIconResId: Int,
	@StringRes titleResId: Int,
	state: SelectionState,
	onBackClick: () -> Unit,
	onApplySelectionClick: () -> Unit,
	onCategoryToggle: (Place.Category) -> Unit,
	onPlaceSelectionToggle: (Place) -> Unit
) {
	Scaffold(
		topBar = {
			TopBar(
				applySelectionIconResId = applySelectionIconResId,
				titleResId = titleResId,
				placesSelected = state.selectedPlaces.isNotEmpty(),
				selectedCategories = state.selectedCategories,
				onBackClick = onBackClick,
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
	onBackClick: () -> Unit,
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
			Box(
				modifier = Modifier
					.fillMaxWidth()
					.statusBarsHeight()
			)
			TopAppBar(
				navigationIcon = {
					IconButton(onClick = onBackClick) {
						Icon(painter = painterResource(id = R.drawable.ic_arrow_back_24), contentDescription = null)
					}
				},
				title = {
					Text(
						text = stringResource(titleResId),
						style = MaterialTheme.typography.h5
					)
				},
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

