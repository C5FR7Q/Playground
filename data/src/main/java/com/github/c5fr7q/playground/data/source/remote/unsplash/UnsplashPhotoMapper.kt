package com.github.c5fr7q.playground.data.source.remote.unsplash

import com.github.c5fr7q.playground.data.source.remote.unsplash.entity.UnsplashPhoto
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UnsplashPhotoMapper @Inject constructor() {
	fun mapPhotos(photos: List<UnsplashPhoto>): List<String> {
		return photos.map { it.urls.regular }
	}
}