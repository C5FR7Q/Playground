package com.github.c5fr7q.playground.domain.entity

data class Place(
	val id: String,
	val name: String,
	val rating: Rating,
	val categories: List<Category>,
	val tags: List<String>,
	val imageUrl: String
) {
	enum class Category {
		DISCOVERING,
		EATING,
		GOING_OUT,
		HIKING,
		PLAYING,
		RELAXING,
		SHOPPING,
		SIGHTSEEING,
		SLEEPING,
		DOING_SPORTS,
		TRAVELING
	}

	data class Rating(
		val globalRating: Float,
		val localRating: Float
	)
}