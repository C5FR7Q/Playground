package com.github.c5fr7q.playground.presentation.ui.screen.profile

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.compose.navArgument
import com.github.c5fr7q.playground.presentation.manager.Route

object ProfileNavigation {
	object Argument {
		const val USER_ID = "userId"
	}

	const val destination = "profile/{${Argument.USER_ID}}"

	val arguments = listOf(
		navArgument(Argument.USER_ID) {}
	)

	fun createRoute(userId: String) = Route("profile/$userId")
}

@Composable
fun ProfileScreen(viewModel: ProfileViewModel) {
	val state by viewModel.state.collectAsState()
	ProfileScreen(
		state = state
	)
}

@Composable
private fun ProfileScreen(state: ProfileState) {
	Surface(
		modifier = Modifier.fillMaxSize(),
		color = MaterialTheme.colors.primary,
	) {
		Box(
			modifier = Modifier.fillMaxSize(),
			contentAlignment = Alignment.Center
		) {
			Text(
				style = MaterialTheme.typography.h3,
				text = state.userName
			)
		}
	}
}