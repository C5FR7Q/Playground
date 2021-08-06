package com.github.c5fr7q.playground.presentation.manager

import android.content.Intent
import android.net.Uri
import androidx.compose.ui.window.DialogProperties
import androidx.navigation.NavHostController
import com.github.c5fr7q.playground.domain.entity.Position
import com.github.c5fr7q.playground.presentation.ui.Navigation
import com.github.c5fr7q.playground.presentation.ui.createRoute
import com.github.c5fr7q.playground.presentation.ui.widget.dialog.ConfirmationDialogModel
import com.github.c5fr7q.playground.presentation.ui.widget.dialog.DialogModel
import com.github.c5fr7q.playground.presentation.ui.widget.dialog.InputDialogModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NavigationManager @Inject constructor() : BaseManager() {
	private val dialogs = MutableStateFlow(emptyList<DialogModel>())

	val dialog = dialogs.map { it.firstOrNull() }

	val hasBackStack get() = navController!!.previousBackStackEntry != null

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

	fun openLiked() {
		navController?.navigate(Navigation.Liked.createRoute())
	}

	fun openConfirmationDialog(
		text: String,
		title: String = "",
		confirmButtonText: String = "",
		onDismissRequest: () -> Unit = {},
		onConfirmClick: () -> Unit = {},
		dialogProperties: DialogProperties = DialogProperties()
	) {
		addDialog(
			ConfirmationDialogModel(
				text = text,
				title = title,
				confirmButtonText = confirmButtonText,
				onDismissRequest = onDismissRequest,
				onConfirmClick = onConfirmClick,
				dialogProperties = dialogProperties
			)
		)
	}

	fun openInputDialog(
		title: String,
		onApplyValue: (Int) -> Unit,
		defaultValue: Int = 0,
		confirmButtonText: String = "",
		dismissButtonText: String = "",
		onDismissRequest: () -> Unit = {},
		dialogProperties: DialogProperties = DialogProperties()
	) {
		addDialog(
			InputDialogModel(
				title = title,
				defaultValue = defaultValue,
				onApplyValue = onApplyValue,
				confirmButtonText = confirmButtonText,
				dismissButtonText = dismissButtonText,
				onDismissRequest = onDismissRequest,
				dialogProperties = dialogProperties
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