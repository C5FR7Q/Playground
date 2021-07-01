package com.github.c5fr7q.playground.presentation.ui.widget

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.padding
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.github.c5fr7q.playground.domain.entity.Place


@Composable
fun PlaceTagRow(modifier: Modifier = Modifier, place: Place) {
	TagRow(modifier = modifier, tags = place.tags) {
		Text(
			text = it,
			maxLines = 1,
			overflow = TextOverflow.Ellipsis,
			modifier = Modifier
				.background(color = MaterialTheme.colors.onSurface)
				.padding(4.dp),
			style = MaterialTheme.typography.caption,
			color = MaterialTheme.colors.onPrimary
		)
	}
}