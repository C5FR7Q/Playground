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
fun ConfirmationDialog(model: ConfirmationDialogModel) {
	val onDismissRequest = LocalOnDismissRequest.current
	AlertDialog(
		onDismissRequest = onDismissRequest,
		title = { Text(text = model.title) },
		text = { Text(text = model.text) },
		confirmButton = {
			Button(onClick = {
				model.onConfirmed()
				onDismissRequest()
			}) {
				Text(text = model.confirmButtonText)
			}
		}
	)
}