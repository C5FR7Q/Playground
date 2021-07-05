package com.github.c5fr7q.playground.presentation.ui.screen.blocked

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
fun BlockedScreen(viewModel: BlockedViewModel) {
	val state by viewModel.state.collectAsState()
	BlockedScreen(
		state = state,
		onBackClick = { viewModel.produceIntent(BaseIntent.Default.ClickBack) },
		onDeleteSelectedPlacesClick = { viewModel.produceIntent(BlockedIntent.ClickDelete) },
		onCategoryToggle = { viewModel.produceIntent(BlockedIntent.ToggleCategory(it)) },
		onPlaceSelectionToggle = { viewModel.produceIntent(BlockedIntent.TogglePlaceSelection(it)) }
	)
}

@Composable
private fun BlockedScreen(
	state: BlockedState,
	onBackClick: () -> Unit,
	onDeleteSelectedPlacesClick: () -> Unit,
	onCategoryToggle: (Place.Category) -> Unit,
	onPlaceSelectionToggle: (Place) -> Unit
) {
	Scaffold(
		topBar = {
			TopBar(
				placesSelected = state.selectedPlaces.isNotEmpty(),
				selectedCategories = state.selectedCategories,
				onBackClick = onBackClick,
				onDeleteSelectedPlacesClick = onDeleteSelectedPlacesClick,
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

@Composable
private fun TopBar(
	placesSelected: Boolean,
	selectedCategories: List<Place.Category>,
	onBackClick: () -> Unit,
	onDeleteSelectedPlacesClick: () -> Unit,
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
						text = stringResource(R.string.blocked),
						style = MaterialTheme.typography.h5
					)
				},
				actions = {
					IconButton(onClick = { filterIsActive = !filterIsActive }) {
						Icon(painter = painterResource(id = R.drawable.ic_filter_list_24), contentDescription = null)
					}
					if (placesSelected) {
						IconButton(onClick = onDeleteSelectedPlacesClick) {
							Icon(painter = painterResource(id = R.drawable.ic_delete_24), contentDescription = null)
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

