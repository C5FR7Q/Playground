package com.github.c5fr7q.playground.data.repository

import com.github.c5fr7q.playground.domain.repository.ProfileRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class ProfileRepositoryImpl @Inject constructor() : ProfileRepository {
	override fun getUserName(userId: String) = flow {
		delay(2000)

		emit("~~~${userId.reversed()}~~~$userId~~~")
	}
}