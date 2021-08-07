package com.github.c5fr7q.playground.presentation.ui.widget

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.VisibilityThreshold
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.ContentAlpha
import androidx.compose.material.Divider
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import coil.compose.rememberImagePainter
import com.github.c5fr7q.playground.domain.entity.Place
import com.github.c5fr7q.playground.presentation.ui.util.asText

@Composable
fun SelectablePlaceItem(
	modifier: Modifier = Modifier,
	place: Place,
	isSelected: Boolean,
	onPlaceClick: () -> Unit
) {
	Box(
		modifier = modifier
			.clickable(onClick = onPlaceClick)
			.height(IntrinsicSize.Max)
	) {
		Spacer(
			modifier = Modifier
				.background(color = MaterialTheme.colors.secondary)
				.width(
					animateDpAsState(
						targetValue = if (isSelected) 4.dp else 0.dp,
						animationSpec = spring(
							visibilityThreshold = Dp.VisibilityThreshold,
							dampingRatio = Spring.DampingRatioMediumBouncy
						)
					).value
				)
				.fillMaxHeight()
				.align(Alignment.CenterEnd)
		)
		Row(
			modifier = Modifier.padding(16.dp),
			verticalAlignment = Alignment.CenterVertically
		) {
			PlaceImage(
				modifier = Modifier.width(88.dp),
				url = place.imageUrl
			)

			Spacer(modifier = Modifier.size(16.dp))

			Column {
				Text(text = place.name, style = MaterialTheme.typography.h4)
				Text(
					text = place.position.asText(),
					style = MaterialTheme.typography.overline.copy(
						color = MaterialTheme.colors.onSurface.copy(alpha = ContentAlpha.medium)
					)
				)
				Spacer(modifier = Modifier.size(2.dp))
				PlaceCategoryRow(modifier = Modifier.wrapContentWidth(), place = place)
			}
		}
		Divider(modifier = Modifier.align(Alignment.BottomCenter))

	}
}

@Composable
private fun PlaceImage(modifier: Modifier = Modifier, url: String) {
	Box(
		modifier = modifier
			.background(color = MaterialTheme.colors.onSurface.copy(alpha = ContentAlpha.medium))
			.aspectRatio(4f / 3f)
			.fillMaxWidth()
	) {
		Image(
			modifier = Modifier.fillMaxSize(),
			contentScale = ContentScale.Crop,
			painter = rememberImagePainter(
				data = url,
				builder = {
					crossfade(true)
				}
			),
			contentDescription = null,
		)
	}
}
