package com.acanel.deskclock.repo.impl

import androidx.compose.ui.graphics.Color
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import com.acanel.deskclock.di.ClockSettingDataStore
import com.acanel.deskclock.entity.UnsplashTopicVO
import com.acanel.deskclock.repo.ClockSettingRepository
import com.acanel.deskclock.repo.db.DeskClockDao
import com.acanel.deskclock.repo.fb.DeskClockFbApi
import com.acanel.deskclock.utils.noSuspendException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AndroidClockSettingRepository @Inject constructor(
    @ClockSettingDataStore private val dataStore: DataStore<Preferences>,
    private val db: DeskClockDao,
    private val fb: DeskClockFbApi
) : BaseDataStoreRepository(dataStore), ClockSettingRepository {
    private val is24HourDisplay by lazy { booleanPreferencesKey("is24HourDisplay") }
    private val isBurnInPrevention by lazy { booleanPreferencesKey("burnInPrevention") }
    private val useClockBackgroundImage by lazy { booleanPreferencesKey("useClockBackgroundImage") }
    private val clockBackgroundTopic by lazy { stringPreferencesKey("clockBackgroundTopic") }
    private val clockFontSizeLevel by lazy { intPreferencesKey("clockFontSizeLevel") }
    private val clockFontShadowSizeLevel by lazy { intPreferencesKey("clockFontShadowSizeLevel") }
    private val clockFontColor by lazy { longPreferencesKey("clockFontColor") }
    private val unsplashTopicListSyncedTime by lazy { longPreferencesKey("unsplashTopicListSyncedTime") }

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

    override fun getClockBackgroundImageTopicSlugFlow(): Flow<String> = getPreferenceFlow(clockBackgroundTopic, DEFAULT_CLOCK_BACKGROUND_TOPIC.slug!!)
    override suspend fun setClockBackgroundImageTopicSlug(slug: String) = setPreference(clockBackgroundTopic, slug)

    override suspend fun getUnsplashTopicList(): List<UnsplashTopicVO> {
        val currentTime = System.currentTimeMillis()
        val syncedTime = getPreference(unsplashTopicListSyncedTime, 0)
        val list = db.getUnsplashTopicList()
        if (currentTime - syncedTime < UNSPLASH_TOPIC_LIST_CACHE_TIME && list.isNotEmpty()) {
            return list // return cached list
        }
        val syncedList = getUnsplashTopicListFormFirebase()
        return if (!syncedList.isNullOrEmpty()) {
            updateUnsplashTopicList(syncedList)
            setPreference(unsplashTopicListSyncedTime, currentTime)
            db.getUnsplashTopicList()
        } else {
            list.ifEmpty {
                listOf(DEFAULT_CLOCK_BACKGROUND_TOPIC)
            }
        }
    }

    private suspend fun updateUnsplashTopicList(topicList: List<UnsplashTopicVO>?) {
        db.removeUnsplashTopicListAll()
        db.addUnsplashTopic(DEFAULT_CLOCK_BACKGROUND_TOPIC)
        topicList?.let {
            db.addUnsplashTopicList(it)
        }
    }

    private suspend fun getUnsplashTopicListFormFirebase(): List<UnsplashTopicVO>? {
        val listResponse = noSuspendException { fb.getTopicList() }
        return if (listResponse?.body() != null && listResponse.isSuccessful) {
            listResponse.body()
        } else {
            null
        }
    }

    companion object {
        const val DEFAULT_CLOCK_BACKGROUND_TOPIC_SLUG = "random"
        val DEFAULT_CLOCK_BACKGROUND_TOPIC = UnsplashTopicVO(DEFAULT_CLOCK_BACKGROUND_TOPIC_SLUG, DEFAULT_CLOCK_BACKGROUND_TOPIC_SLUG, "Random")
        private const val UNSPLASH_TOPIC_LIST_CACHE_TIME = 1000 * 60 * 30 //30min
    }
}