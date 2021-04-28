package com.github.c5fr7q.playground.data.repository

import com.github.c5fr7q.playground.data.Storage
import com.github.c5fr7q.playground.domain.entity.FancyData
import com.github.c5fr7q.playground.domain.repository.FancyRepository
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

class FancyRepositoryImpl @Inject constructor(
	private val storage: Storage
) : FancyRepository {
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

	override fun getCount() = storage.getCount().map { it ?: 0 }

	override fun requestMoreNumbers() {
		GlobalScope.launch {
			requestMoreNumbers(numbers.value.size, 100)
			storage.setCount(numbers.value.size)
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