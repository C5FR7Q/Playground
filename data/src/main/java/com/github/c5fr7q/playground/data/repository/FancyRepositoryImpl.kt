package com.github.c5fr7q.playground.data.repository

import com.github.c5fr7q.playground.domain.entity.FancyData
import com.github.c5fr7q.playground.domain.repository.FancyRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.flow

class FancyRepositoryImpl : FancyRepository {
	override fun getFancyData() = flow {
		emit(FancyData("foo"))

		delay(2500)
		emit(FancyData("bar"))
	}
}