package com.github.c5fr7q.playground.presentation.manager

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NavigationManager @Inject constructor() {
	private val _commands = MutableStateFlow<Route?>(null)
	val commands: StateFlow<Route?> = _commands

	fun navigate(route: Route) {
		_commands.value = route
	}
}

data class Route(val value: String)