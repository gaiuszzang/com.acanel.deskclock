package com.acanel.deskclock.usecase

import com.acanel.deskclock.entity.ClockTimeDisplayOptionVO
import com.acanel.deskclock.entity.UnsplashTopicVO
import com.acanel.deskclock.repo.ClockSettingRepository
import com.acanel.deskclock.repo.impl.AndroidClockSettingRepository.Companion.DEFAULT_CLOCK_BACKGROUND_TOPIC
import com.acanel.deskclock.repo.impl.AndroidClockSettingRepository.Companion.DEFAULT_CLOCK_BACKGROUND_TOPIC_SLUG
import com.acanel.deskclock.utils.L
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import java.lang.Integer.max
import java.lang.Integer.min
import javax.inject.Inject

class ClockSettingUseCase @Inject constructor(
    private val repo: ClockSettingRepository
) {
    fun is24HourDisplay() = repo.is24HourDisplay()
    suspend fun set24HourDisplay(on: Boolean) = repo.set24HourDisplay(on)

    fun isBurnInPrevention() = repo.isBurnInPrevention()
    suspend fun setBurnInPrevention(on: Boolean) = repo.setBurnInPrevention(on)

    fun useClockBackgroundImage() = repo.useClockBackgroundImage()
    suspend fun setUseClockBackgroundImage(on: Boolean) = repo.setUseClockBackgroundImage(on)

    private val fontSizeTable = arrayOf(
        intArrayOf(90, 60, 30),
        intArrayOf(105, 70, 35),
        intArrayOf(120, 80, 40),
        intArrayOf(135, 90, 45),
        intArrayOf(150, 100, 50),
    )

    private val fontShadowSizeTable = intArrayOf(
        0, 10, 20, 30, 40
    )

    fun getClockFontSizeLevel() = repo.getClockFontSizeLevelFlow()
    suspend fun setClockFontSizeLevel(level: Int) = repo.setClockFontSizeLevel(level)
    fun getClockFontSizeLevelRange() = fontSizeTable.indices

    fun getClockFontShadowSizeLevel() = repo.getClockFontShadowSizeLevelFlow()
    suspend fun setClockFontShadowSizeLevel(level: Int) = repo.setClockFontShadowSizeLevel(level)
    fun getClockFontShadowSizeLevelRange() = fontShadowSizeTable.indices

    fun getClockFontColor() = repo.getClockFontColorFlow()
    suspend fun setClockFontColor(color: ULong) = repo.setClockFontColor(color)

    fun getClockTimeDisplayOptionFlow() = combine(
        repo.getClockFontSizeLevelFlow(),
        repo.getClockFontShadowSizeLevelFlow(),
        repo.getClockFontColorFlow()
    ) { fontSize, shadowSize, fontColor ->
        val fontLevel = min(max(0, fontSize), fontSizeTable.size - 1)
        val shadowLevel = min(max(0, shadowSize), fontShadowSizeTable.size - 1)
        ClockTimeDisplayOptionVO(
            timeFontSize = fontSizeTable[fontLevel][0],
            ampmFontSize = fontSizeTable[fontLevel][1],
            dateFontSize = fontSizeTable[fontLevel][2],
            fontShadowSize = fontShadowSizeTable[shadowLevel],
            fontColor = fontColor
        )
    }

    fun getClockBackgroundImageTopicFlow(): Flow<UnsplashTopicVO> {
        return repo.getClockBackgroundImageTopicSlugFlow().map { selectedSlug ->
            repo.getUnsplashTopicList().find { selectedSlug == it.slug } ?: DEFAULT_CLOCK_BACKGROUND_TOPIC
        }
    }

    suspend fun setClockBackgroundImageTopic(topic: UnsplashTopicVO) = repo.setClockBackgroundImageTopicSlug(topic.slug ?: DEFAULT_CLOCK_BACKGROUND_TOPIC_SLUG)

    suspend fun getClockBackgroundImageTopicList(): List<UnsplashTopicVO> {
        return repo.getUnsplashTopicList()
    }
}