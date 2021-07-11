package com.github.c5fr7q.playground.presentation.ui.widget

import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.updateTransition
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.dp
import com.github.c5fr7q.playground.domain.entity.Place
import com.github.c5fr7q.playground.presentation.ui.util.asText
import com.github.c5fr7q.playground.presentation.ui.util.transition.VisibilityTransitionState

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

			val colors = when {
				selected -> ButtonDefaults.buttonColors(
					contentColor = MaterialTheme.colors.surface,
					backgroundColor = MaterialTheme.colors.secondaryVariant
				)
				else -> ButtonDefaults.outlinedButtonColors(
					backgroundColor = Color.Transparent
				)
			}

			val border = when {
				selected -> null
				else -> BorderStroke(ButtonDefaults.OutlinedBorderSize, MaterialTheme.colors.secondaryVariant)
			}

			val elevation = when {
				selected -> ButtonDefaults.elevation()
				else -> null
			}

			val textColor = when {
				selected -> MaterialTheme.colors.surface
				else -> MaterialTheme.colors.secondaryVariant
			}

			var transitionState by remember { mutableStateOf(VisibilityTransitionState.HIDDEN) }

			LaunchedEffect(null) {
				transitionState = VisibilityTransitionState.SHOWN
			}

			val transition = updateTransition(targetState = transitionState, label = "ToggleTransition")

			val alpha by transition.animateFloat(label = "ToggleTransition_alpha") { state ->
				when (state) {
					VisibilityTransitionState.HIDDEN -> 0.8f
					VisibilityTransitionState.SHOWN -> 1f
				}
			}

			val scale by transition.animateFloat(label = "ToggleTransition_scale") { state ->
				when (state) {
					VisibilityTransitionState.HIDDEN -> 0.5f
					VisibilityTransitionState.SHOWN -> 1f
				}
			}

			Button(
				modifier = Modifier
					.padding(
						start = if (index == 0) 16.dp else 0.dp,
						end = if (index == lastIndex) 16.dp else 6.dp
					)
					.graphicsLayer(alpha = alpha, scaleY = scale, scaleX = scale),
				contentPadding = PaddingValues(4.dp),
				onClick = { onCategoryToggle(category) },
				colors = colors,
				border = border,
				elevation = elevation
			) {
				Text(text = category.asText(), style = MaterialTheme.typography.button.copy(color = textColor))
			}
		}
	}
}