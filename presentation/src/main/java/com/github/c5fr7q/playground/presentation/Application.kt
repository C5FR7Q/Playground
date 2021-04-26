package com.github.c5fr7q.playground.presentation

import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltNavGraphViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.github.c5fr7q.playground.presentation.theme.PlaygroundTheme
import com.github.c5fr7q.playground.presentation.ui.fancy.FancyScreen

@Composable
fun Application() {
	PlaygroundTheme {
		val navController = rememberNavController()
		NavHost(navController = navController, startDestination = "fancy_screen") {
			composable("fancy_screen") {
				FancyScreen(hiltNavGraphViewModel())
			}
		}
	}
}