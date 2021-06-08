package com.github.c5fr7q.playground.di.module

import android.content.Context
import androidx.room.Room
import com.github.c5fr7q.playground.data.source.local.Storage
import com.github.c5fr7q.playground.data.source.local.database.AppDatabase
import com.github.c5fr7q.playground.data.source.local.database.dao.PlaceDao
import com.github.c5fr7q.playground.data.source.local.database.migrations
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class LocalSourceModule {
	companion object {
		@Singleton
		@Provides
		fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase {
			return Room.databaseBuilder(context, AppDatabase::class.java, "app_db")
				.addMigrations(*migrations)
				.build()
		}

		@Singleton
		@Provides
		fun provideUserDao(appDatabase: AppDatabase): PlaceDao {
			return appDatabase.userDao()
		}
	}
}