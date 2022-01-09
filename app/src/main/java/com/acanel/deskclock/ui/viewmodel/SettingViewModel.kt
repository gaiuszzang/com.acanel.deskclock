package com.acanel.deskclock.ui.viewmodel

import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.acanel.deskclock.usecase.ClockSettingUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingViewModel @Inject constructor(
    private val clockSettingUseCase: ClockSettingUseCase
) : ViewModel() {
    val uiState = mutableStateOf<SettingScreenState>(SettingScreenState.Init())

    private val menuList = mutableListOf<SettingMenu>()
    private val menuEventMap = mutableMapOf<String, SettingMenuAction>()

    init {
        initMenuList()
    }

    private fun initMenuList() {
        addTitleMenu("Clock Setting") //TODO
        addOnOffMenu(
            key = SETTING_KEY_CLOCK_24HOUR_DISPLAY,
            title = "Clock 24Hour Display",
            flow = clockSettingUseCase.is24HourDisplay()
        ) { clockSettingUseCase.set24HourDisplay(it) }
        addOnOffMenu(
            key = SETTING_KEY_CLOCK_BURN_IN_PREVENT,
            title = "Clock Burn-in Prevention",
            flow = clockSettingUseCase.isBurnInPrevention()
        ) { clockSettingUseCase.setBurnInPrevention(it) }
        addOnOffMenu(
            key = SETTING_KEY_CLOCK_USE_BACKGROUND_IMAGE,
            title = "Use the Background Image",
            flow = clockSettingUseCase.useClockBackgroundImage()
        ) { clockSettingUseCase.setUseClockBackgroundImage(it) }
        addIntSlideDialogMenu(
            key = SETTING_KEY_CLOCK_FONT_SIZE_LEVEL,
            title = "Clock Font Size",
            dialogTitle = "Set Clock Font Size",
            defaultValue = 2,
            valueRange = clockSettingUseCase.getClockFontSizeLevelRange(),
            flow = clockSettingUseCase.getClockFontSizeLevel()
        ) {
            clockSettingUseCase.setClockFontSizeLevel(it)
        }
        addIntSlideDialogMenu(
            key = SETTING_KEY_CLOCK_FONT_SHADOW_SIZE_LEVEL,
            title = "Clock Font Shadow Size",
            dialogTitle = "Set Clock Font Shadow Size",
            defaultValue = 2,
            valueRange = clockSettingUseCase.getClockFontShadowSizeLevelRange(),
            flow = clockSettingUseCase.getClockFontShadowSizeLevel()
        ) {
            clockSettingUseCase.setClockFontShadowSizeLevel(it)
        }
        addColorPickerDialogMenu(
            key = SETTING_KEY_CLOCK_FONT_COLOR,
            title = "Clock Font Color",
            dialogTitle = "Pick Clock Font Color",
            defaultColor = Color.White.value,
            flow = clockSettingUseCase.getClockFontColor()
        ) {
            clockSettingUseCase.setClockFontColor(it)
        }
    }

    fun onClickMenu(menu: SettingMenu) {
        if (menu is SettingMenu.SettingOnOffMenu) {
            when (val menuEvent = menuEventMap[menu.key]) {
                is SettingMenuAction.SettingOnOffMenuClickAction -> {
                    viewModelScope.launch {
                        menuEvent.onClickAction(!menu.isOn)
                    }
                }
                else -> {
                    //nothing to do
                }
            }
        }
    }

    fun onIntSlideValueChanged(menu: SettingMenu, value: Int) {
        if (menu is SettingMenu.SettingIntSlideDialogMenu) {
            when (val menuEvent = menuEventMap[menu.key]) {
                is SettingMenuAction.SettingIntValueChangeAction -> {
                    viewModelScope.launch {
                        menuEvent.onValueChangedAction(value)
                    }
                }
                else -> { /* nothing to do */ }
            }
        }
    }

    fun onColorPickerChanged(menu: SettingMenu, color: ULong) {
        if (menu is SettingMenu.SettingColorPickerDialogMenu) {
            when (val menuEvent = menuEventMap[menu.key]) {
                is SettingMenuAction.SettingULongValueChangeAction -> {
                    viewModelScope.launch {
                        menuEvent.onValueChangedAction(color)
                    }
                }
                else -> { /* nothing to do */ }
            }
        }
    }

    private fun addTitleMenu(title: String) {
        menuList.add(SettingMenu.SettingTitle(title))
    }

    private fun addOnOffMenu(key: String, title: String, flow: Flow<Boolean>, setMethod: suspend (Boolean) -> Unit) {
        menuList.add(SettingMenu.SettingOnOffMenu(key, title, false))
        menuEventMap[key] = SettingMenuAction.SettingOnOffMenuClickAction(setMethod)
        viewModelScope.launch {
            flow.collect { value ->
                val index = menuList.indexOfFirst { (it is SettingMenu.SettingOnOffMenu) && (it.key == key) }
                menuList[index] = SettingMenu.SettingOnOffMenu(key, title, value)
                emitMenuList()
            }
        }
    }

    private fun addIntSlideDialogMenu(key: String, title: String, dialogTitle: String, defaultValue: Int, valueRange: ClosedRange<Int>, flow: Flow<Int>, setMethod: suspend (Int) -> Unit) {
        menuList.add(SettingMenu.SettingIntSlideDialogMenu(key, title, dialogTitle, defaultValue, valueRange))
        menuEventMap[key] = SettingMenuAction.SettingIntValueChangeAction(setMethod)
        viewModelScope.launch {
            flow.collect { value ->
                val index = menuList.indexOfFirst { (it is SettingMenu.SettingIntSlideDialogMenu) && (it.key == key)}
                menuList[index] = SettingMenu.SettingIntSlideDialogMenu(key, title, dialogTitle, value, valueRange)
                emitMenuList()
            }
        }
    }

    private fun addColorPickerDialogMenu(key: String, title: String, dialogTitle: String, defaultColor: ULong, flow: Flow<ULong>, setMethod: suspend (ULong) -> Unit) {
        menuList.add(SettingMenu.SettingColorPickerDialogMenu(key, title, dialogTitle, defaultColor))
        menuEventMap[key] = SettingMenuAction.SettingULongValueChangeAction(setMethod)
        viewModelScope.launch {
            flow.collect { value ->
                val index = menuList.indexOfFirst { (it is SettingMenu.SettingColorPickerDialogMenu) && (it.key == key)}
                menuList[index] = SettingMenu.SettingColorPickerDialogMenu(key, title, dialogTitle, value)
                emitMenuList()
            }
        }

    }

    private fun emitMenuList() {
        uiState.value = (SettingScreenState.Loaded(menuList))
    }

    companion object {
        const val SETTING_KEY_CLOCK_24HOUR_DISPLAY = "Clock24HourDisplay"
        const val SETTING_KEY_CLOCK_BURN_IN_PREVENT = "ClockBurnInPrevention"
        const val SETTING_KEY_CLOCK_USE_BACKGROUND_IMAGE = "ClockUseBackgroundImage"
        const val SETTING_KEY_CLOCK_FONT_SIZE_LEVEL = "ClockFontSizeLevel"
        const val SETTING_KEY_CLOCK_FONT_SHADOW_SIZE_LEVEL = "ClockFontShadowSizeLevel"
        const val SETTING_KEY_CLOCK_FONT_COLOR = "ClockFontColor"
    }
}


sealed interface SettingMenu {
    data class SettingTitle(val title: String): SettingMenu
    data class SettingOnOffMenu(val key: String, val title: String, val isOn: Boolean): SettingMenu
    data class SettingIntSlideDialogMenu(
        val key: String,
        val title: String,
        val dialogTitle: String,
        val value: Int,
        val range: ClosedRange<Int>
    ): SettingMenu
    data class SettingColorPickerDialogMenu(
        val key: String,
        val title: String,
        val dialogTitle: String,
        val color: ULong
    ): SettingMenu
}

sealed interface SettingMenuAction {
    data class SettingOnOffMenuClickAction(val onClickAction: suspend (Boolean) -> Unit): SettingMenuAction
    data class SettingIntValueChangeAction(val onValueChangedAction: suspend (Int) -> Unit): SettingMenuAction
    data class SettingULongValueChangeAction(val onValueChangedAction: suspend (ULong) -> Unit): SettingMenuAction
}

sealed interface SettingScreenState {
    class Init: SettingScreenState
    class Loaded(val menuList: List<SettingMenu>): SettingScreenState
}