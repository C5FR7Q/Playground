package com.github.c5fr7q.playground.presentation.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material.MaterialTheme
import androidx.compose.material.darkColors
import androidx.compose.material.lightColors
import androidx.compose.runtime.Composable

private val DarkColorPalette = darkColors(
	primary = FountainBlue200,
	primaryVariant = FountainBlue900,
	secondary = Tamarillo300,
	secondaryVariant = OrangePeel500
)

private val LightColorPalette = lightColors(
	primary = FountainBlue800,
	primaryVariant = FountainBlue900,
	secondary = Tamarillo300,
	secondaryVariant = OrangePeel500,
	surface = FountainBlue50,
	background = FountainBlue50

	/* Other default colors to override
    background = Color.White,
    surface = Color.White,
    onPrimary = Color.White,
    onSecondary = Color.Black,
    onBackground = Color.Black,
    onSurface = Color.Black,
    */
)

@Composable
fun PlaygroundTheme(darkTheme: Boolean = isSystemInDarkTheme(), content: @Composable() () -> Unit) {
	val colors = if (darkTheme) {
		DarkColorPalette
	} else {
		LightColorPalette
	}

	MaterialTheme(
		colors = colors,
		typography = Typography,
		shapes = Shapes,
		content = content
	)
}