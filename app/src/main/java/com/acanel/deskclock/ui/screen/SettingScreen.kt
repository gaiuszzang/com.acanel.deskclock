package com.acanel.deskclock.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.hilt.navigation.compose.hiltViewModel
import com.acanel.deskclock.ui.LocalNavAction
import com.acanel.deskclock.ui.theme.AppTheme
import com.acanel.deskclock.ui.theme.Black
import com.acanel.deskclock.ui.theme.White
import com.acanel.deskclock.ui.viewmodel.SettingMenu
import com.acanel.deskclock.ui.viewmodel.SettingScreenState
import com.acanel.deskclock.ui.viewmodel.SettingViewModel
import com.acanel.groovin.composable.GroovinBasicMenuCard
import com.acanel.groovin.composable.GroovinOkayCancelDialog
import com.acanel.groovin.composable.GroovinSwitch
import com.acanel.groovin.composable.ScreenPortrait
import com.godaddy.android.colorpicker.ClassicColorPicker
import com.godaddy.android.colorpicker.HsvColor

@Composable
fun SettingScreen(
    viewModel: SettingViewModel = hiltViewModel()
) {
    val uiState = viewModel.uiState.value
    val menuClickAction: (SettingMenu) -> Unit = { viewModel.onClickMenu(it) }
    val intValueChangeAction: (SettingMenu, Int) -> Unit = { menu, value -> viewModel.onIntSlideValueChanged(menu, value) }
    val onColorPickerChanged: (SettingMenu, ULong) -> Unit = { menu, color -> viewModel.onColorPickerChanged(menu, color) }

    AppTheme {
        ScreenPortrait {
            SettingScreenIn(uiState, menuClickAction, intValueChangeAction, onColorPickerChanged)
        }
    }
}

@Composable
fun SettingScreenIn(
    uiState: SettingScreenState,
    menuClickAction: (SettingMenu) -> Unit,
    intValueChangeAction: (SettingMenu, Int) -> Unit,
    onColorPickerChanged: (SettingMenu, ULong) -> Unit
) {
    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        SettingAppBar()
        if (uiState is SettingScreenState.Loaded) {
            LazyColumn(
                modifier = Modifier.fillMaxSize()
            ) {
                //Contents
                this.items(uiState.menuList) { menu ->
                    when (menu) {
                        is SettingMenu.SettingTitle -> SettingTitle(menu)
                        is SettingMenu.SettingOnOffMenu -> SettingOnOffMenu(menu, menuClickAction)
                        is SettingMenu.SettingIntSlideDialogMenu -> SettingIntSlideDialogMenu(menu, intValueChangeAction)
                        is SettingMenu.SettingColorPickerDialogMenu -> SettingColorPickerDialogMenu(menu, onColorPickerChanged)
                    }
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

@Composable
fun SettingTitle(menu: SettingMenu.SettingTitle) {
    Row(
        modifier = Modifier.padding(20.dp)
    ) {
        Text(
            text = menu.title,
            color = White,
            fontSize = 16.sp,
            fontWeight = FontWeight.SemiBold
        )
    }
}

@Composable
fun SettingOnOffMenu(menu: SettingMenu.SettingOnOffMenu, clickAction: (SettingMenu) -> Unit) {
    GroovinBasicMenuCard(
        onClick = {
            clickAction(menu)
        }
    ) {
        ConstraintLayout(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            val (title, switch) = createRefs()
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
            GroovinSwitch(
                checked = menu.isOn,
                modifier = Modifier.constrainAs(switch) {
                    top.linkTo(parent.top)
                    bottom.linkTo(parent.bottom)
                    end.linkTo(parent.end)
                },
                onCheckedChange = {
                    clickAction(menu)
                }
            )
        }
    }
}

@Composable
fun SettingIntSlideDialogMenu(
    menu: SettingMenu.SettingIntSlideDialogMenu,
    intValueChangeAction: (SettingMenu, Int) -> Unit
) {
    var isDialogShow by remember { mutableStateOf(false) }

    //Card
    GroovinBasicMenuCard(
        onClick = { isDialogShow = true }
    ) {
        ConstraintLayout(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            val (title, value) = createRefs()
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
            Text(
                text = menu.value.toString(),
                color = Black,
                fontSize = 16.sp,
                fontWeight = FontWeight.Normal,
                modifier = Modifier.constrainAs(value) {
                    top.linkTo(parent.top)
                    bottom.linkTo(parent.bottom)
                    end.linkTo(parent.end)
                }
            )
        }
    }
    if (isDialogShow) {
        SettingIntSlideDialog(menu, intValueChangeAction, onDismiss = { isDialogShow = false })
    }
}

@Composable
fun SettingColorPickerDialogMenu(
    menu: SettingMenu.SettingColorPickerDialogMenu,
    onColorChanged: (SettingMenu, ULong) -> Unit
) {
    var isDialogShow by remember { mutableStateOf(false) }

    //Card
    GroovinBasicMenuCard(
        onClick = { isDialogShow = true }
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
    if (isDialogShow) {
        SettingColorPickerDialog(
            menu = menu,
            onColorChanged = { settingMenu, color ->
                onColorChanged(settingMenu, color)
                isDialogShow = false
            },
            onDismiss = { isDialogShow = false }
        )
    }
}


@Composable
fun SettingIntSlideDialog(
    menu: SettingMenu.SettingIntSlideDialogMenu,
    intValueChangeAction: (SettingMenu, Int) -> Unit,
    onDismiss : () -> Unit
) {
    var sliderPosition by remember { mutableStateOf(menu.value.toFloat()) }
    GroovinOkayCancelDialog(
        title = menu.dialogTitle,
        showCancelButton = false,
        cancelable = false,
        onPositiveClick = onDismiss
    ) {
        Slider(
            value = sliderPosition,
            steps = menu.range.endInclusive - menu.range.start - 1,
            valueRange = menu.range.start.toFloat()..menu.range.endInclusive.toFloat(),
            onValueChange = {
                sliderPosition = it
            },
            onValueChangeFinished = {
                intValueChangeAction(menu, sliderPosition.toInt())
            }
        )
    }
}

@Composable
fun SettingColorPickerDialog(
    menu: SettingMenu.SettingColorPickerDialogMenu,
    onColorChanged: (SettingMenu, ULong) -> Unit,
    onDismiss: () -> Unit
) {
    var pickColor by remember { mutableStateOf(Color(menu.color)) }
    GroovinOkayCancelDialog(
        title = menu.dialogTitle,
        cancelable = true,
        onPositiveClick = { onColorChanged(menu, pickColor.value) },
        onCancelClick = onDismiss
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
fun PreviewSettingOnOffMenu() {
    AppTheme {
        Column(
            Modifier.fillMaxWidth()
        ) {
            SettingOnOffMenu(SettingMenu.SettingOnOffMenu("TestKey", "Setting On Menu", true)) {}
            SettingOnOffMenu(SettingMenu.SettingOnOffMenu("TestKey", "Setting Off Menu", false)) {}
        }
    }
}

@Preview
@Composable
fun SettingColorPickerDialogMenu() {
    AppTheme {
        Column(
            Modifier.fillMaxWidth()
        ) {
            SettingColorPickerDialogMenu(SettingMenu.SettingColorPickerDialogMenu("TestKey", "Color Menu", "Pick Color", Color.Cyan.value)) { _, _ -> }
            SettingColorPickerDialogMenu(SettingMenu.SettingColorPickerDialogMenu("TestKey", "Color Menu", "Pick Color", Color.Red.value)) { _, _ -> }
        }

    }
}

@Preview
@Composable
fun PreviewSettingOnOffMenuDarkTheme() {
    AppTheme(darkTheme = true) {
        Column(
            Modifier.fillMaxWidth()
        ) {
            SettingOnOffMenu(SettingMenu.SettingOnOffMenu("TestKey", "Setting On Menu", true)) {}
            SettingOnOffMenu(SettingMenu.SettingOnOffMenu("TestKey", "Setting Off Menu", false)) {}
        }
    }
}