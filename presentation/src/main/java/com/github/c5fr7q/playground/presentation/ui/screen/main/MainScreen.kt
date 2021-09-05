package com.github.c5fr7q.playground.presentation.ui.screen.main

import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.github.c5fr7q.playground.domain.entity.Place
import com.github.c5fr7q.playground.presentation.R
import com.github.c5fr7q.playground.presentation.ui.util.transition.VisibilityTransitionState
import com.github.c5fr7q.playground.presentation.ui.widget.DefaultTopAppBar
import com.github.c5fr7q.playground.presentation.ui.widget.PlaceItem
import com.github.c5fr7q.playground.presentation.ui.widget.SelectCategoriesRow
import com.google.accompanist.insets.navigationBarsHeight
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

@Composable
fun MainScreen(viewModel: MainViewModel) {
	MainScreen(
		stateFlow = viewModel.state,
		sideEffectFlow = viewModel.sideEffect,
		onLoadMore = { viewModel.produceIntent(MainIntent.LoadMore) },
		onSettingsClick = { viewModel.produceIntent(MainIntent.ClickSettings) },
		onRefreshClick = { viewModel.produceIntent(MainIntent.ClickRefresh) },
		onLikeClick = { viewModel.produceIntent(MainIntent.ClickLike) },
		onCategoryToggle = { viewModel.produceIntent(MainIntent.ToggleCategory(it)) },
		onToggleItemFavorite = { viewModel.produceIntent(MainIntent.ToggleItemFavorite(it)) },
		onBlockClick = { viewModel.produceIntent(MainIntent.ClickBlock(it)) },
		onShowInMapsClick = { viewModel.produceIntent(MainIntent.ClickShowInMaps(it)) },
	)
}

@Composable
private fun MainScreen(
	stateFlow: StateFlow<MainState>,
	sideEffectFlow: Flow<MainSideEffect>,
	onLoadMore: () -> Unit,
	onSettingsClick: () -> Unit,
	onRefreshClick: () -> Unit,
	onLikeClick: () -> Unit,
	onCategoryToggle: (Place.Category) -> Unit,
	onToggleItemFavorite: (Place) -> Unit,
	onBlockClick: (Place) -> Unit,
	onShowInMapsClick: (Place) -> Unit
) {
	val state by stateFlow.collectAsState()

	val listState = rememberLazyListState()
	val scaffoldState = rememberScaffoldState()

	LaunchedEffect(null) {
		sideEffectFlow.collect { sideEffect ->
			when (sideEffect) {
				MainSideEffect.ScrollToTop -> {
					launch {
						if (listState.layoutInfo.visibleItemsInfo.isNotEmpty()) {
							listState.scrollToItem(0)
						}
					}
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
				likedOnly = state.likedOnly,
				canShowLikeButton = state.places.firstOrNull { it.isFavorite } != null,
				selectedCategories = state.selectedCategories,
				onCategoryToggle = onCategoryToggle,
				onLikeClick = onLikeClick
			)
		},
		bottomBar = {
			BottomBar(
				onSettingsClick = onSettingsClick
			)
		},
		floatingActionButton = {
			var transitionState by remember { mutableStateOf(VisibilityTransitionState.HIDDEN) }
			transitionState = when {
				state.selectedCategories.isNotEmpty() -> VisibilityTransitionState.SHOWN
				else -> VisibilityTransitionState.HIDDEN
			}
			val transition = updateTransition(transitionState, label = "FabTransition")

			val fabSize by transition.animateDp(label = "FabTransition_fabSize") { state ->
				when (state) {
					VisibilityTransitionState.SHOWN -> 56.dp
					VisibilityTransitionState.HIDDEN -> 0.dp
				}
			}

			if (fabSize.value != 0f) {
				val rotation = when {
					state.isLoading -> {
						val infiniteTransition = rememberInfiniteTransition()
						infiniteTransition.animateFloat(
							initialValue = 0f,
							targetValue = 360f,
							animationSpec = infiniteRepeatable(animation = tween(durationMillis = 1000, easing = LinearEasing))
						).value
					}
					else -> 0f
				}
				FloatingActionButton(
					modifier = Modifier.size(fabSize),
					shape = MaterialTheme.shapes.small,
					onClick = onRefreshClick
				) {
					Icon(
						modifier = Modifier.graphicsLayer(rotationZ = rotation),
						painter = painterResource(id = R.drawable.ic_refresh_24),
						contentDescription = null
					)
				}
			}
		},
		floatingActionButtonPosition = FabPosition.Center,
		isFloatingActionButtonDocked = true
	) { innerPadding: PaddingValues ->
		Box(modifier = Modifier.fillMaxSize()) {
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

							var transitionState by remember { mutableStateOf(VisibilityTransitionState.HIDDEN) }

							val transition = updateTransition(targetState = transitionState, label = "PlaceItemTransition")
							LaunchedEffect(null) {
								transitionState = VisibilityTransitionState.SHOWN
							}

							val alpha by transition.animateFloat(label = "PlaceItemTransition_alpha") { state ->
								when (state) {
									VisibilityTransitionState.HIDDEN -> 0f
									VisibilityTransitionState.SHOWN -> 1f
								}
							}

							val scale by transition.animateFloat(label = "PlaceItemTransition_scale") { state ->
								when (state) {
									VisibilityTransitionState.HIDDEN -> 0.5f
									VisibilityTransitionState.SHOWN -> 1f
								}
							}

							key(item.id) {
								PlaceItem(
									modifier = modifier.graphicsLayer(
										alpha = alpha,
										scaleX = scale,
										scaleY = scale
									),
									place = item,
									onToggleFavoriteClick = { onToggleItemFavorite(item) },
									onBlockClick = { onBlockClick(item) },
									onShowInMapsClick = { onShowInMapsClick(item) }
								)
							}
							if (!isLast) {
								Divider(modifier = Modifier.alpha(alpha))
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
private fun TopBar(
	likedOnly: Boolean,
	canShowLikeButton: Boolean,
	selectedCategories: List<Place.Category>,
	onCategoryToggle: (Place.Category) -> Unit,
	onLikeClick: () -> Unit
) {
	var filterIsActive by remember { mutableStateOf(true) }
	Column(
		modifier = Modifier
			.background(MaterialTheme.colors.surface)
			.animateContentSize()
	) {
		DefaultTopAppBar(
			actions = {
				if (filterIsActive && canShowLikeButton) {
					IconButton(onClick = onLikeClick) {
						Icon(
							painter = painterResource(
								id = when {
									likedOnly -> R.drawable.ic_favorite_24
									else -> R.drawable.ic_favorite_border_24
								}
							), contentDescription = null
						)
					}
				}
				IconButton(onClick = { filterIsActive = !filterIsActive }) {
					Icon(painter = painterResource(id = R.drawable.ic_filter_list_24), contentDescription = null)
				}
			},
			backgroundColor = Color.Transparent,
			contentColor = MaterialTheme.colors.onSurface,
			elevation = 0.dp
		)
		Divider()
		if (filterIsActive) {
			SelectCategoriesRow(selectedCategories = selectedCategories, onCategoryToggle = onCategoryToggle)
			Divider()
		}
	}
}

@Composable
private fun BottomBar(
	onSettingsClick: () -> Unit
) {
	Column {
		val backgroundColor = MaterialTheme.colors.primarySurface
		BottomAppBar(backgroundColor = backgroundColor) {
			Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
				IconButton(onClick = onSettingsClick) {
					Icon(painter = painterResource(id = R.drawable.ic_settings_24), contentDescription = null)
				}
			}
		}
		Box(
			modifier = Modifier
				.fillMaxWidth()
				.navigationBarsHeight()
				.background(backgroundColor)
		)
	}
}