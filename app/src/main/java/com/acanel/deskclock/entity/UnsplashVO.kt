package com.acanel.deskclock.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

data class UnsplashImageVO(
    val id: String? = null,
    @SerializedName("created_at")
    val createdAt: String? = null,
    @SerializedName("updated_at")
    val updatedAt: String? = null,
    @SerializedName("promoted_at")
    val promotedAt: String? = null,
    val width: Int = 0,
    val height: Int = 0,
    val color: String? = null,
    val blur_hash: String? = null,
    val description: String? = null,
    @SerializedName("alt_description")
    val altDescription: String? = null,
    val urls: UnsplashImageUrlVO? = null,
    val links: UnsplashLinkVO? = null,
    val user: UnsplashUserVO? = null
)

data class UnsplashImageUrlVO(
    val raw: String? = null,
    val full: String? = null,
    val regular: String? = null,
    val thumb: String? = null,
    val small: String? = null,
    val medium: String? = null,
    val large: String? = null,
)

data class UnsplashLinkVO(
    val self: String? = null,
    val html: String? = null,
    val download: String? = null,
    @SerializedName("download_location")
    val downloadLocation: String? = null,
    val photos: String? = null,
    val likes: String? = null,
    val portfolio: String? = null,
    val following: String? = null,
    val followers: String? = null
)

data class UnsplashUserVO(
    val id: String? = null,
    val username: String? = null,
    val name: String? = null,
    @SerializedName("portfolio_url")
    val portfolioUrl: String? = null,
    val links: UnsplashLinkVO? = null,
    @SerializedName("profile_image")
    val profileImage : UnsplashImageUrlVO? = null
)

@Entity(tableName = "UnsplashTopic", primaryKeys = ["id"])
data class UnsplashTopicVO(
    val id: String,
    val slug: String?,
    val title: String?
)