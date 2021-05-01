package com.github.c5fr7q.playground.presentation.ui.dialog

import androidx.compose.material.AlertDialog
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import com.github.c5fr7q.playground.presentation.ui.LocalOnDismissRequest

data class ConfirmationDialogModel(
	val title: String,
	val text: String,
	val confirmButtonText: String,
	val onConfirmed: () -> Unit
) : DialogModel

@Composable
fun ConfirmationDialog(
	model: ConfirmationDialogModel,
	onDismissRequest: () -> Unit = LocalOnDismissRequest.current,
	title: @Composable () -> Unit = { Text(text = model.title) },
	text: @Composable () -> Unit = { Text(text = model.text) },
	button: @Composable () -> Unit = {
		Button(onClick = {
			model.onConfirmed()
			onDismissRequest()
		}) {
			Text(text = model.confirmButtonText)
		}
	}
) {
	AlertDialog(
		onDismissRequest = onDismissRequest,
		title = title,
		text = text,
		confirmButton = button
	)
}