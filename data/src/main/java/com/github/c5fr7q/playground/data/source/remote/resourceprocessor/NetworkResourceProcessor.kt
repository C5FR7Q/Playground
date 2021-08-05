package com.github.c5fr7q.playground.data.source.remote.resourceprocessor

import com.github.c5fr7q.playground.domain.entity.base.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.onStart

abstract class NetworkResourceProcessor<Entity, Request> {

	companion object {
		suspend fun <Entity> NetworkResourceProcessor<Entity, Unit>.process() = process(Unit)
	}

	private val _resource = MutableStateFlow<Resource<Entity>>(Resource.success(null))
	private var useDbToPrefetch = true

	val resource: Flow<Resource<Entity>>
		get() {
			return _resource.onStart {
				if (useDbToPrefetch && _resource.value.data == null) {
					_resource.value = Resource.loading(_resource.value.data)
					_resource.value = loadFromDb()?.let { Resource.success(it) } ?: Resource.success(_resource.value.data)
					useDbToPrefetch = false
				}
			}
		}

	suspend fun process(request: Request) {
		if (shouldFetch(_resource.value.data, request)) {
			val fetching = _resource.value.loadState.isLoading()
			if (!fetching) {
				_resource.value = Resource.loading(_resource.value.data)
				try {
					val fetchedEntity = fetch(request)
					_resource.value = Resource.success(fetchedEntity)
					if (fetchedEntity != null) {
						saveEntityToDb(fetchedEntity)
					}
				} catch (e: Exception) {
					processException(e)
					if (_resource.value.loadState.isLoading()) {
						_resource.value = Resource.error()
					}
				}
			}
		}
	}

	protected open suspend fun loadFromDb(): Entity? = null

	protected open fun shouldFetch(entity: Entity?, request: Request): Boolean = entity == null

	@Throws(Exception::class)
	protected open suspend fun fetch(request: Request): Entity? = null

	protected open suspend fun saveEntityToDb(entity: Entity) {}

	protected open suspend fun processException(e: Exception) = produceError()

	protected fun produceError(message: String? = null, entity: Entity? = null) {
		_resource.value = Resource.error(entity, message)
	}
}