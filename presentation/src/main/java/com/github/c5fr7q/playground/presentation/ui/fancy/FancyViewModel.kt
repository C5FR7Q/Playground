package com.github.c5fr7q.playground.presentation.ui.fancy

import androidx.lifecycle.LiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.github.c5fr7q.playground.domain.repository.FancyRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.map
import javax.inject.Inject

@HiltViewModel
class FancyViewModel @Inject constructor(
	private val savedStateHandle: SavedStateHandle,
	private val fancyRepository: FancyRepository
) : ViewModel() {

	val state: LiveData<FancyState> =
		fancyRepository.getFancyData()
			.map { FancyState(it.name) }
			.asLiveData()

}