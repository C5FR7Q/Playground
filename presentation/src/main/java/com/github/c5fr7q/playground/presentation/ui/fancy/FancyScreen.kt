package com.github.c5fr7q.playground.presentation.ui.fancy

import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState

@Composable
fun FancyScreen(viewModel: FancyViewModel) {
	val state by viewModel.state.observeAsState(FancyState.Default)
	FancyScreen(state)
}

@Composable
private fun FancyScreen(fancyState: FancyState) {
	Text(text = "Hello ${fancyState.name}!")
}