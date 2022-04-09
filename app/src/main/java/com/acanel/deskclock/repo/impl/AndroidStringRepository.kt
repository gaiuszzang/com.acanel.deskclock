package com.acanel.deskclock.repo.impl

import android.content.Context
import com.acanel.deskclock.R
import com.acanel.deskclock.repo.StringRepository
import com.acanel.deskclock.repo.StringRepository.Type
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class AndroidStringRepository @Inject constructor(@ApplicationContext private val context: Context): StringRepository {
    override fun get(type: Type): String {
        return when (type) {
            Type.ClockDateFormat -> context.getString(R.string.clock_date_format)
            Type.SettingMenuTitleClockTimeDisplay -> context.getString(R.string.setting_menu_title_clock_time_display)
            Type.SettingMenuTitle24HourFormat -> context.getString(R.string.setting_menu_title_24hour_format)
            Type.SettingMenuTitleBurnInPrevention -> context.getString(R.string.setting_menu_title_burn_in_prevention)
            Type.SettingMenuTitleClockFontSize -> context.getString(R.string.setting_menu_title_clock_font_size)
            Type.SettingMenuTitleClockFontShadowSize -> context.getString(R.string.setting_menu_title_clock_font_shadow_size)
            Type.SettingMenuTitleClockFontColor -> context.getString(R.string.setting_menu_title_clock_font_color)
            Type.SettingMenuTitleClockBackground -> context.getString(R.string.setting_menu_title_clock_background)
            Type.SettingMenuTitleUseBackgroundImage -> context.getString(R.string.setting_menu_title_use_background_image)
            Type.SettingMenuTitleBackgroundImageTopic -> context.getString(R.string.setting_menu_title_background_image_topic)
        }
    }
}