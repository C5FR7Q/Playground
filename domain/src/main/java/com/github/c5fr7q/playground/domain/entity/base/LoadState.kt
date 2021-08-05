package com.github.c5fr7q.playground.domain.entity.base

enum class LoadState {
	LOADING,
	SUCCESS,
	ERROR;

	fun isLoading() = this == LOADING
	fun isSucceed() = this == SUCCESS
	fun isFailed() = this == ERROR
}