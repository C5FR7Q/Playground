package com.github.c5fr7q.playground.presentation.ui.dialog

import androidx.compose.material.AlertDialog
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable

data class ConfirmationDialogModel(
	val title: String,
	val text: String,
	val confirmButtonText: String,
	val onConfirmed: () -> Unit
) : DialogModel

@Composable
fun ConfirmationDialog(defaultOnDismissRequest: () -> Unit, model: ConfirmationDialogModel) {
	AlertDialog(
		onDismissRequest = defaultOnDismissRequest,
		title = { Text(text = model.title) },
		text = { Text(text = model.text) },
		confirmButton = {
			Button(onClick = {
				model.onConfirmed()
				defaultOnDismissRequest()
			}) {
				Text(text = model.confirmButtonText)
			}
		}
	)
}