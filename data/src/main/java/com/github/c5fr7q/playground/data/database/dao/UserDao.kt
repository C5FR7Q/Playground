package com.github.c5fr7q.playground.data.database.dao

import androidx.room.*
import com.github.c5fr7q.playground.data.database.entity.UserDto
import kotlinx.coroutines.flow.Flow

@Dao
interface UserDao {
	@Query("SELECT * FROM user WHERE age > 18")
	suspend fun getAllAdultsOnce(): List<UserDto>

	@Query("SELECT * FROM user WHERE age > :requiredAge")
	fun getAllUsersByAge(requiredAge: Int): Flow<List<UserDto>>

	@Query("SELECT * FROM user")
	fun getAllUsers(): Flow<List<UserDto>>

	@Insert(onConflict = OnConflictStrategy.REPLACE)
	fun addUsers(users: List<UserDto>)

	@Insert(onConflict = OnConflictStrategy.REPLACE)
	fun addUser(user: UserDto)

	@Delete
	fun deleteUser(user: UserDto)
}