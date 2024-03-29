package com.github.c5fr7q.playground.presentation.ui.widget

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.ContentAlpha
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil.compose.rememberImagePainter
import java.util.*
import kotlin.math.abs

@Composable
fun RatedImage(
	modifier: Modifier = Modifier,
	url: String,
	rating: Float
) {
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

		Box(
			modifier = Modifier
				.background(color = MaterialTheme.colors.onSurface.copy(alpha = ContentAlpha.medium))
				.padding(4.dp)
		) {
			var fakeRating = abs(rating)
			while (fakeRating != 0f && fakeRating < 1f) {
				fakeRating *= 10
			}

			Text(
				text = "R:" + "%.2f".format(Locale.US, fakeRating),
				style = MaterialTheme.typography.overline,
				color = MaterialTheme.colors.onPrimary
			)
		}
	}
}
