package com.acanel.deskclock.repo

import kotlinx.coroutines.flow.Flow
import java.util.*

interface ClockRepository {
    fun getCurrentDate(): Date
    fun getCurrentDateFlow(period: Long): Flow<Date>
}