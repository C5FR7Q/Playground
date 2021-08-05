package com.github.c5fr7q.playground.data.source.remote.resourceprocessor

import com.github.c5fr7q.playground.data.repository.mapper.PlaceDtoMapper
import com.github.c5fr7q.playground.data.repository.mapper.SygicPlaceMapper
import com.github.c5fr7q.playground.data.source.local.Storage
import com.github.c5fr7q.playground.data.source.local.database.dao.PlaceDao
import com.github.c5fr7q.playground.data.source.local.database.entity.PlaceDto
import com.github.c5fr7q.playground.data.source.remote.sygic.SygicService
import com.github.c5fr7q.playground.data.source.remote.unsplash.UnsplashPhotoProvider
import com.github.c5fr7q.playground.domain.entity.Place
import com.github.c5fr7q.playground.domain.entity.Position
import com.github.c5fr7q.util.mapIterable
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flatMapLatest
import java.time.Instant
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.math.round

@Singleton
class PlaceProcessor @Inject constructor(
	private val sygicService: SygicService,
	private val placeDaoFlow: Flow<@JvmSuppressWildcards PlaceDao>,
	private val unsplashPhotoProvider: UnsplashPhotoProvider,
	private val sygicPlaceMapper: SygicPlaceMapper,
	private val placeDtoMapper: PlaceDtoMapper,
	private val storage: Storage
) : NetworkResourceProcessor<List<Place>, PlaceProcessor.Request>() {
	sealed class Request {

		data class Reload(
			val position: Position,
			val categories: List<Place.Category>
		) : Request()

		object LoadMore : Request()
	}

	private var position: Position? = null
	private var categories: List<Place.Category>? = null
	private var radius: Int? = null
	private var packCount: Int? = null

	override fun shouldFetch(entity: List<Place>?, request: Request): Boolean {
		if (request is Request.Reload) {
			return request.categories != categories || !(request.position.almostTheSameAs(position))
		}

		return position != null && categories != null
	}

	override suspend fun fetch(request: Request): List<Place> {
		val currentPlaces: List<Place>

		when (request) {
			is Request.LoadMore -> {
				currentPlaces = resource.first().data ?: emptyList()
			}
			is Request.Reload -> {
				categories = request.categories
				position = request.position
				currentPlaces = emptyList()
			}
		}

		val offset = currentPlaces.size

		val newPlaces = sygicPlaceMapper.mapResponse(
			sygicService.getPlaces(
				sygicPlaceMapper.mapCategoriesToString(categories!!),
				sygicPlaceMapper.mapArea(position!!.lat, position!!.lon, getRadius()),
				getPackCount(),
				offset
			)
		)

		return when {
			offset != 0 -> currentPlaces + newPlaces
			else -> newPlaces
		}
	}

	override suspend fun processException(e: Exception) {
		categories = null
		position = null
		produceError()
	}

	override suspend fun saveEntityToDb(entity: List<Place>) {
		val placeDao = placeDaoFlow.first()
		val currentPlaces = placeDao.getAllPlaces().first()
		val currentImagesMap = currentPlaces
			.map { it.id to it.imageUrl }
			.toMap()
		val currentBlockedMap = currentPlaces
			.map { it.id to it.isBlocked }
			.toMap()
		val currentFavoriteMap = currentPlaces
			.map { it.id to it.isFavorite }
			.toMap()

		val placesToBeAdded = entity
			.map { placeDtoMapper.mapPlaceToDto(it) }
			.map { placeDto -> currentImagesMap[placeDto.id]?.let { placeDto.copy(imageUrl = it) } ?: placeDto }
			.map { placeDto -> currentBlockedMap[placeDto.id]?.let { placeDto.copy(isBlocked = it) } ?: placeDto }
			.map { placeDto -> currentFavoriteMap[placeDto.id]?.let { placeDto.copy(isFavorite = it) } ?: placeDto }
			.populateWithPhotos()

		placeDao.addPlaces(placesToBeAdded)
	}

	override suspend fun loadFromDb(): List<Place>? {
		return placeDaoFlow.flatMapLatest { placeDao ->
			storage
				.getDataCachingTime()
				.firstOrNull()
				?.let { placeDao.deleteOutdatedPlaces(Date.from(Instant.now()).time - it.toMillis()) }

			placeDao
				.getAllPlaces()
				.mapIterable { placeDtoMapper.mapDtoToPlace(it) }
		}.firstOrNull()
	}

	private suspend fun List<PlaceDto>.populateWithPhotos(): List<PlaceDto> {
		val countToRequest = count { it.imageUrl.isEmpty() }
		if (countToRequest == 0) return this
		val photos = unsplashPhotoProvider.getPhotos(countToRequest).toMutableList()
		return map { place ->
			when {
				place.imageUrl.isEmpty() -> place.copy(imageUrl = photos.removeFirstOrNull() ?: "")
				else -> place
			}
		}
	}

	private fun Position?.almostTheSameAs(other: Position?): Boolean {
		fun Position?.dropDigits(): Position? {
			return this?.run {
				Position(
					lon = round(lon * 100.0f) / 100.0f,
					lat = round(lat * 100.0f) / 100.0f
				)
			}
		}

		return dropDigits() == other.dropDigits()
	}

	private suspend fun getRadius(): Int {
		if (radius == null) {
			radius = storage.getPlacesRadius().first()
		}
		return radius!!
	}

	private suspend fun getPackCount(): Int {
		if (packCount == null) {
			packCount = storage.getPlacesPackCount().first()
		}
		return packCount!!
	}
}