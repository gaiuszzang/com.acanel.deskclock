package com.acanel.deskclock

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class DeskClockApplication: Application() {
    override fun onCreate() {
        super.onCreate()
    }
}