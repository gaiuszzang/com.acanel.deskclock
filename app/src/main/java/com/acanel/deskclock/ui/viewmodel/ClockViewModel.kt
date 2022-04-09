package com.acanel.deskclock.ui.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.acanel.deskclock.entity.ClockTimeAdjustLocationVO
import com.acanel.deskclock.entity.ClockTimeDisplayOptionVO
import com.acanel.deskclock.entity.ClockTimeVO
import com.acanel.deskclock.entity.UnsplashImageVO
import com.acanel.deskclock.usecase.ClockSettingUseCase
import com.acanel.deskclock.usecase.ClockUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import javax.inject.Inject

@HiltViewModel
class ClockViewModel @Inject constructor(
    clockUseCase: ClockUseCase,
    clockSettingUseCase: ClockSettingUseCase
):  ViewModel() {

    var clockState by mutableStateOf(ClockState(
    ))
        private set

    var menuState by mutableStateOf(ClockMenuState(
        showMenu = false,
        onToggleShowMenu = { toggleShowTopMenu() }
    ))
        private set

    private var showTopMenuJob: Job? = null

    init {
        clockUseCase.getClockTimeFlow().flowToClockState { clockState.copy(clockTime = it) }
        clockUseCase.getClockTimeAdjustLocationFlow().flowToClockState { clockState.copy(displayLocation = it) }
        clockUseCase.getBackImageFlow().flowToClockState { clockState.copy(backImage = it) }
        clockSettingUseCase.getClockTimeDisplayOptionFlow().flowToClockState { clockState.copy(displayOption = it) }
    }

    private fun toggleShowTopMenu() {
        showTopMenuJob?.cancel()
        if (!menuState.showMenu) {
            menuState = menuState.copy(showMenu = true)
            showTopMenuJob = viewModelScope.launch {
                delay(5000)
                menuState = menuState.copy(showMenu = false)
                showTopMenuJob = null
            }
        } else {
            menuState = menuState.copy(showMenu = false)
        }
    }

    private inline fun <reified T> Flow<T>.flowToClockState(crossinline toState: ((T) -> ClockState)) {
        viewModelScope.launch {
            this@flowToClockState.collect {
                clockState = toState(it)
            }
        }
    }

    data class ClockState(
        val clockTime: ClockTimeVO? = null,
        val displayOption: ClockTimeDisplayOptionVO = ClockTimeDisplayOptionVO(40, 120, 80, 20, Color.White.value),
        val displayLocation: ClockTimeAdjustLocationVO = ClockTimeAdjustLocationVO(0.5f, 0.5f),
        val backImage: UnsplashImageVO? = null
    )

    data class ClockMenuState(
        val showMenu: Boolean = false,
        val onToggleShowMenu: (() -> Unit)? = null
    )
}