package com.github.c5fr7q.playground.di.module

import com.github.c5fr7q.playground.data.source.remote.sygic.SygicService
import com.github.c5fr7q.playground.data.source.remote.unsplash.UnsplashPhotoMapper
import com.github.c5fr7q.playground.data.source.remote.unsplash.UnsplashPhotoProvider
import com.github.c5fr7q.playground.data.source.remote.unsplash.UnsplashService
import com.github.c5fr7q.playground.di.SygicClient
import com.github.c5fr7q.playground.di.UnsplashClient
import com.squareup.moshi.Moshi
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RemoteSourceModule {
	companion object {
		@Singleton
		@Provides
		fun provideGeoapifyService(@SygicClient client: OkHttpClient, moshi: Moshi): SygicService {
			return Retrofit.Builder()
				.client(client)
				.baseUrl("https://api.sygictravelapi.com/1.2/en/")
				.addConverterFactory(ScalarsConverterFactory.create())
				.addConverterFactory(MoshiConverterFactory.create(moshi))
				.build()
				.create(SygicService::class.java)
		}

		@SygicClient
		@Singleton
		@Provides
		fun provideSygicClient(): OkHttpClient {
			return OkHttpClient.Builder()
				.addInterceptor(HttpLoggingInterceptor())
				.addInterceptor { chain ->
					val original = chain.request()

					val newRequest = original.newBuilder()
						.header("x-api-key", Keys.SYGIC)
						.build()
					chain.proceed(newRequest)
				}
				.build()
		}

		@Singleton
		@Provides
		fun provideUnsplashService(@UnsplashClient client: OkHttpClient, moshi: Moshi): UnsplashService {
			return Retrofit.Builder()
				.client(client)
				.baseUrl("https://api.unsplash.com/")
				.addConverterFactory(ScalarsConverterFactory.create())
				.addConverterFactory(MoshiConverterFactory.create(moshi))
				.build()
				.create(UnsplashService::class.java)
		}

		@UnsplashClient
		@Singleton
		@Provides
		fun provideUnsplashClient(): OkHttpClient {
			return OkHttpClient.Builder()
				.addInterceptor(HttpLoggingInterceptor())
				.addInterceptor { chain ->
					val original = chain.request()
					val originalUrl = original.url

					val newUrl = originalUrl.newBuilder()
						.addQueryParameter("client_id", Keys.UNSPLASH)
						.build()

					val newRequest = original.newBuilder().url(newUrl).build()
					chain.proceed(newRequest)
				}
				.build()
		}

		@Singleton
		@Provides
		fun provideUnsplashPhotoProvider(
			unsplashService: UnsplashService,
			unsplashPhotoMapper: UnsplashPhotoMapper
		): UnsplashPhotoProvider {
			return UnsplashPhotoProvider(unsplashService, unsplashPhotoMapper)
		}
	}
}