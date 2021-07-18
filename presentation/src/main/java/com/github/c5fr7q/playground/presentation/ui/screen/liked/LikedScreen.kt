package com.github.c5fr7q.playground.presentation.ui.screen.liked

import androidx.compose.runtime.Composable
import com.github.c5fr7q.playground.presentation.R
import com.github.c5fr7q.playground.presentation.ui.screen.common.selectable.SelectionScreen

@Composable
fun LikedScreen(viewModel: LikedViewModel) {
	SelectionScreen(
		applySelectionIconResId = R.drawable.ic_delete_24,
		titleResId = R.string.liked,
		viewModel = viewModel
	)
}

