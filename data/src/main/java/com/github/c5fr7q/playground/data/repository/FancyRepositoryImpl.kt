package com.github.c5fr7q.playground.data.repository

import com.github.c5fr7q.playground.domain.entity.FancyData
import com.github.c5fr7q.playground.domain.repository.FancyRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import javax.inject.Inject

class FancyRepositoryImpl @Inject constructor() : FancyRepository {
	private val numbers = MutableStateFlow(emptyList<Int>())

	override fun getFancyData() = flow {
		emit(FancyData("foo"))

		delay(2500)
		emit(FancyData("bar"))
	}

	override fun getNumbersList(): Flow<List<Int>> {
		if (numbers.value.isEmpty()) {
			requestMoreNumbers()
		}
		return numbers.asStateFlow()
	}

	override fun requestMoreNumbers() {
		CoroutineScope(Dispatchers.Default).launch {
			requestMoreNumbers(numbers.value.size, 100)
		}
	}

	private suspend fun requestMoreNumbers(offset: Int, count: Int) {
		delay(1500)
		numbers.value = numbers.value + mutableListOf<Int>().apply {
			for (i in offset until offset + count) {
				add(i)
			}
		}
	}
}