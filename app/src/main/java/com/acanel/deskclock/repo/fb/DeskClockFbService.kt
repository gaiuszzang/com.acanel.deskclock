package com.acanel.deskclock.repo.fb

import com.acanel.deskclock.AppInitializer
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object DeskClockFbService {
    val service: DeskClockFbApi
        get() {
            val retrofit = Retrofit.Builder()
                .baseUrl("https://us-central1-acanel-deskclock.cloudfunctions.net")
                .addConverterFactory(GsonConverterFactory.create())
                .client(AppInitializer.defaultOkHttpClient)
                .build()

            return retrofit.create(DeskClockFbApi::class.java)
        }
}