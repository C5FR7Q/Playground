package com.github.c5fr7q.playground.presentation

import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import com.github.c5fr7q.playground.presentation.theme.PlaygroundTheme

@Composable
fun Application() {
	PlaygroundTheme {
		// A surface container using the 'background' color from the theme
		Surface(color = MaterialTheme.colors.background) {
			Greeting("Android")
		}
	}
}

@Composable
fun Greeting(name: String) {
	Text(text = "Hello $name!")
}