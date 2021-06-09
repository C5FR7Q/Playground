package com.github.c5fr7q.playground.di.module

import com.github.c5fr7q.playground.data.manager.ILocationManager
import com.github.c5fr7q.playground.presentation.manager.LocationManager
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
interface ManagerModule {
	@Singleton
	@Binds
	fun bindLocationManager(locationManager: LocationManager): ILocationManager
}