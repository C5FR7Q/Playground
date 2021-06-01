package com.github.c5fr7q.playground.presentation.ui.util

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.github.c5fr7q.playground.domain.entity.Place
import com.github.c5fr7q.playground.domain.entity.Position
import com.github.c5fr7q.playground.presentation.R
import java.util.*

@Composable
fun Place.Category.asText(): String {
	return stringResource(
		when (this) {
			Place.Category.DISCOVERING -> R.string.discovering
			Place.Category.EATING -> R.string.eating
			Place.Category.GOING_OUT -> R.string.going_out
			Place.Category.HIKING -> R.string.hiking
			Place.Category.PLAYING -> R.string.playing
			Place.Category.RELAXING -> R.string.relaxing
			Place.Category.SHOPPING -> R.string.shopping
			Place.Category.SIGHTSEEING -> R.string.sightseeing
			Place.Category.SLEEPING -> R.string.sleeping
			Place.Category.DOING_SPORTS -> R.string.doing_sports
			Place.Category.TRAVELING -> R.string.traveling
		}
	)
}

@Composable
fun Position.asText() = "%.5f".format(Locale.US, lon) + "," + "%.5f".format(Locale.US, lat)