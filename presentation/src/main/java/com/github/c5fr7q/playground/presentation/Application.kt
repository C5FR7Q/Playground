package com.github.c5fr7q.playground.presentation

import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import com.github.c5fr7q.playground.presentation.theme.PlaygroundTheme
import com.github.c5fr7q.playground.presentation.ui.fancy.FancyScreen

@Composable
fun Application() {
	PlaygroundTheme {
		// A surface container using the 'background' color from the theme
		Surface(color = MaterialTheme.colors.background) {
			FancyScreen(viewModel())
		}
	}
}