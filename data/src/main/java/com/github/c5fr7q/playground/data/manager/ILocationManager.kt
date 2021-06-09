package com.github.c5fr7q.playground.data.manager

import com.github.c5fr7q.playground.domain.entity.Position

interface ILocationManager {
	suspend fun getLastKnownLocation(): Position?
}