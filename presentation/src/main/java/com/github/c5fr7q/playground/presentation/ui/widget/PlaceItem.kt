package com.github.c5fr7q.playground.presentation.ui.widget

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.github.c5fr7q.playground.domain.entity.Place
import com.github.c5fr7q.playground.presentation.R
import com.github.c5fr7q.playground.presentation.ui.util.asText

@Composable
fun PlaceItem(
	modifier: Modifier = Modifier,
	place: Place,
	onToggleFavoriteClick: () -> Unit,
	onBlockClick: () -> Unit,
	onShowInMapsClick: () -> Unit
) {
	Surface(modifier = modifier) {
		Column {
			Spacer(modifier = Modifier.height(16.dp))
			Row(
				modifier = Modifier
					.padding(horizontal = 4.dp)
					.fillMaxWidth(),
				verticalAlignment = Alignment.CenterVertically
			) {
				IconButton(onClick = onToggleFavoriteClick) {
					Icon(if (place.isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder, contentDescription = null)
				}
				Spacer(modifier = Modifier.size(6.dp))
				Text(modifier = Modifier.weight(1f), text = place.name, style = MaterialTheme.typography.h4)
				Spacer(modifier = Modifier.size(6.dp))
				OptionsMenu(options = mutableListOf<OptionsMenuItemModel>().apply {
					add(OptionsMenuItemModel(OptionsMenuItemModel.Title.Res(R.string.block), onBlockClick))
					add(OptionsMenuItemModel(OptionsMenuItemModel.Title.Res(R.string.show_in_maps), onShowInMapsClick))
				})
			}
			CompositionLocalProvider(LocalContentAlpha provides ContentAlpha.medium) {
				Text(
					modifier = Modifier.padding(horizontal = 16.dp),
					text = place.position.asText(),
					style = MaterialTheme.typography.overline
				)
			}
			Spacer(modifier = Modifier.height(2.dp))
			PlaceCategoryRow(
				modifier = Modifier
					.padding(horizontal = 16.dp)
					.wrapContentWidth(),
				place = place
			)
			Spacer(modifier = Modifier.height(6.dp))
			PlaceTagRow(modifier = Modifier.padding(horizontal = 16.dp), place = place)
			TagRow(modifier = Modifier.padding(horizontal = 16.dp), tags = place.tags) {
				Text(
					text = it,
					maxLines = 1,
					overflow = TextOverflow.Ellipsis,
					modifier = Modifier
						.background(MaterialTheme.colors.onSurface, RoundedCornerShape(4.dp))
						.padding(4.dp),
					style = MaterialTheme.typography.caption,
					color = MaterialTheme.colors.onPrimary
				)
			}
			Spacer(modifier = Modifier.height(6.dp))
			RatedImage(modifier = Modifier.padding(horizontal = 16.dp), url = place.imageUrl, rating = place.rating)
			Spacer(modifier = Modifier.height(16.dp))
		}
	}
}