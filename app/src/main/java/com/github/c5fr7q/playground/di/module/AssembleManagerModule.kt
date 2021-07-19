package com.github.c5fr7q.playground.di.module

import com.github.c5fr7q.playground.presentation.manager.*
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dagger.multibindings.IntoSet
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
interface AssembleManagerModule {
	@IntoSet
	@Singleton
	@Binds
	fun bindPermissionManager(manager: PermissionManager): BaseManager

	@IntoSet
	@Singleton
	@Binds
	fun bindNetworkStateManager(manager: NetworkStateManager): BaseManager

	@IntoSet
	@Singleton
	@Binds
	fun bindNavigationManager(manager: NavigationManager): BaseManager

	@IntoSet
	@Singleton
	@Binds
	fun bindLocationManager(manager: LocationManager): BaseManager

}