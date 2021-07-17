package com.github.c5fr7q.playground.domain.usecase.settings

import com.github.c5fr7q.playground.domain.repository.SettingsRepository
import javax.inject.Inject

class GetPlacesRadiusUseCase @Inject constructor(
	private val settingsRepository: SettingsRepository
) {
	fun execute() = settingsRepository.getPlacesRadius()
}