package com.github.c5fr7q.playground.data.source.remote.unsplash

import com.github.c5fr7q.playground.data.source.remote.unsplash.entity.UnsplashPhoto
import javax.inject.Inject
import kotlin.math.ceil

class UnsplashPhotoProvider @Inject constructor(
	private val unsplashService: UnsplashService,
	private val unsplashPhotoMapper: UnsplashPhotoMapper
) {
	companion object {
		const val MAX_COUNT = 30
	}

	suspend fun getPhotos(count: Int): List<String> {
		val requestsCount = when {
			count > MAX_COUNT -> ceil(count / MAX_COUNT.toFloat()).toInt()
			else -> 1
		}
		val lastRequestCount = requestsCount * MAX_COUNT - count

		val photos = mutableListOf<UnsplashPhoto>()

		for (i in 0 until requestsCount - 1) {
			photos.addAll(unsplashService.getPlaces(MAX_COUNT))
		}
		photos.addAll(unsplashService.getPlaces(lastRequestCount))

		return unsplashPhotoMapper.mapPhotos(photos)
	}
}