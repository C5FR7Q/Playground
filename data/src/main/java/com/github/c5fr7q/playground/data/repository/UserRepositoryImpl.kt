package com.github.c5fr7q.playground.data.repository

import com.github.c5fr7q.playground.data.database.dao.UserDao
import com.github.c5fr7q.playground.data.database.entity.UserDto
import com.github.c5fr7q.playground.domain.entity.User
import com.github.c5fr7q.playground.domain.repository.UserRepository
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.random.Random

class UserRepositoryImpl @Inject constructor(
	private val userDao: UserDao
) : UserRepository {
	override fun getAllUsers() = userDao.getAllUsers().map { list ->
		list.map { userDto -> User(userDto.name, userDto.age) }
	}

	override fun addUser(userName: String) {
		GlobalScope.launch {
			userDao.addUser(
				UserDto(
					userName, Random.nextInt(1000)
				)
			)
		}
	}
}