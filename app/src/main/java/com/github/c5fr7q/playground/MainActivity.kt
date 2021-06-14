package com.github.c5fr7q.playground

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import com.github.c5fr7q.playground.presentation.manager.LocationManager
import com.github.c5fr7q.playground.presentation.manager.NavigationManager
import com.github.c5fr7q.playground.presentation.manager.NetworkStateManager
import com.github.c5fr7q.playground.presentation.manager.PermissionManager
import com.github.c5fr7q.playground.presentation.ui.Main
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

	@Inject
	lateinit var navigationManager: NavigationManager

	@Inject
	lateinit var permissionManager: PermissionManager

	@Inject
	lateinit var locationManager: LocationManager

	@Inject
	lateinit var networkStateManager: NetworkStateManager

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		attachManagers()
		setContent {
			Main(navigationManager)
		}
	}

	override fun onDestroy() {
		detachManagers()
		super.onDestroy()
	}

	override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
		super.onRequestPermissionsResult(requestCode, permissions, grantResults)
	}

	private fun attachManagers() {
		navigationManager.attachActivity(this)
		permissionManager.attachActivity(this)
		locationManager.attachActivity(this)
		networkStateManager.attachActivity(this)
	}

	private fun detachManagers() {
		navigationManager.detachActivity()
		permissionManager.detachActivity()
		locationManager.detachActivity()
		networkStateManager.detachActivity()
	}
}