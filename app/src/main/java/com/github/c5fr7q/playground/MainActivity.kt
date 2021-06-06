package com.github.c5fr7q.playground

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.github.c5fr7q.playground.presentation.manager.NavigationManager
import com.github.c5fr7q.playground.presentation.ui.Main
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

	@Inject
	lateinit var navigationManager: NavigationManager

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		navigationManager.attachActivity(this)
		setContent {
			Main(navigationManager)
		}
	}

	override fun onDestroy() {
		navigationManager.detachActivity()
		super.onDestroy()
	}
}