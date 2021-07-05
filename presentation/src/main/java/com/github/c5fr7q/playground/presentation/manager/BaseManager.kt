package com.github.c5fr7q.playground.presentation.manager

import androidx.appcompat.app.AppCompatActivity

abstract class BaseManager {
	private var _activity: AppCompatActivity? = null
	protected val activity: AppCompatActivity get() = _activity!!

	fun attachActivity(activity: AppCompatActivity) {
		_activity = activity
		onAttachActivity(activity)
	}

	fun detachActivity() {
		onDetachActivity()
		_activity = null
	}

	protected open fun onAttachActivity(activity: AppCompatActivity) {}

	protected open fun onDetachActivity() {}
}