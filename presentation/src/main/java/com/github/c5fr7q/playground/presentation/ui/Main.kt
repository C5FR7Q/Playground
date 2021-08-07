package com.github.c5fr7q.playground.presentation.ui

import androidx.compose.animation.*
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.VisibilityThreshold
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.unit.IntOffset
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavDeepLink
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.NamedNavArgument
import com.github.c5fr7q.playground.presentation.manager.NavigationManager
import com.github.c5fr7q.playground.presentation.ui.base.BaseViewModel
import com.github.c5fr7q.playground.presentation.ui.screen.blocked.BlockedScreen
import com.github.c5fr7q.playground.presentation.ui.screen.liked.LikedScreen
import com.github.c5fr7q.playground.presentation.ui.screen.main.MainScreen
import com.github.c5fr7q.playground.presentation.ui.screen.settings.SettingsScreen
import com.github.c5fr7q.playground.presentation.ui.theme.PlaygroundTheme
import com.google.accompanist.insets.ProvideWindowInsets
import com.google.accompanist.navigation.animation.AnimatedNavHost
import com.google.accompanist.navigation.animation.composable
import com.google.accompanist.navigation.animation.rememberAnimatedNavController

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun Main(navigationManager: NavigationManager) {
	val navController = rememberAnimatedNavController()
	DisposableEffect(navController) {
		navigationManager.navController = navController
		onDispose { navigationManager.navController = null }
	}

	PlaygroundTheme {
		ProvideWindowInsets {
			CompositionLocalProvider(
				LocalOnHomeClick provides { navigationManager.closeScreen() },
				LocalShowHomeButton provides { navigationManager.hasBackStack }
			) {
				AnimatedNavHost(navController = navController, startDestination = Navigation.Main.destination) {
					animatedComposable(Navigation.Main.destination) {
						MainScreen(baseViewModel())
					}
					animatedComposable(Navigation.Settings.destination) {
						SettingsScreen(baseViewModel())
					}
					animatedComposable(Navigation.Blocked.destination) {
						BlockedScreen(baseViewModel())
					}
					animatedComposable(Navigation.Liked.destination) {
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

@OptIn(ExperimentalAnimationApi::class)
private fun NavGraphBuilder.animatedComposable(
	route: String,
	arguments: List<NamedNavArgument> = emptyList(),
	deepLinks: List<NavDeepLink> = emptyList(),
	enterTransition: ((initial: NavBackStackEntry, target: NavBackStackEntry) -> EnterTransition?)? = { _, _ ->
		slideInHorizontally(
			initialOffsetX = { it },
			animationSpec = tween(durationMillis = 800)
		)
	},
	exitTransition: ((initial: NavBackStackEntry, target: NavBackStackEntry) -> ExitTransition?)? = { _, _ ->
		slideOutHorizontally(
			targetOffsetX = { -it / 2 },
			animationSpec = tween(durationMillis = 800)
		)
	},
	popEnterTransition: ((initial: NavBackStackEntry, target: NavBackStackEntry) -> EnterTransition?)? = { _, _ ->
		slideInHorizontally(
			initialOffsetX = { -it },
			animationSpec = tween(durationMillis = 800)
		)
	},
	popExitTransition: ((initial: NavBackStackEntry, target: NavBackStackEntry) -> ExitTransition?)? = { _, _ ->
		slideOutHorizontally(
			targetOffsetX = { it / 2 },
			animationSpec = tween(durationMillis = 800)
		)
	},
	content: @Composable (NavBackStackEntry) -> Unit
) {
	composable(
		route = route,
		arguments = arguments,
		deepLinks = deepLinks,
		enterTransition = enterTransition,
		exitTransition = exitTransition,
		popEnterTransition = popEnterTransition,
		popExitTransition = popExitTransition,
		content = content
	)
}
