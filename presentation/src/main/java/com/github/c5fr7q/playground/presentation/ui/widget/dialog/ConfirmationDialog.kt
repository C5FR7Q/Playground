package com.github.c5fr7q.playground.presentation.ui.widget.dialog

import androidx.compose.material.AlertDialog
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.window.DialogProperties
import com.github.c5fr7q.playground.presentation.R
import com.github.c5fr7q.playground.presentation.ui.LocalOnDismissRequest

data class ConfirmationDialogModel(
	val text: String,
	val title: String,
	val onConfirmClick: () -> Unit,
	val confirmButtonText: String,
	val onDismissRequest: () -> Unit,
	val dialogProperties: DialogProperties
) : DialogModel {
	@Composable
	override fun Draw() {
		val cTitle: @Composable () -> Unit = { Text(text = title) }
		val globalOnDismissRequest = LocalOnDismissRequest.current
		AlertDialog(
			onDismissRequest = {
				onDismissRequest()
				globalOnDismissRequest()
			},
			title = if (title.isNotEmpty()) cTitle else null,
			text = { Text(text = text) },
			confirmButton = {
				Button(onClick = {
					onConfirmClick()
					onDismissRequest()
				}) {
					Text(text = confirmButtonText.ifEmpty { stringResource(id = R.string.ok) })
				}
			},
			properties = dialogProperties
		)

	}
}