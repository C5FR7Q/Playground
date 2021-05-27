package com.github.c5fr7q.playground.domain.repository

import kotlinx.coroutines.flow.Flow
import java.time.Duration

interface SettingsRepository {
	fun setPlacesPackCount(value: Int)
	fun setDataCachingTime(duration: Duration)
	fun setPlacesRadius(value: Int)

	fun getPlacesPackCount(): Flow<Int>
	fun getDataCachingTime(): Flow<Duration>
	fun getPlacesRadius(): Flow<Int>
}