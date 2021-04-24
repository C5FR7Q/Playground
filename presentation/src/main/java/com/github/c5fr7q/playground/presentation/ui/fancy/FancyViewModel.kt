package com.github.c5fr7q.playground.presentation.ui.fancy

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.github.c5fr7q.playground.domain.repository.FancyRepository
import kotlinx.coroutines.flow.map

class FancyViewModel(
	private val fancyRepository: FancyRepository
) : ViewModel() {

	val state: LiveData<FancyState> =
		fancyRepository.getFancyData()
			.map { FancyState(it.name) }
			.asLiveData()

}