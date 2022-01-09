package com.acanel.deskclock.repo

import kotlinx.coroutines.flow.Flow

interface ClockSettingRepository {
    fun is24HourDisplay(): Flow<Boolean>
    suspend fun set24HourDisplay(on: Boolean)

    fun isBurnInPrevention(): Flow<Boolean>
    suspend fun setBurnInPrevention(on: Boolean)

    fun useClockBackgroundImage(): Flow<Boolean>
    suspend fun setUseClockBackgroundImage(on: Boolean)

    fun getClockFontSizeLevelFlow(): Flow<Int>
    suspend fun getClockFontSizeLevel(): Int
    suspend fun setClockFontSizeLevel(level: Int)

    fun getClockFontShadowSizeLevelFlow(): Flow<Int>
    suspend fun getClockFontShadowSizeLevel(): Int
    suspend fun setClockFontShadowSizeLevel(level: Int)

    fun getClockFontColorFlow(): Flow<ULong>
    suspend fun setClockFontColor(color: ULong)
}