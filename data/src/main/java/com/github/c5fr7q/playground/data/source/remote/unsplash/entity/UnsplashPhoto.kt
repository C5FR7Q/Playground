package com.github.c5fr7q.playground.data.source.remote.unsplash.entity

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class UnsplashPhoto(
	@Json(name = "urls") val urls: Urls
) {
	@JsonClass(generateAdapter = true)
	data class Urls(
		@Json(name = "regular") val regular: String
	)
}