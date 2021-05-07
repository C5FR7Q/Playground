package com.github.c5fr7q.playground.di

import android.content.Context
import androidx.room.Room
import com.github.c5fr7q.playground.data.Storage
import com.github.c5fr7q.playground.data.database.AppDatabase
import com.github.c5fr7q.playground.data.repository.FancyRepositoryImpl
import com.github.c5fr7q.playground.data.repository.ProfileRepositoryImpl
import com.github.c5fr7q.playground.data.repository.UserRepositoryImpl
import com.github.c5fr7q.playground.domain.repository.FancyRepository
import com.github.c5fr7q.playground.domain.repository.ProfileRepository
import com.github.c5fr7q.playground.domain.repository.UserRepository
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class AppModule {

	companion object {
		@Singleton
		@Provides
		fun providesStorage(@ApplicationContext context: Context) = Storage(context)

		@Singleton
		@Provides
		fun providesAppDatabase(@ApplicationContext context: Context) =
			Room.databaseBuilder(context, AppDatabase::class.java, "app_db").build()

		@Singleton
		@Provides
		fun provideUserDao(appDatabase: AppDatabase) = appDatabase.userDao()
	}

	@Singleton
	@Binds
	abstract fun bindFancyRepository(fancyRepositoryImpl: FancyRepositoryImpl): FancyRepository

	@Singleton
	@Binds
	abstract fun bindProfileRepository(profileRepositoryImpl: ProfileRepositoryImpl): ProfileRepository

	@Singleton
	@Binds
	abstract fun bindUserRepository(userRepositoryImpl: UserRepositoryImpl): UserRepository

}