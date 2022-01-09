package com.acanel.deskclock.utils.flow

import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.flow

fun periodFlow(period: Long) = flow {
    while (true) {
        emit(null)
        delay(period)
    }
}