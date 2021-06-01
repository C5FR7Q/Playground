package com.github.c5fr7q.playground.di.module

import android.content.Context
import com.github.c5fr7q.playground.data.GeneralCoroutineScope
import com.github.c5fr7q.util.ResourceHelper
import com.squareup.moshi.Moshi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.GlobalScope
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class UtilModule {
	companion object {
		@Singleton
		@Provides
		fun provideMoshi(): Moshi = Moshi.Builder().build()

		@GeneralCoroutineScope
		@Singleton
		@Provides
		fun provideGeneralCoroutineScope(): CoroutineScope = GlobalScope

		@Singleton
		@Provides
		fun provideResourceHelper(@ApplicationContext context: Context) = ResourceHelper(context)
	}
}