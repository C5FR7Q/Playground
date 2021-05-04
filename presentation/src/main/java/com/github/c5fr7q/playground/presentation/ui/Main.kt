package com.github.c5fr7q.playground.presentation.ui

import android.util.Log
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.hilt.navigation.compose.hiltNavGraphViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
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
import com.github.c5fr7q.playground.presentation.ui.base.BaseViewModel

val LocalOnDismissRequest = compositionLocalOf<() -> Unit> { error("localOnDismissRequest is not specified") }

@Composable
fun Main(navigationManager: NavigationManager) {
	val navController = rememberNavController()
	DisposableEffect(navController) {
		navigationManager.navController = navController
		onDispose { navigationManager.navController = null }
	}

	PlaygroundTheme {

		NavHost(navController = navController, startDestination = FancyNavigation.destination) {
			composable(FancyNavigation.destination) {
				FancyScreen(baseViewModel())
			}
			composable(ProfileNavigation.destination, arguments = ProfileNavigation.arguments) {
				ProfileScreen(baseViewModel())
			}
		}

		CompositionLocalProvider(LocalOnDismissRequest provides { navigationManager.closeDialog() }) {
			val dialog by navigationManager.dialog.collectAsState(initial = null)
			dialog?.let {
				when (it) {
					is ConfirmationDialogModel -> ConfirmationDialog(it)
					else -> Log.d("Main", "Unknown dialog $it")
				}
			}
		}
	}
}

@Composable
fun OnLifecycleEvent(onEvent: (event: Lifecycle.Event) -> Unit) {
	val eventHandler = rememberUpdatedState(onEvent)
	val lifecycleOwner = rememberUpdatedState(LocalLifecycleOwner.current)

	DisposableEffect(lifecycleOwner.value) {
		val lifecycle = lifecycleOwner.value.lifecycle
		val observer = LifecycleEventObserver { _, event ->
			eventHandler.value(event)
		}

		lifecycle.addObserver(observer)
		onDispose {
			lifecycle.removeObserver(observer)
		}
	}
}

@Composable
inline fun <reified VM : BaseViewModel<*, *>> baseViewModel(): VM {
	val viewModel = hiltNavGraphViewModel<VM>()
	OnLifecycleEvent { event ->
		when (event) {
			Lifecycle.Event.ON_START -> viewModel.attach()
			Lifecycle.Event.ON_STOP -> viewModel.detach()
			else -> Unit
		}
	}
	return viewModel
}

