package com.github.c5fr7q.playground.presentation.ui.screen.profile

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.github.c5fr7q.playground.domain.repository.ProfileRepository
import com.github.c5fr7q.playground.presentation.ui.util.StatefulViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
	savedStateHandle: SavedStateHandle,
	profileRepository: ProfileRepository
) : StatefulViewModel<ProfileState>() {
	private val userId: String = savedStateHandle[ProfileNavigation.Argument.USER_ID]!!

	override val mutableState: MutableStateFlow<ProfileState> = MutableStateFlow(ProfileState())

	init {
		profileRepository.getUserName(userId)
			.onEach { updateState { copy(userName = it) } }
			.launchIn(viewModelScope)
	}
}