package com.github.c5fr7q.playground.data.source.remote.sygic.entity

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass


@JsonClass(generateAdapter = true)
data class SygicPlacesResponse(
	@Json(name = "data") val data: Data
) {

	@JsonClass(generateAdapter = true)
	data class Data(
		@Json(name = "places") val places: List<Place>
	) {

		@JsonClass(generateAdapter = true)
		data class Place(
			@Json(name = "id") val id: String,
			@Json(name = "name") val name: String,
			@Json(name = "thumbnail_url") val thumbnailUrl: String?,
			@Json(name = "rating") val rating: Float,
			@Json(name = "rating_local") val ratingLocal: Float,
			@Json(name = "categories") val categories: List<String>,
			@Json(name = "tag_keys") val tags: List<String>,
		)
	}
}