package com.github.c5fr7q.playground.domain.repository

import com.github.c5fr7q.playground.domain.entity.User
import kotlinx.coroutines.flow.Flow

interface UserRepository {
	fun getAllUsers(): Flow<List<User>>
	fun addUser(userName: String)
}