package com.github.c5fr7q.playground.presentation.ui.widget.dialog

import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.AlertDialog
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.runtime.*
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.window.DialogProperties
import com.github.c5fr7q.playground.presentation.R
import com.github.c5fr7q.playground.presentation.ui.LocalOnDismissRequest

data class InputDialogModel(
	val title: String,
	val defaultValue: Int,
	val onApplyValue: (Int) -> Unit,
	val confirmButtonText: String,
	val dismissButtonText: String,
	val onDismissRequest: () -> Unit,
	val dialogProperties: DialogProperties
) : DialogModel {
	@Composable
	override fun Draw() {
		val globalOnDismissRequest = LocalOnDismissRequest.current

		var inputValue by remember { mutableStateOf(defaultValue.toString()) }
		val submit = {
			inputValue.takeIf { it.isNotEmpty() }?.let { onApplyValue(it.toInt()) }
			globalOnDismissRequest()
		}
		AlertDialog(
			onDismissRequest = {
				onDismissRequest()
				globalOnDismissRequest()
			},
			title = { Text(text = title) },
			confirmButton = {
				Button(onClick = submit) {
					Text(text = confirmButtonText.ifEmpty { stringResource(id = R.string.apply) })
				}
			},
			dismissButton = {
				Button(onClick = globalOnDismissRequest) {
					Text(text = dismissButtonText.ifEmpty { stringResource(id = R.string.cancel) })
				}
			},
			text = {
				TextField(
					value = inputValue,
					onValueChange = { inputValue = it },
					keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number, imeAction = ImeAction.Done),
					keyboardActions = KeyboardActions(onDone = { submit() })
				)
			},
			properties = dialogProperties
		)

	}
}
