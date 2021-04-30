package com.github.c5fr7q.playground.presentation.manager

import androidx.navigation.NavHostController
import androidx.navigation.compose.navigate
import com.github.c5fr7q.playground.presentation.ui.screen.profile.ProfileNavigation
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NavigationManager @Inject constructor() {
	var navController: NavHostController? = null

	fun openProfile(userId: String) {
		navController?.navigate(ProfileNavigation.createRoute(userId = userId))
	}
}