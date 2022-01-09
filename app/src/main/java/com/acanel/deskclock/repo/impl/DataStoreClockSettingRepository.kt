package com.acanel.deskclock.repo.impl

import androidx.compose.ui.graphics.Color
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import com.acanel.deskclock.di.ClockSettingDataStore
import com.acanel.deskclock.repo.ClockSettingRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class DataStoreClockSettingRepository @Inject constructor(
    @ClockSettingDataStore private val dataStore: DataStore<Preferences>
) : BaseDataStoreRepository(dataStore), ClockSettingRepository {
    private val is24HourDisplay by lazy { booleanPreferencesKey("is24HourDisplay") }
    private val isBurnInPrevention by lazy { booleanPreferencesKey("burnInPrevention") }
    private val useClockBackgroundImage by lazy { booleanPreferencesKey("useClockBackgroundImage") }
    private val clockFontSizeLevel by lazy { intPreferencesKey("clockFontSizeLevel") }
    private val clockFontShadowSizeLevel by lazy { intPreferencesKey("clockFontShadowSizeLevel") }
    private val clockFontColor by lazy { longPreferencesKey("clockFontColor") }


    override fun is24HourDisplay(): Flow<Boolean> = getPreferenceFlow(is24HourDisplay, false)
    override suspend fun set24HourDisplay(on: Boolean) = setPreference(is24HourDisplay, on)

    override fun isBurnInPrevention(): Flow<Boolean> = getPreferenceFlow(isBurnInPrevention, true)
    override suspend fun setBurnInPrevention(on: Boolean) = setPreference(isBurnInPrevention, on)

    override fun useClockBackgroundImage(): Flow<Boolean> = getPreferenceFlow(useClockBackgroundImage, false)
    override suspend fun setUseClockBackgroundImage(on: Boolean) = setPreference(useClockBackgroundImage, on)

    override fun getClockFontSizeLevelFlow(): Flow<Int> = getPreferenceFlow(clockFontSizeLevel, 2)
    override suspend fun getClockFontSizeLevel(): Int = getPreference(clockFontSizeLevel, 2)
    override suspend fun setClockFontSizeLevel(level: Int) = setPreference(clockFontSizeLevel, level)

    override fun getClockFontShadowSizeLevelFlow(): Flow<Int> = getPreferenceFlow(clockFontShadowSizeLevel, 2)
    override suspend fun getClockFontShadowSizeLevel(): Int = getPreference(clockFontShadowSizeLevel, 2)
    override suspend fun setClockFontShadowSizeLevel(level: Int) = setPreference(clockFontShadowSizeLevel, level)

    override fun getClockFontColorFlow(): Flow<ULong> = getPreferenceFlow(clockFontColor, Color.White.value.toLong()).map { it.toULong() }
    override suspend fun setClockFontColor(color: ULong) = setPreference(clockFontColor, color.toLong())
}