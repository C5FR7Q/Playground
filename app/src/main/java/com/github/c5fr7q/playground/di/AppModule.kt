package com.github.c5fr7q.playground.di

import android.content.Context
import com.github.c5fr7q.playground.data.Storage
import com.github.c5fr7q.playground.data.repository.FancyRepositoryImpl
import com.github.c5fr7q.playground.domain.repository.FancyRepository
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
	}

	@Singleton
	@Binds
	abstract fun bindFancyRepository(fancyRepositoryImpl: FancyRepositoryImpl): FancyRepository

}