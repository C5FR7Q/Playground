package com.github.c5fr7q.playground.data.source.remote.resourceprocessor

import com.github.c5fr7q.playground.data.source.remote.unsplash.UnsplashPhotoMapper
import com.github.c5fr7q.playground.data.source.remote.unsplash.UnsplashPhotoProvider
import com.github.c5fr7q.playground.data.source.remote.unsplash.UnsplashService
import com.github.c5fr7q.playground.data.source.remote.unsplash.entity.UnsplashPhoto
import javax.inject.Inject
import kotlin.math.ceil

class PhotoProcessor @Inject constructor(
	private val unsplashService: UnsplashService,
	private val unsplashPhotoMapper: UnsplashPhotoMapper
) : NetworkResourceProcessor<List<String>, PhotoProcessor.Request>() {

	data class Request(val count: Int)

	override fun shouldFetch(entity: List<String>?, request: Request) = true

	override suspend fun fetch(request: Request): List<String> {
		val (count) = request
		val requestsCount = when {
			count > UnsplashPhotoProvider.MAX_COUNT -> ceil(count / UnsplashPhotoProvider.MAX_COUNT.toFloat()).toInt()
			else -> 1
		}
		val lastRequestCount = requestsCount * UnsplashPhotoProvider.MAX_COUNT - count

		val photos = mutableListOf<UnsplashPhoto>()

		for (i in 0 until requestsCount - 1) {
			photos.addAll(unsplashService.getPlaces(UnsplashPhotoProvider.MAX_COUNT))
		}
		photos.addAll(unsplashService.getPlaces(lastRequestCount))

		return unsplashPhotoMapper.mapPhotos(photos)
	}
}