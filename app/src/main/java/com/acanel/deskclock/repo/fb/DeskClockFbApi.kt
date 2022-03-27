package com.acanel.deskclock.repo.fb

import com.acanel.deskclock.entity.UnsplashImageVO
import com.acanel.deskclock.entity.UnsplashTopicVO
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path

interface DeskClockFbApi {
    @GET("/api/backgroundImage/{slug}")
    suspend fun getBackgroundImage(@Path("slug") slug: String) : Response<UnsplashImageVO>

    @GET("/api/topicList")
    suspend fun getTopicList(): Response<List<UnsplashTopicVO>>
}
