package com.github.c5fr7q.playground.domain.repository

import kotlinx.coroutines.flow.Flow

interface ProfileRepository {
	fun getUserName(userId: String): Flow<String>
}