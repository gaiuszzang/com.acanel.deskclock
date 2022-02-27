package com.acanel.deskclock.ui.screen

import android.view.MotionEvent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.*
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.input.pointer.pointerInteropFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.hilt.navigation.compose.hiltViewModel
import com.acanel.deskclock.R
import com.acanel.deskclock.entity.ClockTimeAdjustLocationVO
import com.acanel.deskclock.entity.ClockTimeDisplayOptionVO
import com.acanel.deskclock.entity.ClockTimeVO
import com.acanel.deskclock.ui.LocalNavAction
import com.acanel.deskclock.ui.theme.DeskClockTheme
import com.acanel.deskclock.ui.theme.MenuBackColor
import com.acanel.deskclock.ui.theme.White
import com.acanel.deskclock.ui.viewmodel.ClockViewModel
import com.acanel.groovin.composable.*

@Composable
fun ClockScreen(clockViewModel: ClockViewModel = hiltViewModel()) {
    val navAction = LocalNavAction.current
    ScreenLandscape {
        KeepScreenOn {
            ScreenImmersive {
                DeskClockTheme {
                    ClockScreenIn(
                        clockViewModel.clockTime.value,
                        clockViewModel.clockTimeDisplayOption.value,
                        clockViewModel.clockTimeAdjustLocation.value,
                        clockViewModel.backImageUrl.value,
                        clockViewModel.showTopMenu.value,
                        { clockViewModel.toggleShowTopMenu() },
                        navAction.settingAction,
                        navAction.finishAction
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun ClockScreenIn(
    clockTime: ClockTimeVO?,
    clockTimeDisplayOption: ClockTimeDisplayOptionVO,
    clockTimeAdjustLocationVO: ClockTimeAdjustLocationVO,
    backImageUrl: String?,
    showTopMenu: Boolean = false,
    screenTouchListener: (() -> Unit)? = null,
    settingAction: (() -> Unit)? = null,
    finishAction: (() -> Unit)? = null
) {
    val animatedVerticalBias by animateFloatAsState(
        targetValue = clockTimeAdjustLocationVO.verticalBias,
        animationSpec = tween(durationMillis = 10000, easing = LinearEasing)
    )
    val animatedHorizontalBias by animateFloatAsState(
        targetValue = clockTimeAdjustLocationVO.horizontalBias,
        animationSpec = tween(durationMillis = 10000, easing = LinearEasing)
    )
    ConstraintLayout(
        modifier = Modifier
            .fillMaxSize()
    ) {
        val (backImage, settingMenu, timeText) = createRefs()
        if (backImageUrl != null) {
            GroovinUrlCrossFadeImage(
                url = backImageUrl,
                modifier = Modifier
                    .constrainAs(backImage) {
                        top.linkTo(parent.top)
                        bottom.linkTo(parent.bottom)
                        start.linkTo(parent.start)
                        end.linkTo(parent.end)
                    }
                    .fillMaxSize()
                    .pointerInteropFilter {
                        if (it.action == MotionEvent.ACTION_DOWN) {
                            screenTouchListener?.invoke()
                        }
                        true
                    }
            )
        } else {
            Box(
                modifier = Modifier
                    .constrainAs(backImage) {
                        top.linkTo(parent.top)
                        bottom.linkTo(parent.bottom)
                        start.linkTo(parent.start)
                        end.linkTo(parent.end)
                    }
                    .fillMaxSize()
                    .pointerInteropFilter {
                        if (it.action == MotionEvent.ACTION_DOWN) {
                            screenTouchListener?.invoke()
                        }
                        true
                    }
            )
        }

        ClockTime(
            clockTime = clockTime,
            clockTimeDisplayOption = clockTimeDisplayOption,
            modifier = Modifier
                .constrainAs(timeText) {
                    centerVerticallyTo(parent, animatedVerticalBias)
                    centerHorizontallyTo(parent, animatedHorizontalBias)
                }
                .height(IntrinsicSize.Max)
                .width(IntrinsicSize.Max)
        )

        ClockTopMenu(
            modifier = Modifier
                .constrainAs(settingMenu) {
                    top.linkTo(parent.top)
                },
            isShow = showTopMenu,
            settingAction = settingAction,
            finishAction = finishAction
        )
    }
}

@Composable
private fun ClockTime(
    clockTime: ClockTimeVO?,
    clockTimeDisplayOption : ClockTimeDisplayOptionVO,
    modifier: Modifier = Modifier
) {
    if (clockTime == null) return
    Column(
        modifier = modifier
    ) {
        val timeString = buildAnnotatedString {
            withStyle(SpanStyle(fontSize = clockTimeDisplayOption.timeFontSize.sp, color = Color(clockTimeDisplayOption.fontColor))) {
                append(clockTime.time)
            }
            if (clockTime.ampm != null) {
                withStyle(SpanStyle(fontSize = clockTimeDisplayOption.ampmFontSize.sp, color = Color(clockTimeDisplayOption.fontColor))) {
                    append(clockTime.ampm)
                }
            }
        }
        Text(
            text = timeString,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .fillMaxWidth()
                .padding(25.dp, 5.dp, 25.dp, 0.dp),
            style = TextStyle(
                color = Color.White,
                shadow = Shadow(
                    color = Color.Black,
                    offset = Offset(0f, 0f),
                    blurRadius = clockTimeDisplayOption.fontShadowSize.toFloat()
                )
            )
        )
        Text(
            text = clockTime.date,
            fontSize = clockTimeDisplayOption.dateFontSize.sp,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .fillMaxWidth()
                .padding(25.dp, 0.dp, 25.dp, 30.dp),
            style = TextStyle(
                color = Color(clockTimeDisplayOption.fontColor),
                shadow = Shadow(
                    color = Color.Black,
                    offset = Offset(0f, 0f),
                    blurRadius = clockTimeDisplayOption.fontShadowSize.toFloat()
                )
            )
        )
    }
}

@Composable
private fun ClockTopMenu(
    modifier: Modifier = Modifier,
    isShow: Boolean = false,
    settingAction: (() -> Unit)? = null,
    finishAction: (() -> Unit)? = null
) {
    AnimatedVisibility(
        visible = isShow,
        enter = slideInVertically(
            initialOffsetY = { fullHeight -> -fullHeight },
            animationSpec = tween(durationMillis = 200, easing = FastOutLinearInEasing)
        ),
        exit = slideOutVertically(
            targetOffsetY = { fullHeight -> -fullHeight },
            animationSpec = tween(durationMillis = 200, easing = FastOutLinearInEasing)
        )
    ) {
        ConstraintLayout(
            modifier = Modifier
                .fillMaxWidth()
                .background(color = MenuBackColor, shape = RoundedCornerShape(0.dp, 0.dp, 24.dp, 24.dp))
                .padding(8.dp)
                .then(modifier)
        ) {
            val (settingButton, exitButton) = createRefs()
            IconButton(
                modifier = Modifier
                    .constrainAs(settingButton) {
                        top.linkTo(parent.top)
                        start.linkTo(parent.start)
                    },
                onClick = {
                    settingAction?.invoke()
                }) {
                Icon(painterResource(id = R.drawable.ic_settings), "Settings", tint = White, modifier = Modifier.padding(6.dp))
            }

            IconButton(
                modifier = Modifier
                    .constrainAs(exitButton) {
                        top.linkTo(parent.top)
                        end.linkTo(parent.end)
                    },
                onClick = { finishAction?.invoke() }) {
                Icon(painterResource(id = R.drawable.ic_close), "Close", tint = White, modifier = Modifier.padding(6.dp))
            }
        }
    }
}

@Preview(widthDp = 640, heightDp = 360)
@Composable
fun PreviewClockScreen() {
    DeskClockTheme {
        ClockScreenIn(
            ClockTimeVO("10월 14일 목요일", "2:15", "AM"),
            ClockTimeDisplayOptionVO(36, 120, 80, 20, Color.White.value),
            ClockTimeAdjustLocationVO(0.5f, 0.5f),
            null
        )
    }
}

@Preview(widthDp = 640, heightDp = 360)
@Composable
fun PreviewClockScreen2() {
    DeskClockTheme {
        ClockScreenIn(
            ClockTimeVO("10월 14일 목요일", "2:15", null),
            ClockTimeDisplayOptionVO(36, 120, 80, 20, Color.White.value),
            ClockTimeAdjustLocationVO(0.5f, 0.5f),
            null
        )
    }
}

@Preview(widthDp = 640, heightDp = 360)
@Composable
fun PreviewClockScreen3() {
    DeskClockTheme {
        ClockScreenIn(
            ClockTimeVO("10월 14일 목요일", "2:15", "AM"),
            ClockTimeDisplayOptionVO(36, 120, 80, 20, Color.White.value),
            ClockTimeAdjustLocationVO(0.5f, 0.5f),
            null,
            showTopMenu = true
        )
    }
}