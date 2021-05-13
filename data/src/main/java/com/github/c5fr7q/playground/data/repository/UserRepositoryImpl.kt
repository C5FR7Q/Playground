package com.github.c5fr7q.playground.data.repository

import com.github.c5fr7q.playground.data.GeneralCoroutineScope
import com.github.c5fr7q.playground.data.source.local.database.dao.UserDao
import com.github.c5fr7q.playground.data.source.local.database.entity.UserDto
import com.github.c5fr7q.playground.domain.entity.User
import com.github.c5fr7q.playground.domain.repository.UserRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.random.Random

class UserRepositoryImpl @Inject constructor(
	private val userDao: UserDao,
	@GeneralCoroutineScope private val generalScope: CoroutineScope
) : UserRepository {
	override fun getAllUsers() = userDao.getAllUsers().map { list ->
		list.map { userDto -> User(userDto.name, userDto.age) }
	}

	override fun addUser(userName: String) {
		generalScope.launch {
			userDao.addUser(
				UserDto(
					userName, Random.nextInt(1000)
				)
			)
		}
	}
}