package com.acanel.deskclock.repo.impl

import com.acanel.deskclock.repo.ClockRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.util.*
import javax.inject.Inject

class AndroidClockRepository @Inject constructor() : ClockRepository {
    override fun getCurrentDate(): Date = Calendar.getInstance().time

    override fun getCurrentDateFlow(period: Long): Flow<Date> = flow {
        while (true) {
            emit(getCurrentDate())
            delay(period)
        }
    }
}