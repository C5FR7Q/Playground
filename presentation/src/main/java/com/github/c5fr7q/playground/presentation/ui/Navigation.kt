package com.github.c5fr7q.playground.presentation.ui

sealed class Navigation<A> {
	abstract val destination: String
	abstract fun createRoute(args: A): String

	object Main : Navigation<Unit>() {
		override val destination = "main"
		override fun createRoute(args: Unit) = destination
	}

	object Blocked : Navigation<Unit>() {
		override val destination = "blocked"
		override fun createRoute(args: Unit) = destination
	}

	object Settings : Navigation<Unit>() {
		override val destination = "settings"
		override fun createRoute(args: Unit) = destination
	}
}

fun Navigation<Unit>.createRoute() = createRoute(Unit)
