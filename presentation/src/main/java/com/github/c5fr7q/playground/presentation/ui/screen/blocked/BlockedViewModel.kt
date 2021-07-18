package com.github.c5fr7q.playground.presentation.ui.screen.blocked

import com.github.c5fr7q.playground.domain.entity.Place
import com.github.c5fr7q.playground.domain.usecase.place.GetBlockedPlacesUseCase
import com.github.c5fr7q.playground.domain.usecase.place.UnblockPlacesUseCase
import com.github.c5fr7q.playground.presentation.ui.screen.common.selectable.SelectionViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class BlockedViewModel @Inject constructor(
	private val getBlockedPlacesUseCase: GetBlockedPlacesUseCase,
	private val unblockPlacesUseCase: UnblockPlacesUseCase
) : SelectionViewModel() {
	override fun getPlaces() = getBlockedPlacesUseCase.execute()
	override fun applySelection(places: List<Place>) = unblockPlacesUseCase.execute(places)
}