package com.github.c5fr7q.playground.presentation.ui.screen.main

import androidx.annotation.StringRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.github.c5fr7q.playground.domain.entity.Place
import com.github.c5fr7q.playground.domain.entity.Position
import com.github.c5fr7q.playground.presentation.R
import com.github.c5fr7q.playground.presentation.ui.util.TagRow
import com.google.accompanist.coil.rememberCoilPainter
import com.google.accompanist.insets.navigationBarsHeight
import com.google.accompanist.insets.statusBarsHeight
import java.util.*

object MainNavigation {
	const val destination = "main"

	fun createRoute() = destination
}

@Composable
fun MainScreen(viewModel: MainViewModel) {
	val state by viewModel.state.collectAsState()
	MainScreen(
		state = state,
		onLoadMore = { viewModel.produceIntent(MainIntent.LoadMore) },
		onLikeClick = { viewModel.produceIntent(MainIntent.ClickLike) },
		onPreviousClick = { viewModel.produceIntent(MainIntent.ClickPrevious) },
		onSettingsClick = { viewModel.produceIntent(MainIntent.ClickSettings) },
		onRefreshClick = { viewModel.produceIntent(MainIntent.ClickRefresh) },
		onCategoryToggle = { viewModel.produceIntent(MainIntent.ToggleCategory(it)) },
	)
}

@Composable
private fun MainScreen(
	state: MainState,
	onLoadMore: () -> Unit,
	onLikeClick: () -> Unit,
	onPreviousClick: () -> Unit,
	onSettingsClick: () -> Unit,
	onRefreshClick: () -> Unit,
	onCategoryToggle: (Place.Category) -> Unit,
) {
	Box {
		Scaffold(
			topBar = { TopBar(titleRes = if (state.usesPreviousPlaces) R.string.previous_places else R.string.near_you) },
			bottomBar = { BottomBar(onLikeClick, onPreviousClick, onSettingsClick) },
			floatingActionButton = {
				FloatingActionButton(onClick = onRefreshClick) {
					Icon(Icons.Default.Refresh, contentDescription = null)
				}
			},
			floatingActionButtonPosition = FabPosition.Center,
			isFloatingActionButtonDocked = true
		) { innerPadding ->
			Box {
				LazyColumn(modifier = Modifier.fillMaxSize()) {
					val places = state.places
					if (places.isNotEmpty()) {
						val lastIndex = places.lastIndex
						itemsIndexed(places) { index, item ->
							if (index == lastIndex) {
								LaunchedEffect(null) {
									onLoadMore()
								}
							}
							PlaceItem(item)
						}
					}
				}
				if (state.isLoading) {
					LinearProgressIndicator(
						modifier = Modifier
							.align(Alignment.TopCenter)
							.fillMaxWidth()
					)
				}
			}
		}
	}
}

@Composable
private fun PlaceItem(place: Place) {
	Column {
		Surface(modifier = Modifier.padding(horizontal = 16.dp)) {
			Column {
				Spacer(modifier = Modifier.height(16.dp))
				CompositionLocalProvider(LocalContentAlpha provides ContentAlpha.high) {
					Text(text = place.name, style = MaterialTheme.typography.h4)
				}
				CompositionLocalProvider(LocalContentAlpha provides ContentAlpha.medium) {
					Text(text = place.position.asText(), style = MaterialTheme.typography.overline)
				}
				Spacer(modifier = Modifier.height(2.dp))
				TagRow(tags = place.tags) {
					CompositionLocalProvider(LocalContentAlpha provides ContentAlpha.high) {
						Text(
							text = it,
							maxLines = 1,
							overflow = TextOverflow.Ellipsis,
							modifier = Modifier
								.background(MaterialTheme.colors.onSurface, RoundedCornerShape(4.dp))
								.padding(4.dp),
							style = MaterialTheme.typography.caption,
							color = MaterialTheme.colors.onPrimary.copy(alpha = ContentAlpha.high)
						)
					}
				}
				Spacer(modifier = Modifier.height(6.dp))
				PlaceImage(url = place.imageUrl, rating = place.rating)
				Spacer(modifier = Modifier.height(16.dp))
			}
		}
		Divider()
	}
}

@Composable
fun PlaceImage(url: String, rating: Float) {
	Box(
		modifier = Modifier
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

		Box(
			modifier = Modifier
				.background(
					color = MaterialTheme.colors.onSurface.copy(alpha = ContentAlpha.high),
					shape = RoundedCornerShape(topStart = 4.dp, bottomEnd = 4.dp)
				)
				.padding(4.dp)
		) {
			Text(
				text = "R:" + "%.2f".format(Locale.US, rating),
				style = MaterialTheme.typography.overline,
				color = MaterialTheme.colors.onPrimary.copy(alpha = ContentAlpha.high)
			)
		}
	}
}

@Composable
private fun TopBar(@StringRes titleRes: Int) {
	Column {
		Box(
			modifier = Modifier
				.fillMaxWidth()
				.statusBarsHeight()
				.background(MaterialTheme.colors.surface)
		)
		TopAppBar(
			title = {
				titleRes.takeIf { it != 0 }?.let {
					Text(
						text = stringResource(it),
						style = MaterialTheme.typography.h5
					)
				}
			},
			backgroundColor = MaterialTheme.colors.surface,
			contentColor = MaterialTheme.colors.onSurface,
			elevation = 0.dp
		)
		Divider()
	}
}

@Composable
private fun BottomBar(
	onLikeClick: () -> Unit,
	onPreviousClick: () -> Unit,
	onSettingsClick: () -> Unit
) {
	var showMenu by remember { mutableStateOf(false) }
	Column {
		BottomAppBar(
			cutoutShape = CircleShape
		) {
			Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
				IconButton(onClick = onLikeClick) {
					Icon(Icons.Default.Favorite, contentDescription = null)
				}
				IconButton(onClick = { showMenu = true }) {
					Icon(Icons.Default.MoreVert, contentDescription = null)
					DropdownMenu(
						expanded = showMenu,
						onDismissRequest = { showMenu = false })
					{
						DropdownMenuItem(onClick = onPreviousClick) {
							Text(text = stringResource(id = R.string.show_previous_places))
						}
						DropdownMenuItem(onClick = onSettingsClick) {
							Text(text = stringResource(id = R.string.settings))
						}
					}
				}
			}
		}
		Box(
			modifier = Modifier
				.fillMaxWidth()
				.navigationBarsHeight()
				.background(MaterialTheme.colors.primary)
		)
	}
}

private fun Position.asText() = "%.5f".format(Locale.US, lon) + "," + "%.5f".format(Locale.US, lat)