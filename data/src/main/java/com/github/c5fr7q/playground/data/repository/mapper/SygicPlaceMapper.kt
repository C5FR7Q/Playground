package com.github.c5fr7q.playground.data.repository.mapper

import com.github.c5fr7q.playground.data.source.remote.sygic.entity.SygicPlacesResponse
import com.github.c5fr7q.playground.domain.entity.Place
import com.github.c5fr7q.playground.domain.entity.Position
import javax.inject.Inject

class SygicPlaceMapper @Inject constructor() {
	fun mapArea(lat: Float, lon: Float, radius: Int) = "$lat,$lon,$radius"

	fun mapResponse(sygicPlacesResponse: SygicPlacesResponse): List<Place> {
		return sygicPlacesResponse.data.places.map { place ->
			place.run {
				Place(
					id = id,
					name = name,
					rating = rating,
					categories = mapStringsToCategories(categories),
					tags = tags,
					imageUrl = thumbnailUrl ?: "",
					position = place.location.run { Position(lon, lat) }
				)
			}
		}
	}

	fun mapCategoriesToString(categories: List<Place.Category>) = categories.joinToString { mapCategoryToString(it) }.replace(" ", "")

	fun mapStringsToCategories(strings: List<String>): List<Place.Category> {
		return strings.mapNotNull { string ->
			Place.Category.values().firstOrNull { mapCategoryToString(it) == string }
		}
	}

	private fun mapCategoryToString(category: Place.Category): String {
		return when (category) {
			Place.Category.DISCOVERING -> "discovering"
			Place.Category.EATING -> "eating"
			Place.Category.GOING_OUT -> "going_out"
			Place.Category.HIKING -> "hiking"
			Place.Category.PLAYING -> "playing"
			Place.Category.RELAXING -> "relaxing"
			Place.Category.SHOPPING -> "shopping"
			Place.Category.SIGHTSEEING -> "sightseeing"
			Place.Category.SLEEPING -> "sleeping"
			Place.Category.DOING_SPORTS -> "doing_sports"
			Place.Category.TRAVELING -> "traveling"
		}
	}
}