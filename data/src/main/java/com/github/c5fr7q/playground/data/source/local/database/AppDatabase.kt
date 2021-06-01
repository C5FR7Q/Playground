package com.github.c5fr7q.playground.data.source.local.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.github.c5fr7q.playground.data.source.local.database.dao.PlaceDao
import com.github.c5fr7q.playground.data.source.local.database.entity.PlaceDto


@Database(entities = [PlaceDto::class], version = 3, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
	abstract fun userDao(): PlaceDao
}

val migrations = arrayOf(
	object : Migration(1, 2) {
		override fun migrate(database: SupportSQLiteDatabase) {
			database.execSQL("ALTER TABLE place ADD COLUMN isFavorite INTEGER DEFAULT 0 NOT NULL")
		}
	},
	object : Migration(2, 3) {
		override fun migrate(database: SupportSQLiteDatabase) {
			database.execSQL("ALTER TABLE place ADD COLUMN isBlocked INTEGER DEFAULT 0 NOT NULL")
		}
	}
)
