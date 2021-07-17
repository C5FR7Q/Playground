package com.github.c5fr7q.util

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*

fun <T> Flow<Iterable<T>>.filterIterable(filter: (T) -> Boolean): Flow<List<T>> {
	return map { iterable -> iterable.filter { filter(it) } }
}

fun <T, R> Flow<Iterable<T>>.mapIterable(mapper: (T) -> R): Flow<List<R>> {
	return map { iterable -> iterable.map { mapper(it) } }
}

@ExperimentalCoroutinesApi
fun <R> Flow<Boolean>.flatMapLatestOnTrue(flow: Flow<R>): Flow<R> {
	return transformLatest { if (it) emitAll(flow) else emptyFlow<R>() }
}

@ExperimentalCoroutinesApi
fun <R> Flow<Boolean>.flatMapLatestOnFalse(flow: Flow<R>): Flow<R> {
	return transformLatest { if (!it) emitAll(flow) else emptyFlow<R>() }
}

@ExperimentalCoroutinesApi
fun <T, R> Flow<T>.flatMapLatestWith(flow: Flow<R>): Flow<NTuple2<T, R>> {
	return flatMapLatest { t -> flow.map { t then it } }
}

@JvmName("flowCombine")
fun <T1, T2> Flow<T1>.combine(flow: Flow<T2>): Flow<NTuple2<T1, T2>> {
	return combine(flow) { t1, t2 -> t1 then t2 }
}

fun <T1, T2> combine(flow1: Flow<T1>, flow2: Flow<T2>): Flow<NTuple2<T1, T2>> {
	return combine(flow1, flow2) { t1, t2 -> t1 then t2 }
}

fun <T1, T2, T3> combine(flow1: Flow<T1>, flow2: Flow<T2>, flow3: Flow<T3>): Flow<NTuple3<T1, T2, T3>> {
	return combine(flow1, flow2, flow3) { t1, t2, t3 -> t1 then t2 then t3 }
}

fun <T1, T2, T3, T4> combine(flow1: Flow<T1>, flow2: Flow<T2>, flow3: Flow<T3>, flow4: Flow<T4>): Flow<NTuple4<T1, T2, T3, T4>> {
	return combine(flow1, flow2, flow3, flow4) { t1, t2, t3, t4 -> t1 then t2 then t3 then t4 }
}

fun <T1, T2, T3, T4, T5> combine(
	flow1: Flow<T1>,
	flow2: Flow<T2>,
	flow3: Flow<T3>,
	flow4: Flow<T4>,
	flow5: Flow<T5>
): Flow<NTuple5<T1, T2, T3, T4, T5>> {
	return combine(flow1, flow2, flow3, flow4, flow5) { t1, t2, t3, t4, t5 -> t1 then t2 then t3 then t4 then t5 }
}

data class NTuple2<T1, T2>(val t1: T1, val t2: T2)

data class NTuple3<T1, T2, T3>(val t1: T1, val t2: T2, val t3: T3)

data class NTuple4<T1, T2, T3, T4>(val t1: T1, val t2: T2, val t3: T3, val t4: T4)

data class NTuple5<T1, T2, T3, T4, T5>(val t1: T1, val t2: T2, val t3: T3, val t4: T4, val t5: T5)

infix fun <T1, T2> T1.then(t2: T2): NTuple2<T1, T2> {
	return NTuple2(this, t2)
}

infix fun <T1, T2, T3> NTuple2<T1, T2>.then(t3: T3): NTuple3<T1, T2, T3> {
	return NTuple3(this.t1, this.t2, t3)
}

infix fun <T1, T2, T3, T4> NTuple3<T1, T2, T3>.then(t4: T4): NTuple4<T1, T2, T3, T4> {
	return NTuple4(this.t1, this.t2, this.t3, t4)
}

infix fun <T1, T2, T3, T4, T5> NTuple4<T1, T2, T3, T4>.then(t5: T5): NTuple5<T1, T2, T3, T4, T5> {
	return NTuple5(this.t1, this.t2, this.t3, this.t4, t5)
}