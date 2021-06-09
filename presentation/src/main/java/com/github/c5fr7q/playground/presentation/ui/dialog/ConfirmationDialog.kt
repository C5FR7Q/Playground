package com.github.c5fr7q.playground.presentation.ui.dialog

import androidx.compose.material.AlertDialog
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.github.c5fr7q.playground.presentation.R
import com.github.c5fr7q.playground.presentation.ui.LocalOnDismissRequest

data class ConfirmationDialogModel(
	val text: String,
	val title: String,
	val confirmButtonText: String,
	val isCancelable: Boolean,
	val onDismissRequest: () -> Unit,
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
			Text(text = model.confirmButtonText
				.takeIf { it.isNotEmpty() }
				?: stringResource(id = R.string.ok))
		}
	}
) {
	AlertDialog(
		onDismissRequest = {
			if (model.isCancelable) {
				model.onDismissRequest()
				onDismissRequest()
			}
		},
		title = if (model.title.isNotEmpty()) title else null,
		text = text,
		confirmButton = button
	)
}