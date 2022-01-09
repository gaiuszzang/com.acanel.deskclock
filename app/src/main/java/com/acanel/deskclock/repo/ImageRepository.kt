package com.acanel.deskclock.repo

import com.acanel.deskclock.entity.RepoResult
import com.acanel.deskclock.entity.UnsplashImageVO
import kotlinx.coroutines.flow.Flow

interface ImageRepository {
    fun getBackgroundImageFlow(): Flow<RepoResult<UnsplashImageVO>>
}