package com.github.c5fr7q.playground.presentation.ui.widget

import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.runtime.*
import androidx.compose.ui.res.stringResource

@Composable
fun OptionsMenu(options: List<OptionsMenuItemModel>) {
	var showMenu by remember { mutableStateOf(false) }
	IconButton(onClick = { showMenu = true }) {
		Icon(Icons.Default.MoreVert, contentDescription = null)
		DropdownMenu(
			expanded = showMenu,
			onDismissRequest = { showMenu = false })
		{
			options.forEach { option ->
				DropdownMenuItem(onClick = {
					showMenu = false
					option.onClick()
				}) {
					Text(
						text = when (option.title) {
							is OptionsMenuItemModel.Title.Res -> stringResource(option.title.stringRes)
							is OptionsMenuItemModel.Title.Str -> option.title.string
						}
					)
				}
			}
		}
	}
}

data class OptionsMenuItemModel(
	val title: Title,
	val onClick: () -> Unit
) {
	sealed class Title {
		data class Res(val stringRes: Int) : Title()
		data class Str(val string: String) : Title()
	}
}