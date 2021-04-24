package com.github.c5fr7q.playground.di

import com.github.c5fr7q.playground.data.repository.FancyRepositoryImpl
import com.github.c5fr7q.playground.domain.repository.FancyRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class AppModule {

	@Singleton
	@Binds
	abstract fun bindFancyRepository(fancyRepositoryImpl: FancyRepositoryImpl): FancyRepository
}