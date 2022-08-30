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
import com.acanel.deskclock.ui.theme.Black
import com.acanel.deskclock.ui.viewmodel.SettingMenu
import com.acanel.groovin.composable.GroovinBasicMenuCard
import com.acanel.groovin.composable.GroovinSwitch


@Composable
fun SettingOnOffMenu(menu: SettingMenu.SettingOnOffMenu) {
    GroovinBasicMenuCard(
        onClick = {
            menu.updateCallback(!menu.isOn)
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
                    menu.updateCallback(!menu.isOn)
                }
            )
        }
    }
}