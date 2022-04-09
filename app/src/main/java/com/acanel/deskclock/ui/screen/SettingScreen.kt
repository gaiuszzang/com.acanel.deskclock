package com.acanel.deskclock.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.acanel.deskclock.ui.LocalNavAction
import com.acanel.deskclock.ui.composable.dialog.ColorPickerDialogState
import com.acanel.deskclock.ui.composable.dialog.IntSlideDialogState
import com.acanel.deskclock.ui.composable.dialog.LazyRadioButtonListDialogContent
import com.acanel.deskclock.ui.composable.dialog.LazyRadioButtonListDialogState
import com.acanel.deskclock.ui.composable.setting.*
import com.acanel.deskclock.ui.theme.AppTheme
import com.acanel.deskclock.ui.viewmodel.SettingMenu
import com.acanel.deskclock.ui.viewmodel.SettingViewModel
import com.acanel.groovin.composable.ScreenPortrait

@Composable
fun SettingActivityScreen(
    viewModel: SettingViewModel = hiltViewModel()
) {
    AppTheme {
        ScreenPortrait {
            SettingScreen(viewModel.uiStateList)
        }
    }
}

@Composable
fun SettingScreen(
    menuList: List<SettingMenu>
) {
    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        SettingAppBar()
        LazyColumn(
            modifier = Modifier.fillMaxSize()
        ) {
            //Contents
            this.items(menuList) { menu ->
                when (menu) {
                    is SettingMenu.SettingTitle -> SettingTitle(menu)
                    is SettingMenu.SettingOnOffMenu -> SettingOnOffMenu(menu)
                    is SettingMenu.SettingIntSlideDialogMenu -> SettingIntSlideDialogMenu(menu)
                    is SettingMenu.SettingColorPickerDialogMenu -> SettingColorPickerDialogMenu(menu)
                    is SettingMenu.SettingRadioButtonListDialogMenu<*> -> SettingRadioButtonListDialogMenu(menu)
                }
            }
        }
    }
}

@Composable
fun SettingAppBar() {
    val navAction = LocalNavAction.current
    TopAppBar(
        elevation = 0.dp,
        modifier = Modifier.height(80.dp)
    ) {
        IconButton(
            modifier = Modifier.align(Alignment.CenterVertically),
            onClick = {
                navAction.backAction()
            }
        ) {
            Icon(
                imageVector = Icons.Filled.ArrowBack,
                contentDescription = null
            )
        }
        Text(
            modifier = Modifier.weight(1f),
            text = "Settings"
        )
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewSetting() {
    val dummyKey = ""
    AppTheme {
        SettingScreen(
            listOf(
                SettingMenu.SettingTitle("Setting Title 1"),
                SettingMenu.SettingOnOffMenu(dummyKey, "Switch Menu 1", true) {},
                SettingMenu.SettingOnOffMenu(dummyKey, "Switch Menu 2", false) {},
                SettingMenu.SettingIntSlideDialogMenu(dummyKey, "Setting Slide Menu", 3, IntSlideDialogState(false, "", 3, (1..5), {}, {})) {},
                SettingMenu.SettingTitle("Setting Title 2"),
                SettingMenu.SettingColorPickerDialogMenu(dummyKey, "Color Picker Menu 1", Color.Cyan.value, ColorPickerDialogState(false, "", Color.Cyan.value, {}, {})) {},
                SettingMenu.SettingColorPickerDialogMenu(dummyKey, "Color Picker Menu 2", Color.Red.value, ColorPickerDialogState(false, "", Color.Red.value, {}, {})) {},
                SettingMenu.SettingRadioButtonListDialogMenu<String>(dummyKey, "RadioButton List Dialog", "Value 1", true, LazyRadioButtonListDialogState(false, "", {it}, { LazyRadioButtonListDialogContent.Loaded(listOf(), 0)}, {}, {} )) {}
            )
        )
    }
}


@Preview(showBackground = true)
@Composable
fun PreviewSettingDarkTheme() {
    val dummyKey = ""
    AppTheme(darkTheme = true) {
        SettingScreen(
            listOf(
                SettingMenu.SettingTitle("Setting Title 1"),
                SettingMenu.SettingOnOffMenu(dummyKey, "Switch Menu 1", true) {},
                SettingMenu.SettingOnOffMenu(dummyKey, "Switch Menu 2", false) {},
                SettingMenu.SettingIntSlideDialogMenu(dummyKey, "Setting Slide Menu", 3, IntSlideDialogState(false, "", 3, (1..5), {}, {})) {},
                SettingMenu.SettingTitle("Setting Title 2"),
                SettingMenu.SettingColorPickerDialogMenu(dummyKey, "Color Picker Menu 1", Color.Cyan.value, ColorPickerDialogState(false, "", Color.Cyan.value, {}, {})) {},
                SettingMenu.SettingColorPickerDialogMenu(dummyKey, "Color Picker Menu 2", Color.Red.value, ColorPickerDialogState(false, "", Color.Red.value, {}, {})) {},
                SettingMenu.SettingRadioButtonListDialogMenu<String>(dummyKey, "RadioButton List Dialog", "Value 1", true, LazyRadioButtonListDialogState(false, "", {it}, { LazyRadioButtonListDialogContent.Loaded(listOf(), 0)}, {}, {} )) {}
            )
        )
    }
}
