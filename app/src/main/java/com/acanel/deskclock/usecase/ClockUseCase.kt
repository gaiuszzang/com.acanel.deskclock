package com.acanel.deskclock.usecase

import com.acanel.deskclock.entity.ClockTimeAdjustLocationVO
import com.acanel.deskclock.entity.ClockTimeDisplayOptionVO
import com.acanel.deskclock.entity.ClockTimeVO
import com.acanel.deskclock.entity.UnsplashImageVO
import com.acanel.deskclock.repo.ClockRepository
import com.acanel.deskclock.repo.ClockSettingRepository
import com.acanel.deskclock.repo.ImageRepository
import com.acanel.deskclock.utils.flow.periodFlow
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject
import kotlin.random.Random

class ClockUseCase @Inject constructor(
    private val clockRepo: ClockRepository,
    private val clockSettingRepo: ClockSettingRepository,
    private val imageRepo: ImageRepository
) {
    fun getClockTimeFlow(period: Long = TIME_UPDATE_MS): Flow<ClockTimeVO> {
        return clockSettingRepo.is24HourDisplay().combine(clockRepo.getCurrentDateFlow(period)) { is24HourDisplay, date ->
            val pattern = if (is24HourDisplay) "H:mm" else "h:mm"
            return@combine ClockTimeVO(
                date = SimpleDateFormat("MM월 dd일 E요일", Locale.getDefault()).format(date),
                time = SimpleDateFormat(pattern, Locale.ENGLISH).format(date),
                ampm = if (!is24HourDisplay) SimpleDateFormat("a", Locale.ENGLISH).format(date) else null
            )
        }
    }

    fun getBackImageFlow(period: Long = TIME_BACKIMAGE_UPDATE_MS): Flow<UnsplashImageVO?> {
        return periodFlow(period).combine(clockSettingRepo.useClockBackgroundImage()) { _, useClockBackgroundImage ->
            if (useClockBackgroundImage) {
                val imagePath = if (clockSettingRepo.useClockBackgroundImage().first()) {
                    val slug = clockSettingRepo.getClockBackgroundImageTopicSlugFlow().first()
                    val response = imageRepo.getBackgroundImageFlow(slug).first()
                    if (response.isSucceed && response.data != null) response.data else null
                } else {
                    null
                }
                return@combine (imagePath)
            } else {
                return@combine null
            }
        }
    }

    fun getClockTimeAdjustLocationFlow(period: Long = TIME_LOCATION_UPDATE_MS): Flow<ClockTimeAdjustLocationVO> {
        return periodFlow(period).combine(clockSettingRepo.isBurnInPrevention()) {_, isBurnInPrevention ->
            val verticalBias = if (isBurnInPrevention) Random.nextFloat() else 0.5f
            val horizontalBias = if (isBurnInPrevention) Random.nextFloat() else 0.5f
            return@combine ClockTimeAdjustLocationVO(verticalBias, horizontalBias)
        }
    }

    companion object {
        private const val TIME_UPDATE_MS = 1000L //1s
        private const val TIME_LOCATION_UPDATE_MS = 1000L * 30L //30s
        private const val TIME_BACKIMAGE_UPDATE_MS = 1000L * 60L * 10L //10m
    }
}