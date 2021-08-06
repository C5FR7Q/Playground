package com.github.c5fr7q.playground.presentation.ui.widget

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.foundation.layout.RowScope
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Dp
import com.github.c5fr7q.playground.presentation.R
import com.github.c5fr7q.playground.presentation.ui.LocalOnHomeClick
import com.github.c5fr7q.playground.presentation.ui.LocalShowHomeButton


@Composable
fun DefaultTopAppBar(
	modifier: Modifier = Modifier,
	@StringRes titleResId: Int = R.string.app_name,
	@DrawableRes navigationIconRes: Int = R.drawable.ic_arrow_back_24,
	actions: @Composable RowScope.() -> Unit = {},
	backgroundColor: Color = MaterialTheme.colors.primarySurface,
	contentColor: Color = contentColorFor(backgroundColor),
	elevation: Dp = AppBarDefaults.TopAppBarElevation
) {
	val navigationIcon: @Composable () -> Unit = {
		IconButton(onClick = LocalOnHomeClick.current) {
			Icon(painter = painterResource(id = navigationIconRes), contentDescription = null)
		}
	}

	TopAppBar(
		title = {
			Text(
				text = stringResource(titleResId),
				style = MaterialTheme.typography.h5
			)
		},
		modifier = modifier,
		navigationIcon = if (LocalShowHomeButton.current()) navigationIcon else null,
		actions = actions,
		backgroundColor = backgroundColor,
		contentColor = contentColor,
		elevation = elevation
	)
}