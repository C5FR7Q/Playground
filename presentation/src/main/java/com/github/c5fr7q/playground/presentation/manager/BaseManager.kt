package com.github.c5fr7q.playground.presentation.manager

import androidx.annotation.CallSuper
import androidx.appcompat.app.AppCompatActivity

abstract class BaseManager {
	private var _activity: AppCompatActivity? = null
	protected val activity: AppCompatActivity get() = _activity!!

	@CallSuper
	open fun attachActivity(activity: AppCompatActivity) {
		_activity = activity
	}

	@CallSuper
	open fun detachActivity() {
		_activity = null
	}
}