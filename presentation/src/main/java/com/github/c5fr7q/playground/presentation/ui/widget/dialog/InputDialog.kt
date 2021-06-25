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
import com.github.c5fr7q.playground.presentation.R

@Composable
fun InputDialog(
	title: String,
	defaultValue: Int,
	onDismissDialog: () -> Unit,
	onInputValue: (Int) -> Unit
) {
	var inputValue by remember { mutableStateOf(defaultValue.toString()) }
	val submit = {
		inputValue.takeIf { it.isNotEmpty() }?.let { onInputValue(it.toInt()) }
		onDismissDialog()
	}
	AlertDialog(
		onDismissRequest = onDismissDialog,
		title = { Text(text = title) },
		confirmButton = {
			Button(onClick = submit) {
				Text(text = stringResource(id = R.string.apply))
			}
		},
		dismissButton = {
			Button(onClick = onDismissDialog) {
				Text(text = stringResource(id = R.string.cancel))
			}
		},
		text = {
			TextField(
				value = inputValue,
				onValueChange = { inputValue = it },
				keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number, imeAction = ImeAction.Done),
				keyboardActions = KeyboardActions(onDone = { submit() })
			)
		}
	)
}
