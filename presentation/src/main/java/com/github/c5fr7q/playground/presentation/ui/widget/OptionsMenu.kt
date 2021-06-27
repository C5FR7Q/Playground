package com.github.c5fr7q.playground.presentation.ui.widget

import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.runtime.*

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
					Text(text = option.title)
				}
			}
		}
	}
}

data class OptionsMenuItemModel(
	val title: String,
	val onClick: () -> Unit
)