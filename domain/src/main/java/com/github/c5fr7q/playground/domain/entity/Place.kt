package com.github.c5fr7q.playground.domain.entity

data class Place(
	val id: String,
	val name: String,
	val rating: Float,
	val categories: List<Category>,
	val tags: List<String>,
	val imageUrl: String,
	val position: Position,
	val isFavorite: Boolean,
	val isBlocked: Boolean,
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
}