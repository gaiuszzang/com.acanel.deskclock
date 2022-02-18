package com.acanel.deskclock.utils

import java.lang.Exception

inline fun <T> noException(crossinline func: () -> T, defValue: T): T {
    return try {
        func()
    } catch (e: Exception) {
        defValue
    }
}

inline fun <T> noException(crossinline func: () -> T?): T? {
    return try {
        func()
    } catch (e: Exception) {
        null
    }
}

suspend inline fun <T> noSuspendException(crossinline func: suspend () -> T?): T? {
    return try {
        func()
    } catch (e: Exception) {
        null
    }
}
