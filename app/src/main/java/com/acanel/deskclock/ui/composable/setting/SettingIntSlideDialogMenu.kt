package com.acanel.deskclock.ui.composable.setting

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import com.acanel.deskclock.ui.composable.dialog.IntSlideDialog
import com.acanel.deskclock.ui.theme.Black
import com.acanel.deskclock.ui.viewmodel.SettingMenu
import com.acanel.groovin.composable.GroovinBasicMenuCard


@Composable
fun SettingIntSlideDialogMenu(menu: SettingMenu.SettingIntSlideDialogMenu) {
    //Card
    GroovinBasicMenuCard(
        onClick = menu.onMenuClick
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
    IntSlideDialog(menu.dialogState)
}
