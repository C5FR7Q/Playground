package com.github.c5fr7q.playground.data.source.remote.unsplash

import com.github.c5fr7q.playground.data.source.remote.unsplash.entity.UnsplashPhoto
import retrofit2.http.GET
import retrofit2.http.Query

interface UnsplashService {
	@GET("photos/random/")
	suspend fun getPlaces(
		@Query("count") count: Int
	): List<UnsplashPhoto>
}