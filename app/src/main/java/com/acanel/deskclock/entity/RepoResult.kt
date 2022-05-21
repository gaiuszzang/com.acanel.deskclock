package com.acanel.deskclock.entity

import androidx.annotation.Keep

@Keep
data class RepoResult<T>(
    val isSucceed: Boolean = true,
    val data: T? = null,
    val error: ErrorType? = null
)

@Keep
enum class ErrorType {
    NETWORK_ERROR,
    NO_RESPONSE,
    EMPTY_DATA
}