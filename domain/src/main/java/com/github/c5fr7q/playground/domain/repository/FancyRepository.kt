package com.github.c5fr7q.playground.domain.repository

import com.github.c5fr7q.playground.domain.entity.FancyData
import kotlinx.coroutines.flow.Flow

interface FancyRepository {
	fun getFancyData(): Flow<FancyData>
	fun getNumbersList(): Flow<List<String>>
	fun getCount(): Flow<Int>

	fun requestMoreNumbers()
}