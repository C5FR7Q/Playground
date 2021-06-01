package com.github.c5fr7q.playground.presentation.ui.screen.blocked

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.github.c5fr7q.playground.domain.entity.Place
import com.github.c5fr7q.playground.presentation.R
import com.github.c5fr7q.playground.presentation.ui.base.BaseIntent
import com.github.c5fr7q.playground.presentation.ui.util.CustomContentAlpha
import com.github.c5fr7q.playground.presentation.ui.util.TagRow
import com.github.c5fr7q.playground.presentation.ui.util.asText
import com.google.accompanist.coil.rememberCoilPainter
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
		LazyColumn {
			items(state.places) { place ->
				PlaceItem(
					place = place,
					isSelected = state.selectedPlaces.contains(place),
					onPlaceClick = { onPlaceSelectionToggle(place) }
				)
			}
		}
	}
}

@Composable
private fun PlaceItem(
	place: Place,
	isSelected: Boolean,
	onPlaceClick: () -> Unit
) {
	val backgroundColor = when {
		isSelected -> MaterialTheme.colors.primary.copy(alpha = CustomContentAlpha.pressed)
		else -> Color.Transparent
	}
	Box(
		modifier = Modifier
			.background(color = backgroundColor)
			.clickable(onClick = onPlaceClick)
	) {
		Row(
			modifier = Modifier.padding(16.dp),
			verticalAlignment = Alignment.CenterVertically
		) {
			PlaceImage(
				modifier = Modifier.width(88.dp),
				url = place.imageUrl
			)

			Spacer(modifier = Modifier.size(16.dp))

			Column {
				Text(text = place.name, style = MaterialTheme.typography.h4)
				CompositionLocalProvider(LocalContentAlpha provides ContentAlpha.medium) {
					Text(
						text = place.position.asText(),
						style = MaterialTheme.typography.overline
					)
				}
				Spacer(modifier = Modifier.size(2.dp))
				Column(modifier = Modifier.wrapContentWidth()) {
					TagRow(tags = place.categories.map { it.asText() }) {
						Text(
							text = it,
							maxLines = 1,
							overflow = TextOverflow.Ellipsis,
							modifier = Modifier
								.background(
									color = MaterialTheme.colors.primaryVariant,
									shape = RoundedCornerShape(4.dp)
								)
								.padding(4.dp),
							style = MaterialTheme.typography.caption,
							color = MaterialTheme.colors.onPrimary.copy(alpha = ContentAlpha.high)
						)
					}
				}
			}
		}
		Divider(modifier = Modifier.align(Alignment.BottomCenter))
	}
}

@Composable
private fun PlaceImage(modifier: Modifier = Modifier, url: String) {
	Box(
		modifier = modifier
			.background(color = MaterialTheme.colors.onSurface.copy(alpha = ContentAlpha.medium), RoundedCornerShape(4.dp))
			.aspectRatio(4f / 3f)
			.fillMaxWidth()
	) {
		Image(
			painter = rememberCoilPainter(
				request = url,
				fadeIn = true,
				shouldRefetchOnSizeChange = { _, _ -> false },
			),
			contentDescription = null,
		)
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
	val backgroundColor = if (placesSelected) MaterialTheme.colors.onSurface else MaterialTheme.colors.surface
	val contentColor = if (placesSelected) MaterialTheme.colors.surface else MaterialTheme.colors.onSurface
	val buttonsColor = if (placesSelected) MaterialTheme.colors.surface else MaterialTheme.colors.primary
	Column(
		modifier = Modifier.background(color = backgroundColor)
	) {
		Box(
			modifier = Modifier
				.fillMaxWidth()
				.statusBarsHeight()
		)
		TopAppBar(
			navigationIcon = {
				IconButton(onClick = onBackClick) {
					Icon(Icons.Default.ArrowBack, contentDescription = null)
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
					Icon(Icons.Default.FilterList, contentDescription = null)
				}
				if (placesSelected) {
					IconButton(onClick = onDeleteSelectedPlacesClick) {
						Icon(Icons.Default.Delete, contentDescription = null)
					}
				}
			},
			backgroundColor = Color.Transparent,
			contentColor = contentColor,
			elevation = 0.dp,
		)
		if (placesSelected) {
			Divider(color = contentColor)
		} else {
			Divider()
		}
		if (filterIsActive) {
			LazyRow(modifier = Modifier.padding(vertical = 6.dp)) {
				val allCategories = Place.Category.values()
				val lastIndex = allCategories.lastIndex
				itemsIndexed(allCategories) { index, category ->
					val selected = selectedCategories.contains(category)
					if (selected) {
						Button(
							modifier = Modifier.padding(
								start = if (index == 0) 16.dp else 0.dp,
								end = if (index == lastIndex) 16.dp else 6.dp
							),
							contentPadding = PaddingValues(4.dp),
							onClick = { onCategoryToggle(category) },
							colors = ButtonDefaults.buttonColors(
								contentColor = backgroundColor,
								backgroundColor = buttonsColor
							)
						) {
							Text(text = category.asText(), style = MaterialTheme.typography.button)
						}
					} else {
						OutlinedButton(
							modifier = Modifier.padding(
								start = if (index == 0) 16.dp else 0.dp,
								end = if (index == lastIndex) 16.dp else 6.dp,
							),
							contentPadding = PaddingValues(4.dp),
							onClick = { onCategoryToggle(category) },
							colors = ButtonDefaults.outlinedButtonColors(
								contentColor = buttonsColor,
								backgroundColor = Color.Transparent
							),
							border = BorderStroke(
								ButtonDefaults.OutlinedBorderSize,
								buttonsColor
							)
						) {
							Text(
								text = category.asText(),
								style = MaterialTheme.typography.button,
								color = buttonsColor
							)
						}
					}
				}
			}
			if (placesSelected) {
				Divider(color = contentColor)
			} else {
				Divider()
			}
		}
	}
}

