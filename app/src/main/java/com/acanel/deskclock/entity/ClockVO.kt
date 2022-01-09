package com.acanel.deskclock.entity

data class ClockTimeVO(
    val date: String,
    val time: String,
    val ampm: String?
)

data class ClockTimeDisplayOptionVO(
    val dateFontSize: Int,
    val timeFontSize: Int,
    val ampmFontSize: Int,
    val fontShadowSize: Int,
    val fontColor: ULong
)

data class ClockTimeAdjustLocationVO(
    val verticalBias: Float,
    val horizontalBias: Float
)