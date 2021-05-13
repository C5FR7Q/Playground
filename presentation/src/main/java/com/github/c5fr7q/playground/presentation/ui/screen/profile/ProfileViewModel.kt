package com.github.c5fr7q.playground.presentation.ui.screen.profile

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.github.c5fr7q.playground.domain.entity.Place
import com.github.c5fr7q.playground.domain.repository.PlaceRepository
import com.github.c5fr7q.playground.domain.repository.ProfileRepository
import com.github.c5fr7q.playground.presentation.manager.NavigationManager
import com.github.c5fr7q.playground.presentation.ui.base.BaseViewModel
import com.github.c5fr7q.playground.presentation.ui.dialog.ConfirmationDialogModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
	savedStateHandle: SavedStateHandle,
	private val profileRepository: ProfileRepository,
	private val placeRepository: PlaceRepository,
	private val navigationManager: NavigationManager
) : BaseViewModel<ProfileState, ProfileIntent>() {
	private val userId: String = savedStateHandle[ProfileNavigation.Argument.USER_ID]!!

	override val mutableState: MutableStateFlow<ProfileState> = MutableStateFlow(ProfileState())

	override fun attach() {
		super.attach()
		produceIntent(ProfileIntent.Init)
	}

	override fun handleIntent(intent: ProfileIntent) {
		when (intent) {
			ProfileIntent.Init -> {
				profileRepository.getUserName(userId)
					.onEach { updateState { copy(userName = it) } }
					.launchIn(viewModelScope)
			}
			ProfileIntent.Click -> {
				navigationManager.openConfirmationDialog(
					ConfirmationDialogModel(
						"Change content?",
						"This content will be replaced with something cool",
						"Confirm"
					) {
						placeRepository.getPlaces(listOf(Place.Category.DISCOVERING), 5000).launchIn(viewModelScope)
						updateState { copy(userName = "ABRACADABRA") }
					})
			}
		}
	}
}