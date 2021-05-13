package com.github.c5fr7q.playground.di.module

import com.github.c5fr7q.playground.data.repository.FancyRepositoryImpl
import com.github.c5fr7q.playground.data.repository.PlaceRepositoryImpl
import com.github.c5fr7q.playground.data.repository.ProfileRepositoryImpl
import com.github.c5fr7q.playground.data.repository.UserRepositoryImpl
import com.github.c5fr7q.playground.domain.repository.FancyRepository
import com.github.c5fr7q.playground.domain.repository.PlaceRepository
import com.github.c5fr7q.playground.domain.repository.ProfileRepository
import com.github.c5fr7q.playground.domain.repository.UserRepository
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
	fun bindFancyRepository(fancyRepositoryImpl: FancyRepositoryImpl): FancyRepository

	@Singleton
	@Binds
	fun bindProfileRepository(profileRepositoryImpl: ProfileRepositoryImpl): ProfileRepository

	@Singleton
	@Binds
	fun bindUserRepository(userRepositoryImpl: UserRepositoryImpl): UserRepository

	@Singleton
	@Binds
	fun bindMapRepository(mapRepositoryImpl: PlaceRepositoryImpl): PlaceRepository
}