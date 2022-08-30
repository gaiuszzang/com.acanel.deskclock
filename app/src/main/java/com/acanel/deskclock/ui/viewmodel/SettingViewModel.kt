package com.acanel.deskclock.ui.viewmodel

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.acanel.deskclock.entity.UnsplashTopicVO
import com.acanel.deskclock.repo.StringRepository
import com.acanel.deskclock.repo.StringRepository.Type.*
import com.acanel.deskclock.ui.composable.dialog.IntSlideDialogState
import com.acanel.deskclock.ui.composable.dialog.LazyRadioButtonListDialogContent
import com.acanel.deskclock.ui.composable.dialog.LazyRadioButtonListDialogState
import com.acanel.deskclock.ui.composable.dialog.ColorPickerDialogState
import com.acanel.deskclock.usecase.ClockSettingUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingViewModel @Inject constructor(
    private val clockSettingUseCase: ClockSettingUseCase,
    private val stringRepo: StringRepository
) : ViewModel() {
    var uiStateList = mutableStateListOf<SettingMenu>()
        private set

    init {
        initMenuList()
    }

    private fun initMenuList() {
        addTitleMenu(stringRepo.get(SettingMenuTitleClockTimeDisplay))
        addOnOffMenu(
            key = SETTING_KEY_CLOCK_24HOUR_DISPLAY,
            title = stringRepo.get(SettingMenuTitle24HourFormat),
            flow = clockSettingUseCase.is24HourDisplay()
        ) { clockSettingUseCase.set24HourDisplay(it) }
        addOnOffMenu(
            key = SETTING_KEY_CLOCK_BURN_IN_PREVENT,
            title = stringRepo.get(SettingMenuTitleBurnInPrevention),
            flow = clockSettingUseCase.isBurnInPrevention()
        ) { clockSettingUseCase.setBurnInPrevention(it) }
        addIntSlideDialogMenu(
            key = SETTING_KEY_CLOCK_FONT_SIZE_LEVEL,
            title = stringRepo.get(SettingMenuTitleClockFontSize),
            dialogTitle = stringRepo.get(SettingMenuTitleClockFontSize),
            defaultValue = 2,
            valueRange = clockSettingUseCase.getClockFontSizeLevelRange(),
            flow = clockSettingUseCase.getClockFontSizeLevel()
        ) {
            clockSettingUseCase.setClockFontSizeLevel(it)
        }
        addIntSlideDialogMenu(
            key = SETTING_KEY_CLOCK_FONT_SHADOW_SIZE_LEVEL,
            title = stringRepo.get(SettingMenuTitleClockFontShadowSize),
            dialogTitle = stringRepo.get(SettingMenuTitleClockFontShadowSize),
            defaultValue = 2,
            valueRange = clockSettingUseCase.getClockFontShadowSizeLevelRange(),
            flow = clockSettingUseCase.getClockFontShadowSizeLevel()
        ) {
            clockSettingUseCase.setClockFontShadowSizeLevel(it)
        }
        addColorPickerDialogMenu(
            key = SETTING_KEY_CLOCK_FONT_COLOR,
            title = stringRepo.get(SettingMenuTitleClockFontColor),
            dialogTitle = stringRepo.get(SettingMenuTitleClockFontColor),
            defaultColor = Color.White.value,
            flow = clockSettingUseCase.getClockFontColor()
        ) {
            clockSettingUseCase.setClockFontColor(it)
        }
        addTitleMenu(stringRepo.get(SettingMenuTitleClockBackground))
        addOnOffMenu(
            key = SETTING_KEY_CLOCK_USE_BACKGROUND_IMAGE,
            title = stringRepo.get(SettingMenuTitleUseBackgroundImage),
            flow = clockSettingUseCase.useClockBackgroundImage()
        ) { clockSettingUseCase.setUseClockBackgroundImage(it) }
        addRadioButtonListDialogMenu(
            key = SETTING_KEY_CLOCK_TOPIC_LIST,
            title = stringRepo.get(SettingMenuTitleBackgroundImageTopic),
            defaultValue = "",
            itemToValue = { it.title ?: "" },
            selectedItemFlow = clockSettingUseCase.getClockBackgroundImageTopicFlow(),
            dialogTitle = stringRepo.get(SettingMenuTitleBackgroundImageTopic),
            dialogContentLoadListener = { loadClockBackgroundImageTopicList() },
            selectedItemListener = { clockSettingUseCase.setClockBackgroundImageTopic(it) },
            visibleConditionFlow = clockSettingUseCase.useClockBackgroundImage()
        )
    }

    private fun addTitleMenu(title: String) {
        uiStateList.add(SettingMenu.SettingTitle(title))
    }

    private fun addOnOffMenu(key: String, title: String, flow: Flow<Boolean>, updateCallback: suspend (Boolean) -> Unit) {
        uiStateList.add(SettingMenu.SettingOnOffMenu(key, title, false) { viewModelScope.launch { updateCallback(it) } })
        viewModelScope.launch {
            flow.collect { value ->
                val index = uiStateList.indexOfFirst { (it is SettingMenu.SettingOnOffMenu) && (it.key == key) }
                uiStateList[index] = (uiStateList[index] as SettingMenu.SettingOnOffMenu).copy(isOn = value)
            }
        }
    }

    private fun addIntSlideDialogMenu(key: String, title: String, defaultValue: Int, valueRange: ClosedRange<Int>, dialogTitle: String, flow: Flow<Int>, onUpdate: suspend (Int) -> Unit) {
        uiStateList.add(
            SettingMenu.SettingIntSlideDialogMenu(
                key, title, defaultValue,
                dialogState = IntSlideDialogState(
                    false, dialogTitle, defaultValue, valueRange,
                    onUpdate = { viewModelScope.launch { onUpdate(it) } },
                    onDismiss = {
                        val (index, prevState) = findState<SettingMenu.SettingIntSlideDialogMenu>(key)
                        uiStateList[index] = prevState.copy(dialogState = prevState.dialogState.copy(isShow = false))
                    }
                ),
                onMenuClick = {
                    val (index, prevState) = findState<SettingMenu.SettingIntSlideDialogMenu>(key)
                    uiStateList[index] = prevState.copy(dialogState = prevState.dialogState.copy(isShow = true))
                }))
        viewModelScope.launch {
            flow.collect { value ->
                val (index, prevState) = findState<SettingMenu.SettingIntSlideDialogMenu>(key)
                uiStateList[index] = prevState.copy(value = value, dialogState = prevState.dialogState.copy(value = value))
            }
        }
    }

    private fun addColorPickerDialogMenu(key: String, title: String, dialogTitle: String, defaultColor: ULong, flow: Flow<ULong>, onUpdate: suspend (ULong) -> Unit) {
        uiStateList.add(
            SettingMenu.SettingColorPickerDialogMenu(
                key, title, defaultColor,
                dialogState = ColorPickerDialogState(false, dialogTitle, defaultColor,
                    onUpdate = {
                        viewModelScope.launch { onUpdate(it) }
                    },
                    onDismiss = {
                        val (index, prevState) = findState<SettingMenu.SettingColorPickerDialogMenu>(key)
                        uiStateList[index] = prevState.copy(dialogState = prevState.dialogState.copy(isShow = false))
                    }
                ),
                onMenuClick = {
                    val (index, prevState) = findState<SettingMenu.SettingColorPickerDialogMenu>(key)
                    uiStateList[index] = prevState.copy(dialogState = prevState.dialogState.copy(isShow = true))
                }
            ))
        viewModelScope.launch {
            flow.collect { color ->
                val (index, prevState) = findState<SettingMenu.SettingColorPickerDialogMenu>(key)
                uiStateList[index] = prevState.copy(color = color, dialogState = prevState.dialogState.copy(color = color))
            }
        }
    }

    private inline fun <reified T: SettingMenu> findState(key: String): Pair<Int, T> {
        val index = uiStateList.indexOfFirst { (it is T) && (it.key == key) }
        return Pair(index, uiStateList[index] as T)
    }

    private fun <T> addRadioButtonListDialogMenu(
        key: String,
        title: String,
        defaultValue: String,
        itemToValue: (T) -> String,
        dialogTitle: String,
        dialogContentLoadListener: suspend () -> LazyRadioButtonListDialogContent.Loaded<T>,
        selectedItemListener: suspend (T) -> Unit,
        selectedItemFlow: Flow<T>,
        visibleConditionFlow: Flow<Boolean>? = null
    ) {
        uiStateList.add(
            SettingMenu.SettingRadioButtonListDialogMenu(
                key, title, defaultValue, false,
                dialogState = LazyRadioButtonListDialogState(false, dialogTitle, itemToValue,
                    loadContent = dialogContentLoadListener,
                    onSelected = {
                        viewModelScope.launch {
                            selectedItemListener(it)
                        }
                    },
                    onDismiss = {
                        val (index, prevState) = findState<SettingMenu.SettingRadioButtonListDialogMenu<T>>(key)
                        uiStateList[index] = prevState.copy(dialogState = prevState.dialogState.copy(isShow = false))
                    }
                ),
                onMenuClick = {
                    val (index, prevState) = findState<SettingMenu.SettingRadioButtonListDialogMenu<T>>(key)
                    uiStateList[index] = prevState.copy(dialogState = prevState.dialogState.copy(isShow = true))
                }
            )
        )
        viewModelScope.launch {
            selectedItemFlow.collect { selectedItem ->
                val (index, prevState) = findState<SettingMenu.SettingRadioButtonListDialogMenu<T>>(key)
                uiStateList[index] = prevState.copy(value = itemToValue(selectedItem))
            }
        }
        if (visibleConditionFlow != null) {
            viewModelScope.launch {
                visibleConditionFlow.collect { isVisible ->
                    val index = uiStateList.indexOfFirst { (it is SettingMenu.SettingRadioButtonListDialogMenu<*>) && (it.key == key) }
                    uiStateList[index] = (uiStateList[index] as SettingMenu.SettingRadioButtonListDialogMenu<*>).copy(isVisible = isVisible)
                }
            }
        }
    }

    private suspend fun loadClockBackgroundImageTopicList(): LazyRadioButtonListDialogContent.Loaded<UnsplashTopicVO> {
        val list = clockSettingUseCase.getClockBackgroundImageTopicList()
        val selectedItem = clockSettingUseCase.getClockBackgroundImageTopicFlow().first()
        val selectedIndex = list.indexOfFirst { it.slug == selectedItem.slug }
        return LazyRadioButtonListDialogContent.Loaded(list, selectedIndex)
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
    val key: String
    data class SettingTitle(
        val title: String,
        override val key: String = ""
    ): SettingMenu
    data class SettingOnOffMenu(
        override val key: String,
        val title: String,
        val isOn: Boolean,
        val updateCallback: (Boolean) -> Unit
    ): SettingMenu
    data class SettingIntSlideDialogMenu(
        override val key: String,
        val title: String,
        val value: Int,
        val dialogState: IntSlideDialogState,
        val onMenuClick: () -> Unit
    ): SettingMenu
    data class SettingColorPickerDialogMenu(
        override val key: String,
        val title: String,
        val color: ULong,
        val dialogState: ColorPickerDialogState,
        val onMenuClick: () -> Unit
    ): SettingMenu
    data class SettingRadioButtonListDialogMenu<T>(
        override val key: String,
        val title: String,
        val value: String,
        val isVisible: Boolean,
        val dialogState: LazyRadioButtonListDialogState<T>,
        val onMenuClick: () -> Unit
    ): SettingMenu
}

