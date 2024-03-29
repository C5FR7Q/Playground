package com.github.c5fr7q.playground.presentation.ui.widget

import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
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
					Icon(
						painter = painterResource(
							id = if (place.isFavorite)
								R.drawable.ic_favorite_24 else
								R.drawable.ic_favorite_border_24
						),
						contentDescription = null
					)
				}
				Spacer(modifier = Modifier.size(6.dp))
				Text(modifier = Modifier.weight(1f), text = place.name, style = MaterialTheme.typography.h4)
				Spacer(modifier = Modifier.size(6.dp))
				OptionsMenu(options = mutableListOf<OptionsMenuItemModel>().apply {
					add(OptionsMenuItemModel(stringResource(id = R.string.block), onBlockClick))
					add(OptionsMenuItemModel(stringResource(id = R.string.show_in_maps), onShowInMapsClick))
				})
			}
			Text(
				modifier = Modifier.padding(horizontal = 16.dp),
				text = place.position.asText(),
				style = MaterialTheme.typography.overline.copy(
					color = MaterialTheme.colors.onSurface.copy(alpha = ContentAlpha.medium)
				)
			)
			Spacer(modifier = Modifier.height(2.dp))
			PlaceCategoryRow(
				modifier = Modifier
					.padding(horizontal = 16.dp)
					.wrapContentWidth(),
				place = place
			)
			Spacer(modifier = Modifier.height(6.dp))
			PlaceTagRow(modifier = Modifier.padding(horizontal = 16.dp), place = place)
			Spacer(modifier = Modifier.height(6.dp))
			RatedImage(modifier = Modifier.padding(horizontal = 16.dp), url = place.imageUrl, rating = place.rating)
			Spacer(modifier = Modifier.height(16.dp))
		}
	}
}