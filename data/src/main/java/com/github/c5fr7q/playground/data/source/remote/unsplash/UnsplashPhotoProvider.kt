package com.github.c5fr7q.playground.data.source.remote.unsplash

import com.github.c5fr7q.playground.data.source.remote.resourceprocessor.PhotoProcessor
import kotlinx.coroutines.flow.first
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UnsplashPhotoProvider @Inject constructor(
	private val photoProcessor: PhotoProcessor
) {
	companion object {
		const val MAX_COUNT = 30
	}

	suspend fun getPhotos(count: Int): List<String> {
		return photoProcessor.run {
			process(PhotoProcessor.Request(count))
			resource.first { it.loadState.isSucceed() }.data ?: emptyList()
		}
	}

}