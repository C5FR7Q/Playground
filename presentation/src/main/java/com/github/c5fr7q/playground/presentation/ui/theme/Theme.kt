package com.github.c5fr7q.playground.presentation.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material.MaterialTheme
import androidx.compose.material.darkColors
import androidx.compose.material.lightColors
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorPalette = darkColors(
	primary = Bismark100,
	primaryVariant = Bismark700,
	secondary = Pomegranate,
	secondaryVariant = OrangePeel
)

private val LightColorPalette = lightColors(
	primary = Bismark500,
	primaryVariant = Bismark700,
	secondary = Pomegranate,
	secondaryVariant = OrangePeel,
	surface = Mercury,
	background = Mercury,
	onPrimary = Color.White,
	onSecondary = Color.White,
	onBackground = Color.Black,
	onSurface = Color.Black,
	onError = Color.White,
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