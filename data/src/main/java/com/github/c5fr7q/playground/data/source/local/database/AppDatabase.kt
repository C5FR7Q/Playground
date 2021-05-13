package com.github.c5fr7q.playground.data.source.local.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.github.c5fr7q.playground.data.source.local.database.dao.UserDao
import com.github.c5fr7q.playground.data.source.local.database.entity.UserDto

@Database(entities = [UserDto::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
	abstract fun userDao(): UserDao
}