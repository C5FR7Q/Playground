package com.github.c5fr7q.playground.presentation.ui

import androidx.compose.runtime.compositionLocalOf

/* Provides default dismiss dialogue impl */
val LocalOnDismissRequest = localOf<() -> Unit>()

/* Provides default home button click impl */
val LocalOnHomeClick = localOf<() -> Unit>()

/* Provides default check whether to show home button */
val LocalShowHomeButton = localOf<() -> Boolean>()

private fun <T> localOf() = compositionLocalOf<T> { error("not specified") }