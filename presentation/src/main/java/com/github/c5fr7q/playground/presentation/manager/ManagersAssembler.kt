package com.github.c5fr7q.playground.presentation.manager

import android.app.Activity
import android.app.Application
import android.content.Context
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ManagersAssembler @Inject constructor(
	@ApplicationContext private val context: Context,
	managers: Set<@JvmSuppressWildcards BaseManager>
) {

	init {
		(context as Application).registerActivityLifecycleCallbacks(object : Application.ActivityLifecycleCallbacks {
			override fun onActivityStarted(activity: Activity) {}
			override fun onActivityResumed(activity: Activity) {}
			override fun onActivityPaused(activity: Activity) {}
			override fun onActivityStopped(activity: Activity) {}
			override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {}

			override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {
				managers.forEach { it.attachActivity(activity as AppCompatActivity) }
			}

			override fun onActivityDestroyed(activity: Activity) {
				managers.forEach { it.detachActivity() }
			}
		})

	}
}