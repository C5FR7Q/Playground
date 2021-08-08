package com.github.c5fr7q.playground.data.repository

import com.github.c5fr7q.playground.data.source.local.Storage
import com.github.c5fr7q.playground.domain.repository.SettingsRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import java.time.Duration
import javax.inject.Inject

class SettingsRepositoryImpl @Inject constructor(
	private val storage: Storage,
	private val generalScope: CoroutineScope
) : SettingsRepository {
	override fun setPlacesPackCount(value: Int) {
		generalScope.launch {
			storage.setPlacesPackCount(value)
		}
	}

	override fun setDataCachingTime(duration: Duration) {
		generalScope.launch {
			storage.setDataCachingTime(duration)
		}
	}

	override fun setPlacesRadius(value: Int) {
		generalScope.launch {
			storage.setPlacesRadius(value)
		}
	}

	override fun getPlacesPackCount() = storage.getPlacesPackCount()

	override fun getDataCachingTime() = storage.getDataCachingTime()

	override fun getPlacesRadius() = storage.getPlacesRadius()
}