package com.github.c5fr7q.playground.domain.repository

import kotlinx.coroutines.flow.Flow
import java.time.Duration

interface SettingsRepository {
	fun setPlacesPackCount(value: Int)
	fun setPlacesMetersCallThreshold(value: Int)
	fun setDataCachingTime(duration: Duration)

	fun getPlacesPackCount(): Flow<Int>
	fun getPlacesMetersCallThreshold(): Flow<Int>
	fun getDataCachingTime(): Flow<Duration>
}