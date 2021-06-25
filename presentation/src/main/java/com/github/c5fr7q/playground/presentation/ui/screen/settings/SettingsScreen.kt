package com.github.c5fr7q.playground.presentation.ui.screen.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.github.c5fr7q.playground.presentation.R
import com.github.c5fr7q.playground.presentation.ui.base.BaseIntent
import com.github.c5fr7q.playground.presentation.ui.widget.dialog.InputDialog
import com.google.accompanist.insets.statusBarsHeight

@Composable
fun SettingsScreen(viewModel: SettingsViewModel) {
	val state by viewModel.state.collectAsState()
	SettingsScreen(
		state = state,
		onBackClick = { viewModel.produceIntent(BaseIntent.Default.ClickBack) },
		onPackCountClick = { viewModel.produceIntent(SettingsIntent.ClickPackCount) },
		onCachingDaysClick = { viewModel.produceIntent(SettingsIntent.ClickCachingDays) },
		onRadiusClick = { viewModel.produceIntent(SettingsIntent.ClickRadius) },
		onDismissDialog = { viewModel.produceIntent(SettingsIntent.DismissDialog) },
		onInputValue = { viewModel.produceIntent(SettingsIntent.InputValue(it)) }
	)
}

@Composable
private fun SettingsScreen(
	state: SettingsState,
	onBackClick: () -> Unit,
	onPackCountClick: () -> Unit,
	onCachingDaysClick: () -> Unit,
	onRadiusClick: () -> Unit,
	onDismissDialog: () -> Unit,
	onInputValue: (Int) -> Unit,
) {
	Scaffold(
		topBar = {
			TopBar(onBackClick)
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
		when (state.inputReceiver) {
			InputReceiver.NONE -> Unit
			InputReceiver.CACHING_DAYS -> {
				InputDialog(
					title = stringResource(id = R.string.caching_days),
					defaultValue = state.cachingDays,
					onDismissDialog = onDismissDialog,
					onInputValue = onInputValue
				)
			}
			InputReceiver.PACK_COUNT -> {
				InputDialog(
					title = stringResource(id = R.string.pack_count),
					defaultValue = state.packCount,
					onDismissDialog = onDismissDialog,
					onInputValue = onInputValue
				)
			}
			InputReceiver.RADIUS -> {
				InputDialog(
					title = stringResource(id = R.string.radius),
					defaultValue = state.radius,
					onDismissDialog = onDismissDialog,
					onInputValue = onInputValue
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
				style = MaterialTheme.typography.subtitle1.copy(color = MaterialTheme.colors.onSurface.copy(alpha = ContentAlpha.high))
			)
			Text(
				text = value,
				style = MaterialTheme.typography.body2.copy(color = MaterialTheme.colors.onSurface.copy(alpha = ContentAlpha.medium))
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
	onBackClick: () -> Unit
) {
	Column {
		Box(
			modifier = Modifier
				.fillMaxWidth()
				.statusBarsHeight()
				.background(MaterialTheme.colors.surface)
		)
		TopAppBar(
			navigationIcon = {
				IconButton(onClick = onBackClick) {
					Icon(Icons.Default.ArrowBack, contentDescription = null)
				}
			},
			title = {
				Text(
					text = stringResource(R.string.settings),
					style = MaterialTheme.typography.h5
				)
			},
			backgroundColor = MaterialTheme.colors.surface,
			contentColor = MaterialTheme.colors.onSurface,
			elevation = 0.dp
		)
		Divider()
	}
}

enum class InputReceiver {
	NONE,
	CACHING_DAYS,
	PACK_COUNT,
	RADIUS
}


