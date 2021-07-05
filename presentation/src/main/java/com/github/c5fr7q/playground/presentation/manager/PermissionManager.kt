package com.github.c5fr7q.playground.presentation.manager

import android.content.pm.PackageManager
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import dagger.Lazy
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

@Singleton
class PermissionManager @Inject constructor(
	_navigationManager: Lazy<NavigationManager>
) : BaseManager() {
	private val navigationManager by lazy { _navigationManager.get() }
	private var callback: (result: MutableMap<String, Boolean>) -> Unit = {}
	private var requestPermissionLauncher: ActivityResultLauncher<Array<String>>? = null

	override fun onAttachActivity(activity: AppCompatActivity) {
		requestPermissionLauncher =
			activity.registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { callback(it) }
	}

	suspend fun requestPermission(
		permission: String,
		rationaleMessage: String,
		createFallbackMessage: (List<String>) -> String = { "" }
	) = requestPermissions(arrayOf(permission), rationaleMessage, createFallbackMessage)

	suspend fun requestPermissions(
		permissions: Array<String>,
		rationaleMessage: String,
		createFallbackMessage: (List<String>) -> String = { "" }
	): Boolean {
		if (checkPermissions(permissions)) return true
		val showRationale = permissions.map { activity.shouldShowRequestPermissionRationale(it) }.contains(true)
		if (showRationale) {
			if (!showRationale(rationaleMessage)) return false
		}
		return requestPermissions(createFallbackMessage, permissions)
	}

	private fun checkPermissions(permissions: Array<String>): Boolean {
		var isGranted = true
		permissions.forEach { permission ->
			isGranted = isGranted && ActivityCompat.checkSelfPermission(activity, permission) == PackageManager.PERMISSION_GRANTED
		}
		return isGranted
	}

	private suspend fun requestPermissions(createFallbackMessage: (List<String>) -> String, permissions: Array<String>): Boolean {
		return suspendCoroutine { cont ->
			callback = { grantedMap ->
				val granted = grantedMap.values.all { it }
				if (!granted) {
					val fallbackMessage = createFallbackMessage(grantedMap.filter { !it.value }.keys.toList())
					if (fallbackMessage.isNotEmpty()) {
						navigationManager.openConfirmationDialog(
							text = fallbackMessage
						)
					}
				}
				cont.resume(granted)
			}
			requestPermissionLauncher?.launch(permissions)
		}
	}

	private suspend fun showRationale(rationaleMessage: String): Boolean {
		return suspendCoroutine { cont ->
			navigationManager.openConfirmationDialog(
				text = rationaleMessage,
				onDismissRequest = { cont.resume(false) },
				onConfirmClick = { cont.resume(true) }
			)
		}
	}

}