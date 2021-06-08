package com.github.c5fr7q.util

import android.content.Context
import androidx.annotation.StringRes
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ResourceHelper @Inject constructor(
	@ApplicationContext private val context: Context
) {
	fun getString(@StringRes resId: Int) = context.getString(resId)
}