package com.github.c5fr7q.util

import android.content.Context
import androidx.annotation.StringRes
import javax.inject.Inject

class ResourceHelper @Inject constructor(
	private val context: Context
) {
	fun getString(@StringRes resId: Int) = context.getString(resId)
}