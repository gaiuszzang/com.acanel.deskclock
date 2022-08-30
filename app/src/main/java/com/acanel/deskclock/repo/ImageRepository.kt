package com.acanel.deskclock.repo

import com.acanel.deskclock.entity.RepoResult
import com.acanel.deskclock.entity.UnsplashImageVO

interface ImageRepository {
    suspend fun getBackgroundImage(slug: String): RepoResult<UnsplashImageVO>
}