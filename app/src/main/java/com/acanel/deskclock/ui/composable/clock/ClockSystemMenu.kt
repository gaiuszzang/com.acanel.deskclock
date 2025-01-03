package com.acanel.deskclock.ui.composable.clock

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutLinearInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.ConstraintLayoutScope
import com.acanel.deskclock.ui.theme.DeskClockTheme
import com.acanel.deskclock.ui.theme.MenuBackColor

enum class ClockSystemMenuPosition {
    TOP, BOTTOM
}

@Composable
fun ClockSystemMenu(
    constraintLayoutScope: ConstraintLayoutScope,
    modifier: Modifier = Modifier,
    isShow: Boolean = false,
    position: ClockSystemMenuPosition,
    content: @Composable ConstraintLayoutScope.(constraintLayoutScope: ConstraintLayoutScope) -> Unit
) {
    constraintLayoutScope.apply {
        val constrainedRef = createRef()
        val shape = remember {
            if (position == ClockSystemMenuPosition.TOP)
                RoundedCornerShape(0.dp, 0.dp, 24.dp, 24.dp)
            else
                RoundedCornerShape(24.dp, 24.dp, 0.dp, 0.dp)
        }
        AnimatedVisibility(
            modifier = Modifier
                .fillMaxWidth()
                .constrainAs(constrainedRef) {
                    if (position == ClockSystemMenuPosition.TOP) top.linkTo(parent.top) else bottom.linkTo(parent.bottom)
                },
            visible = isShow,
            enter = slideInVertically(
                initialOffsetY = { fullHeight -> if (position == ClockSystemMenuPosition.TOP) -fullHeight else +fullHeight },
                animationSpec = tween(durationMillis = 200, easing = FastOutLinearInEasing)
            ),
            exit = slideOutVertically(
                targetOffsetY = { fullHeight -> if (position == ClockSystemMenuPosition.TOP) -fullHeight else +fullHeight },
                animationSpec = tween(durationMillis = 200, easing = FastOutLinearInEasing)
            )
        ) {
            ConstraintLayout(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(color = MenuBackColor, shape = shape)
                    .then(modifier)
            ) {
                content(this)
            }
        }
    }
}

@Preview(widthDp = 640, heightDp = 360)
@Composable
fun PreviewClockSystemMenu() {
    DeskClockTheme {
        ConstraintLayout(
            modifier = Modifier.fillMaxSize().background(Color.Yellow)
        ) {
            ClockSystemMenu(
                modifier = Modifier.padding(8.dp),
                constraintLayoutScope = this,
                isShow = true,
                position = ClockSystemMenuPosition.TOP
            ) {
                Text(text = "Top")
            }
            ClockSystemMenu(
                modifier = Modifier.padding(8.dp),
                constraintLayoutScope = this,
                isShow = true,
                position = ClockSystemMenuPosition.BOTTOM
            ) {
                Text(text = "Bottom")
            }
        }

    }

}