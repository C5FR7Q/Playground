package com.github.c5fr7q.playground.data.source.local.database.dao

import androidx.room.*
import com.github.c5fr7q.playground.data.source.local.database.entity.PlaceDto
import kotlinx.coroutines.flow.Flow

@Dao
interface PlaceDao {
	@Query("SELECT * FROM place")
	fun getAllPlaces(): Flow<List<PlaceDto>>

	@Insert(onConflict = OnConflictStrategy.REPLACE)
	suspend fun addPlaces(places: List<PlaceDto>)

	@Query("DELETE FROM place WHERE createdDate < :minDate AND isFavorite = 0")
	suspend fun deleteOutdatedPlaces(minDate: Long)

	@Query("UPDATE place SET isFavorite = 1 WHERE id == :placeId")
	suspend fun likePlace(placeId: String)

	@Query("UPDATE place SET isFavorite = 0 WHERE id == :placeId")
	suspend fun dislikePlace(placeId: String)

	@Query("UPDATE place SET isBlocked = 1 WHERE id == :placeId")
	suspend fun blockPlace(placeId: String)

	@Query("UPDATE place SET isBlocked = 0 WHERE id IN (:placeIds)")
	suspend fun unblockPlaces(placeIds: List<String>)

	@Update
	suspend fun updatePlaces(places: List<PlaceDto>)
}