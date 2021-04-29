package com.github.c5fr7q.playground.presentation.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.hilt.navigation.compose.hiltNavGraphViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigate
import androidx.navigation.compose.rememberNavController
import com.github.c5fr7q.playground.presentation.manager.NavigationManager
import com.github.c5fr7q.playground.presentation.ui.screen.fancy.FancyNavigation
import com.github.c5fr7q.playground.presentation.ui.screen.fancy.FancyScreen
import com.github.c5fr7q.playground.presentation.ui.screen.profile.ProfileNavigation
import com.github.c5fr7q.playground.presentation.ui.screen.profile.ProfileScreen
import com.github.c5fr7q.playground.presentation.ui.theme.PlaygroundTheme

@Composable
fun Main(navigationManager: NavigationManager) {
	PlaygroundTheme {
		val navController = rememberNavController()
		navigationManager.commands.collectAsState().value?.let {
			navController.navigate(it.value)
		}

		NavHost(navController = navController, startDestination = FancyNavigation.destination) {
			composable(FancyNavigation.destination) {
				FancyScreen(hiltNavGraphViewModel())
			}
			composable(ProfileNavigation.destination, arguments = ProfileNavigation.arguments) {
				ProfileScreen(hiltNavGraphViewModel())
			}
		}
	}
}