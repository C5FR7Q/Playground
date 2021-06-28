package com.github.c5fr7q.playground.presentation.ui.widget

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.github.c5fr7q.playground.domain.entity.Place
import com.github.c5fr7q.playground.presentation.ui.util.asText

@Composable
fun SelectCategoriesRow(
	modifier: Modifier = Modifier,
	selectedCategories: List<Place.Category>,
	onCategoryToggle: (Place.Category) -> Unit
) {
	LazyRow(modifier = modifier.padding(vertical = 6.dp)) {
		val allCategories = Place.Category.values()
		val lastIndex = allCategories.lastIndex
		itemsIndexed(allCategories) { index, category ->
			val selected = selectedCategories.contains(category)
			if (selected) {
				Button(
					modifier = Modifier.padding(
						start = if (index == 0) 16.dp else 0.dp,
						end = if (index == lastIndex) 16.dp else 6.dp
					),
					contentPadding = PaddingValues(4.dp),
					onClick = { onCategoryToggle(category) },
					colors = ButtonDefaults.buttonColors(
						contentColor = MaterialTheme.colors.surface
					)
				) {
					Text(text = category.asText(), style = MaterialTheme.typography.button)
				}
			} else {
				OutlinedButton(
					modifier = Modifier.padding(
						start = if (index == 0) 16.dp else 0.dp,
						end = if (index == lastIndex) 16.dp else 6.dp,
					),
					contentPadding = PaddingValues(4.dp),
					onClick = { onCategoryToggle(category) },
					colors = ButtonDefaults.outlinedButtonColors(
						backgroundColor = Color.Transparent
					),
					border = BorderStroke(ButtonDefaults.OutlinedBorderSize, MaterialTheme.colors.primary)
				) {
					Text(text = category.asText(), style = MaterialTheme.typography.button, color = MaterialTheme.colors.primary)
				}
			}
		}
	}

}