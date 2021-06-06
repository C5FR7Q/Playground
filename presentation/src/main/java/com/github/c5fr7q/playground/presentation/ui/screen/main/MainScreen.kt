package com.github.c5fr7q.playground.presentation.ui.screen.main

import androidx.annotation.StringRes
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.github.c5fr7q.playground.domain.entity.Place
import com.github.c5fr7q.playground.presentation.R
import com.github.c5fr7q.playground.presentation.ui.util.TagRow
import com.github.c5fr7q.playground.presentation.ui.util.asText
import com.google.accompanist.coil.rememberCoilPainter
import com.google.accompanist.insets.navigationBarsHeight
import com.google.accompanist.insets.statusBarsHeight
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import java.util.*

@Composable
fun MainScreen(viewModel: MainViewModel) {
	val state by viewModel.state.collectAsState()
	MainScreen(
		state = state,
		sideEffectFlow = viewModel.sideEffect,
		onLoadMore = { viewModel.produceIntent(MainIntent.LoadMore) },
		onLikeClick = { viewModel.produceIntent(MainIntent.ClickLike) },
		onPreviousClick = { viewModel.produceIntent(MainIntent.ClickPrevious) },
		onBlockedClick = { viewModel.produceIntent(MainIntent.ClickBlocked) },
		onSettingsClick = { viewModel.produceIntent(MainIntent.ClickSettings) },
		onRefreshClick = { viewModel.produceIntent(MainIntent.ClickRefresh) },
		onCategoryToggle = { viewModel.produceIntent(MainIntent.ToggleCategory(it)) },
		onToggleItemFavorite = { viewModel.produceIntent(MainIntent.ToggleItemFavorite(it)) },
		onBlockClick = { viewModel.produceIntent(MainIntent.ClickBlock(it)) },
		onShowInMapsClick = { viewModel.produceIntent(MainIntent.ClickShowInMaps(it)) },
	)
}

@Composable
private fun MainScreen(
	state: MainState,
	sideEffectFlow: Flow<MainSideEffect>,
	onLoadMore: () -> Unit,
	onLikeClick: () -> Unit,
	onPreviousClick: () -> Unit,
	onBlockedClick: () -> Unit,
	onSettingsClick: () -> Unit,
	onRefreshClick: () -> Unit,
	onCategoryToggle: (Place.Category) -> Unit,
	onToggleItemFavorite: (Place) -> Unit,
	onBlockClick: (Place) -> Unit,
	onShowInMapsClick: (Place) -> Unit
) {
	val listState = rememberLazyListState()
	val scaffoldState = rememberScaffoldState()

	LaunchedEffect(null) {
		sideEffectFlow.collect { sideEffect ->
			when (sideEffect) {
				MainSideEffect.ScrollToTop -> {
					launch { listState.scrollToItem(0) }
				}
				is MainSideEffect.ShowError -> {
					launch { scaffoldState.snackbarHostState.showSnackbar(message = sideEffect.text) }
				}
			}
		}
	}

	Scaffold(
		scaffoldState = scaffoldState,
		topBar = {
			TopBar(
				titleRes = when (state.contentType) {
					MainState.ContentType.PREVIOUS -> R.string.previous
					MainState.ContentType.NEAR -> R.string.near_you
					MainState.ContentType.FAVORITE -> R.string.favorite
				},
				selectedCategories = state.selectedCategories,
				onCategoryToggle = onCategoryToggle
			)
		},
		bottomBar = {
			BottomBar(
				contentType = state.contentType,
				onLikeClick = onLikeClick,
				onPreviousClick = onPreviousClick,
				onBlockedClick = onBlockedClick,
				onSettingsClick = onSettingsClick
			)
		},
		floatingActionButton = {
			if (state.selectedCategories.isNotEmpty()) {
				FloatingActionButton(onClick = onRefreshClick) {
					Icon(Icons.Default.Refresh, contentDescription = null)
				}
			}
		},
		floatingActionButtonPosition = FabPosition.Center,
		isFloatingActionButtonDocked = true
	) { innerPadding: PaddingValues ->
		Box(
			modifier = Modifier
				.fillMaxSize()
		) {
			if (state.places.isEmpty()) {
				Text(
					modifier = Modifier.align(Alignment.Center),
					text = stringResource(id = R.string.nothing_found),
					style = MaterialTheme.typography.h5
				)
			} else {
				LazyColumn(modifier = Modifier.fillMaxSize(), state = listState) {
					val places = state.places
					if (places.isNotEmpty()) {
						val lastIndex = places.lastIndex
						itemsIndexed(places) { index, item ->
							val isFirst = index == 0
							val isLast = index == lastIndex
							if (isLast) {
								LaunchedEffect(null) {
									onLoadMore()
								}
							}
							val modifier = when {
								isFirst -> Modifier.padding(top = innerPadding.calculateTopPadding())
								isLast -> Modifier.padding(bottom = innerPadding.calculateBottomPadding())
								else -> Modifier
							}
							PlaceItem(
								modifier = modifier,
								place = item,
								onToggleFavoriteClick = { onToggleItemFavorite(item) },
								onBlockClick = { onBlockClick(item) },
								onShowInMapsClick = { onShowInMapsClick(item) }
							)
							if (!isLast) {
								Divider()
							}
						}
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

@Composable
private fun PlaceItem(
	modifier: Modifier = Modifier,
	place: Place,
	onToggleFavoriteClick: () -> Unit,
	onBlockClick: () -> Unit,
	onShowInMapsClick: () -> Unit
) {
	Surface(modifier = modifier) {
		Column {
			Spacer(modifier = Modifier.height(16.dp))
			Row(
				modifier = Modifier
					.padding(horizontal = 4.dp)
					.fillMaxWidth(),
				verticalAlignment = Alignment.CenterVertically
			) {
				var showMenu by remember { mutableStateOf(false) }
				IconButton(onClick = onToggleFavoriteClick) {
					Icon(if (place.isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder, contentDescription = null)
				}
				Spacer(modifier = Modifier.size(6.dp))
				Text(modifier = Modifier.weight(1f), text = place.name, style = MaterialTheme.typography.h4)
				Spacer(modifier = Modifier.size(6.dp))
				IconButton(onClick = { showMenu = true }) {
					Icon(Icons.Default.MoreVert, contentDescription = null)
					DropdownMenu(
						expanded = showMenu,
						onDismissRequest = { showMenu = false })
					{
						DropdownMenuItem(onClick = {
							showMenu = false
							onBlockClick()
						}) {
							Text(text = stringResource(id = R.string.block))
						}
						DropdownMenuItem(onClick = {
							showMenu = false
							onShowInMapsClick()
						}) {
							Text(text = stringResource(id = R.string.show_in_maps))
						}
					}
				}

			}
			CompositionLocalProvider(LocalContentAlpha provides ContentAlpha.medium) {
				Text(
					modifier = Modifier.padding(horizontal = 16.dp),
					text = place.position.asText(),
					style = MaterialTheme.typography.overline
				)
			}
			Spacer(modifier = Modifier.height(2.dp))
			Column(
				modifier = Modifier
					.padding(horizontal = 16.dp)
					.wrapContentWidth()
			) {
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
						color = MaterialTheme.colors.onPrimary
					)
				}
			}
			Spacer(modifier = Modifier.height(6.dp))
			TagRow(modifier = Modifier.padding(horizontal = 16.dp), tags = place.tags) {
				Text(
					text = it,
					maxLines = 1,
					overflow = TextOverflow.Ellipsis,
					modifier = Modifier
						.background(MaterialTheme.colors.onSurface, RoundedCornerShape(4.dp))
						.padding(4.dp),
					style = MaterialTheme.typography.caption,
					color = MaterialTheme.colors.onPrimary
				)
			}
			Spacer(modifier = Modifier.height(6.dp))
			PlaceImage(modifier = Modifier.padding(horizontal = 16.dp), url = place.imageUrl, rating = place.rating)
			Spacer(modifier = Modifier.height(16.dp))
		}
	}
}

@Composable
private fun PlaceImage(modifier: Modifier = Modifier, url: String, rating: Float) {
	Box(
		modifier = modifier
			.clip(RoundedCornerShape(4.dp))
			.background(color = MaterialTheme.colors.onSurface.copy(alpha = ContentAlpha.medium))
			.aspectRatio(4f / 3f)
			.fillMaxWidth()
	) {
		Image(
			modifier = Modifier.fillMaxSize(),
			contentScale = ContentScale.Crop,
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
private fun TopBar(
	@StringRes titleRes: Int,
	selectedCategories: List<Place.Category>,
	onCategoryToggle: (Place.Category) -> Unit
) {
	var filterIsActive by remember { mutableStateOf(true) }
	Column(
		modifier = Modifier.background(MaterialTheme.colors.surface)
	) {
		Box(
			modifier = Modifier
				.fillMaxWidth()
				.statusBarsHeight()
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
			actions = {
				IconButton(onClick = { filterIsActive = !filterIsActive }) {
					Icon(Icons.Default.FilterList, contentDescription = null)
				}
			},
			backgroundColor = Color.Transparent,
			contentColor = MaterialTheme.colors.onSurface,
			elevation = 0.dp
		)
		Divider()
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
							onClick = { onCategoryToggle(category) }
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
							border = BorderStroke(ButtonDefaults.OutlinedBorderSize, MaterialTheme.colors.primary)
						) {
							Text(text = category.asText(), style = MaterialTheme.typography.button, color = MaterialTheme.colors.primary)
						}
					}
				}
			}
			Divider()
		}
	}
}

@Composable
private fun BottomBar(
	contentType: MainState.ContentType,
	onLikeClick: () -> Unit,
	onPreviousClick: () -> Unit,
	onBlockedClick: () -> Unit,
	onSettingsClick: () -> Unit
) {
	var showMenu by remember { mutableStateOf(false) }
	Column {
		BottomAppBar(
			cutoutShape = CircleShape
		) {
			Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
				if (contentType != MainState.ContentType.FAVORITE) {
					IconButton(onClick = onLikeClick) {
						Icon(Icons.Default.Favorite, contentDescription = null)
					}
				}
				IconButton(onClick = { showMenu = true }) {
					Icon(Icons.Default.MoreVert, contentDescription = null)
					DropdownMenu(
						expanded = showMenu,
						onDismissRequest = { showMenu = false })
					{
						if (contentType != MainState.ContentType.PREVIOUS) {
							DropdownMenuItem(onClick = {
								showMenu = false
								onPreviousClick()
							}) {
								Text(text = stringResource(id = R.string.previous))
							}
						}
						DropdownMenuItem(onClick = {
							showMenu = false
							onBlockedClick()
						}) {
							Text(text = stringResource(id = R.string.blocked))
						}
						DropdownMenuItem(onClick = {
							showMenu = false
							onSettingsClick()
						}) {
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
				.background(MaterialTheme.colors.primarySurface)
		)
	}
}