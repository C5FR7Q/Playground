package com.github.c5fr7q.playground.presentation.ui

import android.util.Log
import androidx.compose.runtime.*
import androidx.hilt.navigation.compose.hiltNavGraphViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.github.c5fr7q.playground.presentation.manager.NavigationManager
import com.github.c5fr7q.playground.presentation.ui.dialog.ConfirmationDialog
import com.github.c5fr7q.playground.presentation.ui.dialog.ConfirmationDialogModel
import com.github.c5fr7q.playground.presentation.ui.screen.fancy.FancyNavigation
import com.github.c5fr7q.playground.presentation.ui.screen.fancy.FancyScreen
import com.github.c5fr7q.playground.presentation.ui.screen.profile.ProfileNavigation
import com.github.c5fr7q.playground.presentation.ui.screen.profile.ProfileScreen
import com.github.c5fr7q.playground.presentation.ui.theme.PlaygroundTheme

@Composable
fun Main(navigationManager: NavigationManager) {
	PlaygroundTheme {
		val navController = rememberNavController()
		DisposableEffect(navController) {
			navigationManager.navController = navController
			onDispose { navigationManager.navController = null }
		}

		NavHost(navController = navController, startDestination = FancyNavigation.destination) {
			composable(FancyNavigation.destination) {
				FancyScreen(hiltNavGraphViewModel())
			}
			composable(ProfileNavigation.destination, arguments = ProfileNavigation.arguments) {
				ProfileScreen(hiltNavGraphViewModel())
			}
		}

		val dialog by navigationManager.dialog.collectAsState(initial = null)
		dialog?.let {
			val defaultOnDismissRequest = remember { { navigationManager.closeDialog() } }
			when (it) {
				is ConfirmationDialogModel -> ConfirmationDialog(defaultOnDismissRequest, it)
				else -> Log.d("Main", "Unknown dialog $it")
			}
		}
	}
}