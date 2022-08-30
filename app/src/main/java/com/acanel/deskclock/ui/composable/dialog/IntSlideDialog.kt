package com.acanel.deskclock.ui.composable.dialog

import androidx.compose.material.Slider
import androidx.compose.runtime.*
import com.acanel.groovin.composable.GroovinOkayCancelDialog
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

data class IntSlideDialogState(
    val isShow: Boolean,
    val title: String,
    val value: Int,
    val range: ClosedRange<Int>,
    val onUpdate: (Int) -> Unit,
    val onDismiss: () -> Unit
)

@Composable
fun IntSlideDialog(
    dialogState: IntSlideDialogState
) {
    if (!dialogState.isShow) return
    var sliderPosition by remember { mutableStateOf(dialogState.value.toFloat()) }
    GroovinOkayCancelDialog(
        title = dialogState.title,
        showCancelButton = false,
        cancelable = false,
        onPositiveClick = dialogState.onDismiss
    ) {
        Slider(
            value = sliderPosition,
            steps = dialogState.range.endInclusive - dialogState.range.start - 1,
            valueRange = dialogState.range.start.toFloat()..dialogState.range.endInclusive.toFloat(),
            onValueChange = {
                sliderPosition = it
            },
            onValueChangeFinished = {
                dialogState.onUpdate(sliderPosition.toInt())
            }
        )
    }
}