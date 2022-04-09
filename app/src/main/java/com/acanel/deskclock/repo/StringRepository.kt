package com.acanel.deskclock.repo

interface StringRepository {
    fun get(type: Type): String

    enum class Type {
        ClockDateFormat,
        SettingMenuTitleClockTimeDisplay,
        SettingMenuTitle24HourFormat,
        SettingMenuTitleBurnInPrevention,
        SettingMenuTitleClockFontSize,
        SettingMenuTitleClockFontShadowSize,
        SettingMenuTitleClockFontColor,
        SettingMenuTitleClockBackground,
        SettingMenuTitleUseBackgroundImage,
        SettingMenuTitleBackgroundImageTopic,
    }
}

