package com.acanel.deskclock.entity

import androidx.annotation.Keep

@Keep
data class ClockTimeVO(
    val date: String,
    val time: String,
    val ampm: String?
)

@Keep
data class ClockTimeDisplayOptionVO(
    val dateFontSize: Int,
    val timeFontSize: Int,
    val ampmFontSize: Int,
    val fontShadowSize: Int,
    val fontColor: ULong
)

@Keep
data class ClockTimeAdjustLocationVO(
    val verticalBias: Float,
    val horizontalBias: Float
)