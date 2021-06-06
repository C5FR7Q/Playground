package com.github.c5fr7q.playground.presentation.manager

import android.app.Activity

abstract class BaseManager {
	private var _activity: Activity? = null
	protected val activity: Activity get() = _activity!!

	fun attachActivity(activity: Activity) {
		_activity = activity
	}

	fun detachActivity() {
		_activity = null
	}
}