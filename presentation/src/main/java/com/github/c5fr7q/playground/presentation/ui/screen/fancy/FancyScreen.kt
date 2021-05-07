package com.github.c5fr7q.playground.presentation.ui.screen.fancy

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

object FancyNavigation {
	const val destination = "fancy"

	fun createRoute() = destination
}

@Composable
fun FancyScreen(viewModel: FancyViewModel) {
	val state by viewModel.state.collectAsState()
	FancyScreen(
		fancyState = state,
		onLoadMore = { viewModel.produceIntent(FancyIntent.LoadMore) },
		onItemClicked = { viewModel.produceIntent(FancyIntent.ClickItem(it)) }
	)
}

@Composable
private fun FancyScreen(
	fancyState: FancyState,
	onLoadMore: () -> Unit,
	onItemClicked: (String) -> Unit
) {
	Column(horizontalAlignment = Alignment.CenterHorizontally) {
		Text(
			modifier = Modifier.padding(top = 16.dp),
			style = MaterialTheme.typography.h3,
			text = "Hello ${fancyState.name}!"
		)
		if (fancyState.currentResult > 0) {
			Text(
				modifier = Modifier.padding(top = 16.dp),
				style = MaterialTheme.typography.h5,
				textAlign = TextAlign.Center,
				text = "Current result:  ${fancyState.currentResult}"
			)
		}

		LazyColumn(
			modifier = Modifier
				.fillMaxWidth()
				.padding(top = 16.dp),
			horizontalAlignment = Alignment.CenterHorizontally
		) {
			val lastIndex = fancyState.dataList.lastIndex
			itemsIndexed(fancyState.dataList) { index, item ->
				key(item) {
					if (index == lastIndex) {
						LaunchedEffect(null) {
							onLoadMore()
						}
					}
					Text(
						modifier = Modifier
							.padding(16.dp)
							.clickable { onItemClicked(item.toString()) },
						style = MaterialTheme.typography.h4,
						text = item.toString()
					)
				}
			}
		}
	}
}