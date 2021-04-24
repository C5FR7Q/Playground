package com.github.c5fr7q.playground.presentation

import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import com.github.c5fr7q.playground.data.repository.FancyRepositoryImpl
import com.github.c5fr7q.playground.presentation.theme.PlaygroundTheme
import com.github.c5fr7q.playground.presentation.ui.fancy.FancyScreen
import com.github.c5fr7q.playground.presentation.ui.fancy.FancyViewModel

@Composable
fun Application() {
	PlaygroundTheme {
		// A surface container using the 'background' color from the theme
		Surface(color = MaterialTheme.colors.background) {
			FancyScreen(FancyViewModel(FancyRepositoryImpl()))
		}
	}
}