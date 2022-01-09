package com.acanel.deskclock.entity

data class RepoResult<T>(
    val isSucceed: Boolean = true,
    val data: T? = null,
    val error: ErrorType? = null
)

enum class ErrorType {
    NETWORK_ERROR,
    NO_RESPONSE,
    EMPTY_DATA
}