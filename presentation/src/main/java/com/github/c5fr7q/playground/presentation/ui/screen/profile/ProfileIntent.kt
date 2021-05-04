package com.github.c5fr7q.playground.presentation.ui.screen.profile

sealed class ProfileIntent {
	object Init: ProfileIntent()
	object Click: ProfileIntent()
}