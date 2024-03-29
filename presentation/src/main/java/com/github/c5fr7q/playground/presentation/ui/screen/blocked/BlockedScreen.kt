package com.github.c5fr7q.playground.presentation.ui.screen.blocked

import androidx.compose.runtime.Composable
import com.github.c5fr7q.playground.presentation.R
import com.github.c5fr7q.playground.presentation.ui.screen.common.selectable.SelectionScreen

@Composable
fun BlockedScreen(viewModel: BlockedViewModel) {
	SelectionScreen(
		applySelectionIconResId = R.drawable.ic_delete_24,
		titleResId = R.string.blocked,
		viewModel = viewModel
	)
}

