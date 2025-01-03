package com.acanel.deskclock.ui.composable.setting

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.acanel.deskclock.ui.theme.Black
import com.acanel.deskclock.ui.viewmodel.SettingMenu

@Composable
fun SettingTitle(menu: SettingMenu.SettingTitle) {
    Row(
        modifier = Modifier.padding(20.dp, 30.dp, 20.dp, 10.dp)
    ) {
        Text(
            text = menu.title,
            color = Black,
            fontSize = 16.sp,
            fontWeight = FontWeight.SemiBold
        )
    }
}

@Preview
@Composable
fun SettingTitlePreview() {
    SettingTitle(SettingMenu.SettingTitle(title = "Setting Menu Title"))
}