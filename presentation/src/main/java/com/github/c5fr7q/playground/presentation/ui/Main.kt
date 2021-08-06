package com.github.c5fr7q.playground.presentation.ui

import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.github.c5fr7q.playground.presentation.manager.NavigationManager
import com.github.c5fr7q.playground.presentation.ui.base.BaseViewModel
import com.github.c5fr7q.playground.presentation.ui.screen.blocked.BlockedScreen
import com.github.c5fr7q.playground.presentation.ui.screen.liked.LikedScreen
import com.github.c5fr7q.playground.presentation.ui.screen.main.MainScreen
import com.github.c5fr7q.playground.presentation.ui.screen.settings.SettingsScreen
import com.github.c5fr7q.playground.presentation.ui.theme.PlaygroundTheme
import com.google.accompanist.insets.ProvideWindowInsets

val LocalOnDismissRequest = compositionLocalOf<() -> Unit> { error("localOnDismissRequest is not specified") }
val LocalOnHomeClick = compositionLocalOf<() -> Unit> { error("LocalOnHomeClick is not specified") }

@Composable
fun Main(navigationManager: NavigationManager) {
	val navController = rememberNavController()
	DisposableEffect(navController) {
		navigationManager.navController = navController
		onDispose { navigationManager.navController = null }
	}

	PlaygroundTheme {
		ProvideWindowInsets {
			CompositionLocalProvider(LocalOnHomeClick provides { navigationManager.closeScreen() }) {
				NavHost(navController = navController, startDestination = Navigation.Main.destination) {
					composable(Navigation.Main.destination) {
						MainScreen(baseViewModel())
					}
					composable(Navigation.Settings.destination) {
						SettingsScreen(baseViewModel())
					}
					composable(Navigation.Blocked.destination) {
						BlockedScreen(baseViewModel())
					}
					composable(Navigation.Liked.destination) {
						LikedScreen(baseViewModel())
					}
				}
			}

			CompositionLocalProvider(LocalOnDismissRequest provides { navigationManager.closeDialog() }) {
				val dialog by navigationManager.dialog.collectAsState(initial = null)
				dialog?.Draw()
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
inline fun <reified VM : BaseViewModel<*, *, *>> baseViewModel(): VM {
	val viewModel = hiltViewModel<VM>()
	OnLifecycleEvent { event ->
		when (event) {
			Lifecycle.Event.ON_START -> viewModel.attach()
			Lifecycle.Event.ON_STOP -> viewModel.detach()
			else -> Unit
		}
	}
	return viewModel
}

