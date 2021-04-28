package com.github.c5fr7q.playground.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class Storage @Inject constructor(
	context: Context
) {
	private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "storage")
	private val dataStore = context.dataStore

	private companion object {
		val COUNT = intPreferencesKey("count")
	}

	suspend fun setCount(value: Int) = set(COUNT, value)

	fun getCount() = get(COUNT)

	private suspend fun <T> set(key: Preferences.Key<T>, value: T) {
		dataStore.edit { it[key] = value }
	}

	private fun <T> get(key: Preferences.Key<T>): Flow<T?> = dataStore.data.map { it[key] }

}