package com.github.c5fr7q.playground.presentation.ui.screen.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.github.c5fr7q.playground.presentation.R
import com.github.c5fr7q.playground.presentation.ui.widget.DefaultTopAppBar
import com.github.c5fr7q.playground.presentation.ui.widget.OptionsMenu
import com.github.c5fr7q.playground.presentation.ui.widget.OptionsMenuItemModel

@Composable
fun SettingsScreen(viewModel: SettingsViewModel) {
	val state by viewModel.state.collectAsState()
	SettingsScreen(
		state = state,
		onLikedClick = { viewModel.produceIntent(SettingsIntent.ClickLiked) },
		onBlockedClick = { viewModel.produceIntent(SettingsIntent.ClickBlocked) },
		onPackCountClick = { viewModel.produceIntent(SettingsIntent.ClickPackCount) },
		onCachingDaysClick = { viewModel.produceIntent(SettingsIntent.ClickCachingDays) },
		onRadiusClick = { viewModel.produceIntent(SettingsIntent.ClickRadius) },
	)
}

@Composable
private fun SettingsScreen(
	state: SettingsState,
	onLikedClick: () -> Unit,
	onBlockedClick: () -> Unit,
	onPackCountClick: () -> Unit,
	onCachingDaysClick: () -> Unit,
	onRadiusClick: () -> Unit
) {
	Scaffold(
		topBar = {
			TopBar(
				onLikedClick = onLikedClick,
				onBlockedClick = onBlockedClick
			)
		}
	) { innerPadding ->
		LazyColumn {
			item {
				SettingsCategoryItem(text = stringResource(id = R.string.places))
			}
			item {
				SettingsValueItem(
					title = stringResource(id = R.string.pack_count),
					value = state.packCount.takeIf { it != 0 }?.toString() ?: "",
					itemClick = onPackCountClick
				)
			}
			item {
				SettingsValueItem(
					title = stringResource(id = R.string.caching_days),
					value = state.cachingDays.takeIf { it != 0 }?.toString() ?: "",
					itemClick = onCachingDaysClick
				)
			}
			item {
				SettingsValueItem(
					title = stringResource(id = R.string.radius),
					value = state.radius.takeIf { it != 0 }?.let { stringResource(id = R.string.n_meters, it) } ?: "",
					itemClick = onRadiusClick
				)
			}
		}
	}
}

@Composable
private fun SettingsValueItem(
	title: String,
	value: String,
	itemClick: () -> Unit
) {
	Box(
		modifier = Modifier
			.clickable(onClick = itemClick)
			.fillMaxWidth()
			.height(64.dp)
	) {
		Column(
			modifier = Modifier
				.fillMaxSize()
				.padding(horizontal = 16.dp)
				.padding(top = 9.dp)
		) {
			Text(
				text = title,
				style = MaterialTheme.typography.subtitle1.copy(color = MaterialTheme.colors.onSurface)
			)
			Text(
				text = value,
				style = MaterialTheme.typography.body2.copy(
					color = MaterialTheme.colors.onSurface.copy(alpha = ContentAlpha.medium)
				)
			)
		}
		Divider(modifier = Modifier.padding(start = 16.dp))
	}
}

@Composable
private fun SettingsCategoryItem(text: String) {
	Box(
		modifier = Modifier
			.height(48.dp)
			.fillMaxWidth()
	) {
		Text(
			modifier = Modifier
				.align(Alignment.BottomStart)
				.padding(start = 16.dp, bottom = 4.dp),
			text = text,
			style = MaterialTheme.typography.h5.copy(color = MaterialTheme.colors.primaryVariant)
		)
	}
}

@Composable
private fun TopBar(
	onLikedClick: () -> Unit,
	onBlockedClick: () -> Unit
) {
	Column {
		DefaultTopAppBar(
			titleResId = R.string.settings,
			actions = {
				OptionsMenu(options = mutableListOf<OptionsMenuItemModel>().apply {
					add(OptionsMenuItemModel(stringResource(id = R.string.all_liked_places), onLikedClick))
					add(OptionsMenuItemModel(stringResource(id = R.string.all_blocked_places), onBlockedClick))
				})
			},
			backgroundColor = MaterialTheme.colors.surface,
			contentColor = MaterialTheme.colors.onSurface,
			elevation = 0.dp
		)
		Divider()
	}
}

