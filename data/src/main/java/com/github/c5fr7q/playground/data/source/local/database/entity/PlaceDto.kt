package com.github.c5fr7q.playground.data.source.local.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "place")
data class PlaceDto(
	@PrimaryKey
	val id: String,
	val name: String,
	val rating: Float,
	val categories: String,
	val tags: String,
	val imageUrl: String,
	val lat: Float,
	val lon: Float,
	val createdDate: Long,
	@ColumnInfo(defaultValue = "0")
	val isFavorite: Boolean,
	@ColumnInfo(defaultValue = "0")
	val isBlocked: Boolean
)