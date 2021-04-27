package com.github.c5fr7q.playground.presentation.ui.fancy

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun FancyScreen(viewModel: FancyViewModel) {
	val state by viewModel.state.collectAsState()
	FancyScreen(
		fancyState = state,
		onLoadMore = viewModel::loadMore
	)
}

@Composable
private fun FancyScreen(fancyState: FancyState, onLoadMore: () -> Unit) {
	Text(text = "Hello ${fancyState.name}!")
	LazyColumn {
		val lastIndex = fancyState.numbers.lastIndex
		itemsIndexed(fancyState.numbers) { index, item ->
			key(item) {
				if (index == lastIndex) {
					LaunchedEffect(null) {
						onLoadMore()
					}
				}
				Text(text = item.toString(), modifier = Modifier.padding(16.dp))
			}
		}
	}
}