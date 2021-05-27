package com.github.c5fr7q.playground.data.source.local

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.time.Duration
import javax.inject.Inject

class Storage @Inject constructor(
	context: Context
) {
	private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "storage")
	private val dataStore = context.dataStore

	private companion object {
		val PLACES_PACK_COUNT = intPreferencesKey("places_pack_count")
		val DATA_CACHING_TIME = longPreferencesKey("data_caching_time")
		val PLACES_RADIUS = intPreferencesKey("places_radius")
	}

	suspend fun setPlacesPackCount(value: Int) = set(PLACES_PACK_COUNT, value)
	suspend fun setDataCachingTime(duration: Duration) = set(DATA_CACHING_TIME, duration.toDays())
	suspend fun setPlacesRadius(value: Int) = set(PLACES_RADIUS, value)

	fun getPlacesPackCount() = get(PLACES_PACK_COUNT).map { it ?: 10 }
	fun getDataCachingTime() = get(DATA_CACHING_TIME).map { Duration.ofDays(it ?: 30L) }
	fun getPlacesRadius() = get(PLACES_RADIUS).map { it ?: 5000 }

	private suspend fun <T> set(key: Preferences.Key<T>, value: T) {
		dataStore.edit { it[key] = value }
	}

	private fun <T> get(key: Preferences.Key<T>): Flow<T?> = dataStore.data.map { it[key] }

}