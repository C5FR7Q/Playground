package com.github.c5fr7q.playground.presentation.manager

import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NetworkStateManager @Inject constructor() : BaseManager() {
	private val connectivityManager get() = activity.getSystemService(ConnectivityManager::class.java)
	private val _isConnected = MutableStateFlow(false)

	private val networkCallback = object : ConnectivityManager.NetworkCallback() {
		override fun onAvailable(network: Network) {
			_isConnected.value = true
		}

		override fun onLost(network: Network) {
			_isConnected.value = false
		}
	}

	override fun onAttachActivity(activity: AppCompatActivity) {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
			connectivityManager.registerDefaultNetworkCallback(networkCallback)
		} else {
			val request = NetworkRequest.Builder()
				.addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
				.build()
			connectivityManager.registerNetworkCallback(request, networkCallback)
		}
	}

	override fun onDetachActivity() {
		connectivityManager.unregisterNetworkCallback(networkCallback)
	}

	val isConnected: Flow<Boolean> = _isConnected.asStateFlow()
}