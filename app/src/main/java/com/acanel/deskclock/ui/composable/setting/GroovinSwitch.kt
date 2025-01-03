package com.acanel.deskclock.ui.composable.setting

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.acanel.deskclock.ui.theme.AppTheme

//TODO : material3 Switch is not support to swipe thumbIcon.
@Composable
fun GroovinSwitch(checked: Boolean,
                  onCheckedChange: ((Boolean) -> Unit)?,
                  modifier: Modifier = Modifier,
                  enabled: Boolean = true) {
    Switch(
        checked = checked,
        modifier = modifier,
        onCheckedChange = onCheckedChange,
        enabled = enabled,
        colors = SwitchDefaults.colors()
    )
}

@SuppressLint("UnrememberedMutableState")
@Preview
@Composable
fun PreviewGroovinSwitch() {
    AppTheme {
        Column(
            Modifier.fillMaxWidth()
        ) {
            GroovinSwitch(checked = true, {})
            GroovinSwitch(checked = false, {})

            var checked by remember { mutableStateOf(false) }
            GroovinSwitch(
                checked = checked,
                onCheckedChange = { checked = it }
            )
        }
    }
}