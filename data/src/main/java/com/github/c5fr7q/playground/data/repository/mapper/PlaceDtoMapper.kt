package com.github.c5fr7q.playground.data.repository.mapper

import com.github.c5fr7q.playground.data.source.local.database.entity.PlaceDto
import com.github.c5fr7q.playground.domain.entity.Place
import com.github.c5fr7q.playground.domain.entity.Position
import java.time.Instant
import java.util.*
import javax.inject.Inject

class PlaceDtoMapper @Inject constructor() {
	fun mapPlaceToDto(place: Place): PlaceDto {
		return place.run {
			PlaceDto(
				id = id,
				name = name,
				rating = rating,
				categories = mapCategoriesToString(categories),
				tags = mapTagsToString(tags),
				imageUrl = imageUrl,
				lat = position.lat,
				lon = position.lon,
				createdDate = Date.from(Instant.now()).time
			)
		}
	}

	fun mapDtoToPlace(dto: PlaceDto): Place {
		return dto.run {
			Place(
				id = id,
				name = name,
				rating = rating,
				categories = mapStringToCategories(categories),
				tags = mapStringToTags(tags),
				imageUrl = imageUrl,
				position = Position(lon, lat)
			)
		}
	}

	private fun mapCategoriesToString(categories: List<Place.Category>): String {
		return categories.joinToString(",") { it.name }
	}

	private fun mapStringToCategories(string: String): List<Place.Category> {
		return string.split(",").mapNotNull { str ->
			Place.Category.values().firstOrNull { it.name == str }
		}
	}

	private fun mapTagsToString(tags: List<String>) = tags.joinToString(",")

	private fun mapStringToTags(string: String) = string.split(",")
}