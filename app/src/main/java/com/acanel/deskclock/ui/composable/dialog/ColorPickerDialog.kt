package com.acanel.deskclock.ui.composable.dialog

import androidx.compose.foundation.layout.height
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.acanel.deskclock.ui.theme.AppTheme
import com.acanel.groovin.composable.GroovinOkayCancelDialog
import com.godaddy.android.colorpicker.ClassicColorPicker
import com.godaddy.android.colorpicker.HsvColor
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

data class ColorPickerDialogState (
    val isShow: Boolean,
    val title: String,
    val color: ULong,
    val onUpdate: suspend (ULong) -> Unit,
    val onDismiss: () -> Unit
)

@Composable
fun ColorPickerDialog(
    state: ColorPickerDialogState,
    coroutineScope: CoroutineScope = rememberCoroutineScope()
) {
    if (!state.isShow) return
    var pickColor by remember { mutableStateOf(Color(state.color)) }
    GroovinOkayCancelDialog(
        title = state.title,
        cancelable = true,
        onPositiveClick = {
            coroutineScope.launch {
                state.onUpdate(pickColor.value)
                state.onDismiss()
            }
        },
        onCancelClick = state.onDismiss
    ) {
        ClassicColorPicker(
            color = pickColor,
            modifier = Modifier.height(200.dp),
            onColorChanged = { color: HsvColor ->
                pickColor = color.toColor()
            }
        )
    }
}

@Preview
@Composable
fun PreviewColorPickerDialog() {
    AppTheme {
        ColorPickerDialog(
            ColorPickerDialogState(
                isShow = true,
                title = "Color Picker Dialog",
                color = Color.Cyan.value,
                onUpdate = {},
                onDismiss = {}
            )
        )
    }
}