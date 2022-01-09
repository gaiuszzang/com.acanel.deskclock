package com.acanel.deskclock.ui.screen

import android.view.MotionEvent
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.input.pointer.pointerInteropFilter
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
import com.acanel.deskclock.entity.ClockTimeAdjustLocationVO
import com.acanel.deskclock.entity.ClockTimeDisplayOptionVO
import com.acanel.deskclock.entity.ClockTimeVO
import com.acanel.deskclock.ui.LocalNavAction
import com.acanel.deskclock.ui.theme.DeskClockTheme
import com.acanel.deskclock.ui.viewmodel.ClockViewModel
import com.acanel.groovin.composable.KeepScreenOn
import com.acanel.groovin.composable.ScreenImmersive
import com.acanel.groovin.composable.ScreenLandscape
import com.acanel.groovin.composable.GroovinUrlCrossFadeImage

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
                        clockViewModel.showSettingUI.value,
                        { clockViewModel.toggleSettingUI() },
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
    showSettingUI: Boolean = false,
    screenTouchListener: (() -> Unit)? = null,
    settingButtonClickListener: (() -> Unit)? = null,
    closeButtonClickListener: (() -> Unit)? = null
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
        val (backImage, settingButton, exitButton, timeText) = createRefs()
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

        if (clockTime != null) {
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
        }

        if (showSettingUI) {
            Button(
                modifier = Modifier
                    .constrainAs(settingButton) {
                        top.linkTo(parent.top)
                        start.linkTo(parent.start)
                    },
                onClick = {
                    settingButtonClickListener?.invoke()
                }
            ) {
                Text(text = "Setting")
            }
            Button(
                modifier = Modifier
                    .constrainAs(exitButton) {
                        top.linkTo(parent.top)
                        end.linkTo(parent.end)
                    },
                onClick = { closeButtonClickListener?.invoke() }
            ) {
                Text(text = "X")
            }
        }
    }
}

@Composable
private fun ClockTime(
    clockTime: ClockTimeVO,
    clockTimeDisplayOption : ClockTimeDisplayOptionVO,
    modifier: Modifier = Modifier
) {
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