package com.github.c5fr7q.playground.data.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user")
data class UserDto(
	@PrimaryKey
	val name: String,
	val age: Int
)