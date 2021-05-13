package com.github.c5fr7q.playground.data.source.remote.sygic

import com.github.c5fr7q.playground.data.source.remote.sygic.entity.SygicPlacesResponse
import retrofit2.http.GET
import retrofit2.http.Query


interface SygicService {
	@GET("places/list?level=poi")
	suspend fun getPlaces(
		@Query("categories") categories: String,
		@Query("area") filter: String,
		@Query("limit") limit: Int,
		@Query("offset") offset: Int
	): SygicPlacesResponse
}