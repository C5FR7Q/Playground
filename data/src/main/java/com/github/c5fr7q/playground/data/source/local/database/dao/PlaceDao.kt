package com.github.c5fr7q.playground.data.source.local.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.github.c5fr7q.playground.data.source.local.database.entity.PlaceDto
import kotlinx.coroutines.flow.Flow

@Dao
interface PlaceDao {
	@Query("SELECT * FROM place")
	suspend fun getAllPlacesOnce(): List<PlaceDto>

	@Query("SELECT * FROM place")
	fun getAllPlaces(): Flow<List<PlaceDto>>

	@Insert(onConflict = OnConflictStrategy.IGNORE)
	suspend fun addPlaces(places: List<PlaceDto>)

	@Query("DELETE FROM place WHERE createdDate < :minDate")
	suspend fun deleteOutdated(minDate: Long)
}