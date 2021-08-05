package com.github.c5fr7q.playground.di.module

import android.content.Context
import androidx.room.Room
import com.github.c5fr7q.playground.data.GeneralCoroutineScope
import com.github.c5fr7q.playground.data.source.local.database.AppDatabase
import com.github.c5fr7q.playground.data.source.local.database.dao.PlaceDao
import com.github.c5fr7q.playground.data.source.local.database.migrations
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.*
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class LocalSourceModule {
	companion object {

		/* Let's imagine AppDatabase is a heavy dependency. */
		@Singleton
		@Provides
		fun provideAppDatabaseFlow(
			@ApplicationContext context: Context,
			@GeneralCoroutineScope coroutineScope: CoroutineScope
		): Flow<@JvmSuppressWildcards AppDatabase> {
			return flow {
				emit(
					Room.databaseBuilder(context, AppDatabase::class.java, "app_db")
						.addMigrations(*migrations)
						.build()
				)
			}.flowOn(coroutineScope.coroutineContext).shareIn(coroutineScope, SharingStarted.WhileSubscribed(), 1)
		}

		@Singleton
		@Provides
		fun provideUserDao(appDatabaseFlow: Flow<@JvmSuppressWildcards AppDatabase>): Flow<@JvmSuppressWildcards PlaceDao> {
			return appDatabaseFlow.map { it.userDao() }
		}
	}
}