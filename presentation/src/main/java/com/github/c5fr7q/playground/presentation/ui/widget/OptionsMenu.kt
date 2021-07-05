package com.github.c5fr7q.playground.presentation.ui.widget

import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.res.painterResource
import com.github.c5fr7q.playground.presentation.R

@Composable
fun OptionsMenu(options: List<OptionsMenuItemModel>) {
	var showMenu by remember { mutableStateOf(false) }
	IconButton(onClick = { showMenu = true }) {
		Icon(painter = painterResource(id = R.drawable.ic_more_vert_24), contentDescription = null)
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