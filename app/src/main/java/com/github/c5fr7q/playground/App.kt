package com.github.c5fr7q.playground

import android.app.Application
import com.github.c5fr7q.playground.presentation.manager.ManagersAssembler
import dagger.hilt.android.HiltAndroidApp
import timber.log.Timber
import javax.inject.Inject

@HiltAndroidApp
class App : Application() {
	@Inject
	lateinit var managersAssembler: ManagersAssembler

	override fun onCreate() {
		super.onCreate()
		Timber.plant(Timber.DebugTree())
	}
}