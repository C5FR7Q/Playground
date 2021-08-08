package com.github.c5fr7q.playground.presentation.ui.widget

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.google.accompanist.flowlayout.FlowRow

@Composable
fun TagRow(
	modifier: Modifier = Modifier,
	tags: Collection<String>,
	item: @Composable (String) -> Unit
) {
	FlowRow(
		modifier = modifier,
		mainAxisSpacing = 6.dp,
		crossAxisSpacing = 6.dp,
	) {
		for (tag in tags) {
			item(tag)
		}
	}
}