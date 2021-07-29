package com.github.c5fr7q.playground.domain.usecase.settings

import com.github.c5fr7q.playground.domain.repository.SettingsRepository
import java.time.Duration
import javax.inject.Inject

class SetDataCachingTimeUseCase @Inject constructor(
	private val settingsRepository: SettingsRepository
) {
	operator fun invoke(duration: Duration) = settingsRepository.setDataCachingTime(duration)
}