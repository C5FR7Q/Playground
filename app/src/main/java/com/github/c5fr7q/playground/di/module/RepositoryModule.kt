package com.github.c5fr7q.playground.di.module

import com.github.c5fr7q.playground.data.repository.PlaceRepositoryImpl
import com.github.c5fr7q.playground.data.repository.SettingsRepositoryImpl
import com.github.c5fr7q.playground.domain.repository.PlaceRepository
import com.github.c5fr7q.playground.domain.repository.SettingsRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
interface RepositoryModule {
	@Singleton
	@Binds
	fun bindSettingsRepository(settingsRepositoryImpl: SettingsRepositoryImpl): SettingsRepository

	@Singleton
	@Binds
	fun bindMapRepository(mapRepositoryImpl: PlaceRepositoryImpl): PlaceRepository
}