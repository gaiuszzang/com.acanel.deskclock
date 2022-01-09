package com.acanel.deskclock.ui.viewmodel

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.acanel.deskclock.entity.ClockTimeAdjustLocationVO
import com.acanel.deskclock.entity.ClockTimeDisplayOptionVO
import com.acanel.deskclock.usecase.ClockSettingUseCase
import com.acanel.deskclock.usecase.ClockUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import javax.inject.Inject

@HiltViewModel
class ClockViewModel @Inject constructor(
    private val useCase: ClockUseCase,
    private val clockSettingUseCase: ClockSettingUseCase
):  ViewModel() {

    val showSettingUI = mutableStateOf(false)
    val clockTime = useCase.getClockTimeFlow().asState(viewModelScope, null)
    val clockTimeDisplayOption = clockSettingUseCase.getClockTimeDisplayOptionFlow().asState(viewModelScope, ClockTimeDisplayOptionVO(40, 120, 80, 20, Color.White.value))
    val clockTimeAdjustLocation = useCase.getClockTimeAdjustLocationFlow().asState(viewModelScope, ClockTimeAdjustLocationVO(0.5f, 0.5f))
    val backImageUrl = useCase.getBackImageFlow().asState(viewModelScope, null)

    private var showSettingUIJob: Job? = null


    fun toggleSettingUI() {
        showSettingUIJob?.cancel()
        if (!showSettingUI.value) {
            showSettingUI.value = true
            showSettingUIJob = viewModelScope.launch {
                delay(5000)
                showSettingUI.value = false
                showSettingUIJob = null
            }
        } else {
            showSettingUI.value = false
        }
    }
}

//TODO extensions
private fun <T> Flow<T>.asState(coroutineScope: CoroutineScope, defaultValue: T): MutableState<T> {
    val state = mutableStateOf(defaultValue)
    coroutineScope.launch {
        this@asState.collect {
            state.value = it
        }
    }
    return state
}