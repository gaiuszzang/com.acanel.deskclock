package com.acanel.deskclock

import android.content.Context
import okhttp3.OkHttpClient

object AppInitializer {
    lateinit var defaultOkHttpClient: OkHttpClient

    fun init(context: Context) {
        initNetwork()
    }

    private fun initNetwork() {
        defaultOkHttpClient = OkHttpClient.Builder().build()
    }
}