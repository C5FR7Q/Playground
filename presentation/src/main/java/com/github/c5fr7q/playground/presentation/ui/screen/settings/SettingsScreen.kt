package com.github.c5fr7q.playground.presentation.ui.screen.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.github.c5fr7q.playground.presentation.R
import com.github.c5fr7q.playground.presentation.ui.screen.blocked.BlockedState
import com.google.accompanist.insets.statusBarsHeight

@Composable
fun SettingsScreen(viewModel: SettingsViewModel) {
	val state by viewModel.state.collectAsState()
	SettingsScreen(
		state = state,
		onBackClick = { viewModel.produceIntent(SettingsIntent.ClickBack) }
	)
}

@Composable
private fun SettingsScreen(
	state: SettingsState,
	onBackClick: () -> Unit
) {
	Scaffold(
		topBar = {
			TopBar(onBackClick)
		}
	) { innerPadding ->
		Box {

		}
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

