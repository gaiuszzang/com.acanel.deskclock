package com.acanel.deskclock.ui.viewmodel

import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.acanel.deskclock.usecase.ClockSettingUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingViewModel @Inject constructor(
    private val clockSettingUseCase: ClockSettingUseCase
) : ViewModel() {

    val uiState = mutableStateOf<SettingScreenState>(SettingScreenState.Init())

    private val menuList = mutableListOf<SettingMenu>()

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
        addRadioButtonListDialogMenu(
            key = SETTING_KEY_CLOCK_TOPIC_LIST,
            title = "Clock Background Image Topics",
            defaultValue = "",
            itemToValue = { it.title ?: "" },
            selectedItemFlow = clockSettingUseCase.getClockBackgroundImageTopicFlow(),
            dialogTitle = "Clock Background Image Topic List",
            dialogContentLoadListener = {
                val list = clockSettingUseCase.getClockBackgroundImageTopicList()
                val selectedItem = clockSettingUseCase.getClockBackgroundImageTopicFlow().first()
                val selectedIndex = list.indexOfFirst { it.slug == selectedItem.slug }
                RadioButtonListDialogContent.Loaded(list, selectedIndex)
            },
            selectedItemListener = { clockSettingUseCase.setClockBackgroundImageTopic(it) }
        )
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

    private fun addTitleMenu(title: String) {
        menuList.add(SettingMenu.SettingTitle(title))
    }

    private fun addOnOffMenu(key: String, title: String, flow: Flow<Boolean>, updateCallback: suspend (Boolean) -> Unit) {
        menuList.add(SettingMenu.SettingOnOffMenu(key, title, false, updateCallback))
        viewModelScope.launch {
            flow.collect { value ->
                val index = menuList.indexOfFirst { (it is SettingMenu.SettingOnOffMenu) && (it.key == key) }
                menuList[index] = SettingMenu.SettingOnOffMenu(key, title, value, updateCallback)
                emitMenuList()
            }
        }
    }

    private fun addIntSlideDialogMenu(key: String, title: String, dialogTitle: String, defaultValue: Int, valueRange: ClosedRange<Int>, flow: Flow<Int>, updateCallback: suspend (Int) -> Unit) {
        menuList.add(SettingMenu.SettingIntSlideDialogMenu(key, title, dialogTitle, defaultValue, valueRange, updateCallback))
        viewModelScope.launch {
            flow.collect { value ->
                val index = menuList.indexOfFirst { (it is SettingMenu.SettingIntSlideDialogMenu) && (it.key == key) }
                menuList[index] = SettingMenu.SettingIntSlideDialogMenu(key, title, dialogTitle, value, valueRange, updateCallback)
                emitMenuList()
            }
        }
    }

    private fun addColorPickerDialogMenu(key: String, title: String, dialogTitle: String, defaultColor: ULong, flow: Flow<ULong>, updateCallback: suspend (ULong) -> Unit) {
        menuList.add(SettingMenu.SettingColorPickerDialogMenu(key, title, dialogTitle, defaultColor, updateCallback))
        viewModelScope.launch {
            flow.collect { value ->
                val index = menuList.indexOfFirst { (it is SettingMenu.SettingColorPickerDialogMenu) && (it.key == key) }
                menuList[index] = SettingMenu.SettingColorPickerDialogMenu(key, title, dialogTitle, value, updateCallback)
                emitMenuList()
            }
        }

    }

    private fun <T> addRadioButtonListDialogMenu(
        key: String,
        title: String,
        defaultValue: String,
        itemToValue: (T) -> String,
        dialogTitle: String,
        dialogContentLoadListener: suspend () -> RadioButtonListDialogContent.Loaded<T>,
        selectedItemListener: suspend (T) -> Unit,
        selectedItemFlow: Flow<T>
    ) {
        menuList.add(SettingMenu.SettingRadioButtonListDialogMenu(key, title, defaultValue, itemToValue, dialogTitle, dialogContentLoadListener, selectedItemListener))
        viewModelScope.launch {
            selectedItemFlow.collect { selectedItem ->
                val index = menuList.indexOfFirst { (it is SettingMenu.SettingRadioButtonListDialogMenu<*>) && (it.key == key) }
                val value = itemToValue(selectedItem)
                menuList[index] = SettingMenu.SettingRadioButtonListDialogMenu(key, title, value, itemToValue, dialogTitle, dialogContentLoadListener, selectedItemListener)
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
        const val SETTING_KEY_CLOCK_TOPIC_LIST = "ClockTopicList"
    }
}


sealed interface SettingMenu {
    data class SettingTitle(val title: String): SettingMenu
    data class SettingOnOffMenu(
        val key: String,
        val title: String,
        val isOn: Boolean,
        val updateCallback: suspend (Boolean) -> Unit
    ): SettingMenu
    data class SettingIntSlideDialogMenu(
        val key: String,
        val title: String,
        val dialogTitle: String,
        val value: Int,
        val range: ClosedRange<Int>,
        val updateCallback: suspend (Int) -> Unit
    ): SettingMenu
    data class SettingColorPickerDialogMenu(
        val key: String,
        val title: String,
        val dialogTitle: String,
        val color: ULong,
        val updateCallback: suspend (ULong) -> Unit
    ): SettingMenu
    data class SettingRadioButtonListDialogMenu<T>(
        val key: String,
        val title: String,
        val value: String,
        val itemToValue: (T) -> String,
        val dialogTitle: String,
        val dialogContentLoadListener: suspend () -> RadioButtonListDialogContent.Loaded<T>,
        val updateCallback: suspend (T) -> Unit
    ): SettingMenu
}

sealed interface RadioButtonListDialogContent<T> {
    class Init<T>: RadioButtonListDialogContent<T>
    class Loaded<T>(
        val content: List<T>,
        val selectedIndex: Int
    ): RadioButtonListDialogContent<T>
}

sealed interface SettingScreenState {
    class Init: SettingScreenState
    class Loaded(val menuList: List<SettingMenu>): SettingScreenState
}