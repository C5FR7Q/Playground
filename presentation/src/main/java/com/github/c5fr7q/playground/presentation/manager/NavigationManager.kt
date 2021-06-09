package com.github.c5fr7q.playground.presentation.manager

import android.content.Intent
import android.net.Uri
import androidx.navigation.NavHostController
import androidx.navigation.compose.navigate
import com.github.c5fr7q.playground.domain.entity.Position
import com.github.c5fr7q.playground.presentation.ui.Navigation
import com.github.c5fr7q.playground.presentation.ui.createRoute
import com.github.c5fr7q.playground.presentation.ui.dialog.ConfirmationDialogModel
import com.github.c5fr7q.playground.presentation.ui.dialog.DialogModel
import com.github.c5fr7q.playground.presentation.ui.screen.profile.ProfileNavigation
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NavigationManager @Inject constructor() : BaseManager() {
	private val dialogs = MutableStateFlow(emptyList<DialogModel>())

	val dialog = dialogs.map { it.firstOrNull() }

	var navController: NavHostController? = null

	fun openMaps(position: Position) {
		val intent = Intent(Intent.ACTION_VIEW, Uri.parse("geo:0,0?q=${position.lat},${position.lon}"))
		if (intent.resolveActivity(activity.packageManager) != null) {
			activity.startActivity(intent)
		}
	}

	fun openSettings() {
		navController?.navigate(Navigation.Settings.createRoute())
	}

	fun openBlocked() {
		navController?.navigate(Navigation.Blocked.createRoute())
	}

	fun openProfile(userId: String) {
		navController?.navigate(ProfileNavigation.createRoute(userId = userId))
	}

	fun openConfirmationDialog(
		text: String,
		title: String = "",
		confirmButtonText: String = "",
		isCancelable: Boolean = true,
		onDismissRequest: () -> Unit = {},
		onConfirmed: () -> Unit = {}
	) {
		addDialog(
			ConfirmationDialogModel(
				text = text,
				title = title,
				confirmButtonText = confirmButtonText,
				isCancelable = isCancelable,
				onDismissRequest = onDismissRequest,
				onConfirmed = onConfirmed
			)
		)
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