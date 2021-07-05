package com.github.c5fr7q.playground.presentation.manager

import android.Manifest.permission.ACCESS_COARSE_LOCATION
import android.Manifest.permission.ACCESS_FINE_LOCATION
import androidx.annotation.RequiresPermission
import androidx.appcompat.app.AppCompatActivity
import com.github.c5fr7q.playground.data.manager.ILocationManager
import com.github.c5fr7q.playground.domain.entity.Position
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

@Singleton
class LocationManager @Inject constructor() : BaseManager(), ILocationManager {
	private var fusedLocationClient: FusedLocationProviderClient? = null

	override fun onAttachActivity(activity: AppCompatActivity) {
		fusedLocationClient = LocationServices.getFusedLocationProviderClient(activity)
	}

	@RequiresPermission(allOf = [ACCESS_COARSE_LOCATION, ACCESS_FINE_LOCATION])
	override suspend fun getLastKnownLocation(): Position? {
		return suspendCoroutine { cont ->
			fusedLocationClient!!.lastLocation
				.addOnSuccessListener(activity) { location ->
					cont.resume(
						location?.let {
							Position(it.longitude.toFloat(), it.latitude.toFloat())
						}
					)
				}
				.addOnFailureListener(activity) { cont.resume(null) }
		}
	}

	override fun onDetachActivity() {
		fusedLocationClient = null
	}
}