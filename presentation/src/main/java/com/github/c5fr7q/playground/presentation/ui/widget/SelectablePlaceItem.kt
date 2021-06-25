package com.github.c5fr7q.playground.presentation.ui.widget

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import com.github.c5fr7q.playground.domain.entity.Place
import com.github.c5fr7q.playground.presentation.ui.util.CustomContentAlpha
import com.github.c5fr7q.playground.presentation.ui.util.asText
import com.google.accompanist.coil.rememberCoilPainter

@Composable
fun SelectablePlaceItem(
	place: Place,
	isSelected: Boolean,
	onPlaceClick: () -> Unit
) {
	val backgroundColor = when {
		isSelected -> MaterialTheme.colors.primary.copy(alpha = CustomContentAlpha.pressed)
		else -> Color.Transparent
	}
	Box(
		modifier = Modifier
			.background(color = backgroundColor)
			.clickable(onClick = onPlaceClick)
	) {
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
				CompositionLocalProvider(LocalContentAlpha provides ContentAlpha.medium) {
					Text(
						text = place.position.asText(),
						style = MaterialTheme.typography.overline
					)
				}
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
			.clip(RoundedCornerShape(4.dp))
			.background(color = MaterialTheme.colors.onSurface.copy(alpha = ContentAlpha.medium))
			.aspectRatio(4f / 3f)
			.fillMaxWidth()
	) {
		Image(
			modifier = Modifier.fillMaxSize(),
			contentScale = ContentScale.Crop,
			painter = rememberCoilPainter(
				request = url,
				fadeIn = true,
				shouldRefetchOnSizeChange = { _, _ -> false },
			),
			contentDescription = null,
		)
	}
}
