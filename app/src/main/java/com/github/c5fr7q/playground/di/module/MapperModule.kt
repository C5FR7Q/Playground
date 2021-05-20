package com.github.c5fr7q.playground.di.module

import com.github.c5fr7q.playground.data.repository.mapper.PlaceDtoMapper
import com.github.c5fr7q.playground.data.repository.mapper.SygicPlaceMapper
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class MapperModule {
	companion object {
		@Singleton
		@Provides
		fun provideSygicPlaceMapper() = SygicPlaceMapper()

		@Singleton
		@Provides
		fun providePlaceDtoMapper() = PlaceDtoMapper()
	}
}