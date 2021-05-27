package com.github.c5fr7q.playground.presentation.manager

import androidx.navigation.NavHostController
import androidx.navigation.compose.navigate
import com.github.c5fr7q.playground.presentation.ui.dialog.ConfirmationDialogModel
import com.github.c5fr7q.playground.presentation.ui.dialog.DialogModel
import com.github.c5fr7q.playground.presentation.ui.screen.blocked.BlockedNavigation
import com.github.c5fr7q.playground.presentation.ui.screen.profile.ProfileNavigation
import com.github.c5fr7q.playground.presentation.ui.screen.settings.SettingsNavigation
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NavigationManager @Inject constructor() {
	private val dialogs = MutableStateFlow(emptyList<DialogModel>())

	val dialog = dialogs.map { it.firstOrNull() }

	var navController: NavHostController? = null

	fun openSettings() {
		navController?.navigate(SettingsNavigation.createRoute())
	}

	fun openBlocked() {
		navController?.navigate(BlockedNavigation.createRoute())
	}

	fun openProfile(userId: String) {
		navController?.navigate(ProfileNavigation.createRoute(userId = userId))
	}

	fun openConfirmationDialog(model: ConfirmationDialogModel) {
		addDialog(model)
	}

	fun closeDialog() {
		val dialogsList = dialogs.value
		if (dialogsList.isNotEmpty()) {
			dialogs.value = dialogsList.toMutableList().apply { removeFirst() }
		}
	}

	fun closeScreen() {
		navController?.popBackStack()
	}

	private fun addDialog(dialogModel: DialogModel) {
		dialogs.value = dialogs.value + dialogModel
	}
}