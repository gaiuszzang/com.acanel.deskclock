package com.acanel.deskclock.ui.composable.setting

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import com.acanel.deskclock.ui.composable.dialog.ColorPickerDialog
import com.acanel.deskclock.ui.composable.dialog.ColorPickerDialogState
import com.acanel.deskclock.ui.theme.AppTheme
import com.acanel.deskclock.ui.theme.Black
import com.acanel.deskclock.ui.viewmodel.SettingMenu
import com.acanel.groovin.composable.GroovinBasicMenuCard


@Composable
fun SettingColorPickerDialogMenu(
    menu: SettingMenu.SettingColorPickerDialogMenu
) {
    val scope = rememberCoroutineScope()

    //Card
    GroovinBasicMenuCard(
        onClick = { menu.onMenuClick() }
    ) {
        ConstraintLayout(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            val (title, colorValue) = createRefs()
            Text(
                text = menu.title,
                color = Black,
                fontSize = 16.sp,
                fontWeight = FontWeight.Normal,
                modifier = Modifier.constrainAs(title) {
                    top.linkTo(parent.top)
                    bottom.linkTo(parent.bottom)
                    start.linkTo(parent.start)
                }
            )
            Box(
                modifier = Modifier
                    .constrainAs(colorValue) {
                        top.linkTo(parent.top)
                        bottom.linkTo(parent.bottom)
                        end.linkTo(parent.end)
                    }
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(Color(menu.color))
                    .border(0.5.dp, Color.Gray, CircleShape)
            )
        }
    }
    ColorPickerDialog(state = menu.dialogState, coroutineScope = scope)
}

@Preview
@Composable
fun PreviewSettingColorPickerDialogMenu() {
    val dummyKey = "Test Key"
    val dummyColorPickerDialogState = ColorPickerDialogState(false, "Color Picker Dialog", Color.Black.value, {}, {})

    AppTheme {
        Column(
            Modifier.fillMaxWidth()
        ) {
            SettingColorPickerDialogMenu(
                SettingMenu.SettingColorPickerDialogMenu(dummyKey, "Color Menu", Color.Cyan.value, dummyColorPickerDialogState) {}
            )
            SettingColorPickerDialogMenu(
                SettingMenu.SettingColorPickerDialogMenu(dummyKey, "Color Menu", Color.Red.value, dummyColorPickerDialogState) {}
            )
        }
    }
}