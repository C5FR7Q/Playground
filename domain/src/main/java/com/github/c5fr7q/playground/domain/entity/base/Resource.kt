package com.github.c5fr7q.playground.domain.entity.base

/* Sealed class cannot be used due to lack of copy method (sealed class cannot be a data one) */
data class Resource<T> constructor(
	val loadState: LoadState,
	val data: T? = null,
	val message: String? = null
) {
	companion object {
		fun <T> loading(data: T? = null) = Resource(LoadState.LOADING, data)
		fun <T> success(data: T) = Resource(LoadState.SUCCESS, data)
		fun <T> error(data: T? = null, message: String? = null) = Resource(LoadState.ERROR, data, message)
	}
}