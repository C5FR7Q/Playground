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
	fun getAllPlaces(): Flow<List<PlaceDto>>

	@Insert(onConflict = OnConflictStrategy.IGNORE)
	suspend fun addPlaces(places: List<PlaceDto>)

	@Query("DELETE FROM place WHERE createdDate < :minDate AND isFavorite = 0")
	suspend fun deleteOutdated(minDate: Long)

	@Query("UPDATE place SET isFavorite = 1 WHERE id == :placeId")
	suspend fun addPlaceToFavorite(placeId: String)

	@Query("UPDATE place SET isFavorite = 0 WHERE id == :placeId")
	suspend fun removePlaceFromFavorite(placeId: String)

	@Query("UPDATE place SET isBlocked = 1 WHERE id == :placeId")
	suspend fun addPlaceToBlocked(placeId: String)

	@Query("UPDATE place SET isBlocked = 0 WHERE id IN (:placeIds)")
	suspend fun removePlacesFromBlocked(placeIds: List<String>)
}