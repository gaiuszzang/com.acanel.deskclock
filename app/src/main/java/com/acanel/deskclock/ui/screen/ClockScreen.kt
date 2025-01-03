package com.acanel.deskclock.ui.screen

import android.view.MotionEvent
import androidx.compose.animation.core.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material.Text
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInteropFilter
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.*
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.ConstraintLayoutScope
import androidx.hilt.navigation.compose.hiltViewModel
import com.acanel.deskclock.R
import com.acanel.deskclock.entity.ClockTimeAdjustLocationVO
import com.acanel.deskclock.entity.ClockTimeDisplayOptionVO
import com.acanel.deskclock.entity.ClockTimeVO
import com.acanel.deskclock.entity.UnsplashImageVO
import com.acanel.deskclock.ui.LocalNavAction
import com.acanel.deskclock.ui.composable.clock.ClockSystemMenu
import com.acanel.deskclock.ui.composable.clock.ClockSystemMenuPosition
import com.acanel.deskclock.ui.composable.clock.ClockTime
import com.acanel.deskclock.ui.composable.image.GroovinUrlCrossFadeImage
import com.acanel.deskclock.ui.composable.window.KeepScreenOn
import com.acanel.deskclock.ui.composable.window.ScreenImmersive
import com.acanel.deskclock.ui.composable.window.ScreenLandscape
import com.acanel.deskclock.ui.theme.DeskClockTheme
import com.acanel.deskclock.ui.theme.White
import com.acanel.deskclock.ui.viewmodel.ClockViewModel

@Composable
fun ClockActivityScreen(clockViewModel: ClockViewModel = hiltViewModel()) {
    ScreenLandscape {
        KeepScreenOn {
            ScreenImmersive {
                DeskClockTheme {
                    ClockScreen(clockViewModel.clockState, clockViewModel.menuState)
                }
            }
        }
    }
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun ClockScreen(
    clockState: ClockViewModel.ClockState,
    clockMenuState: ClockViewModel.ClockMenuState
) {
    val navAction = LocalNavAction.current
    val animatedVerticalBias by animateFloatAsState(
        targetValue = clockState.displayLocation.verticalBias,
        animationSpec = tween(durationMillis = 10000, easing = LinearEasing),
        label = "animatedVerticalBias"
    )
    val animatedHorizontalBias by animateFloatAsState(
        targetValue = clockState.displayLocation.horizontalBias,
        animationSpec = tween(durationMillis = 10000, easing = LinearEasing),
        label = "animatedHorizontalBias"
    )
    ConstraintLayout(
        modifier = Modifier.fillMaxSize()
    ) {
        val (backImage, clockTime) = createRefs()
        ClockBackground(
            backgroundImageUrl = clockState.backImage?.urls?.regular,
            modifier = Modifier
                .constrainAs(backImage) {
                    top.linkTo(parent.top)
                    bottom.linkTo(parent.bottom)
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                }
                .fillMaxSize()
                .onActionDownListener { clockMenuState.onToggleShowMenu?.invoke() }
        )
        ClockTime(
            clockTime = clockState.clockTime,
            clockTimeDisplayOption = clockState.displayOption,
            modifier = Modifier
                .constrainAs(clockTime) {
                    centerVerticallyTo(parent, animatedVerticalBias)
                    centerHorizontallyTo(parent, animatedHorizontalBias)
                }
                .height(IntrinsicSize.Max)
                .width(IntrinsicSize.Max)
        )
        ClockTopMenu(
            constraintLayoutScope = this,
            isShow = clockMenuState.showMenu,
            settingAction = navAction.settingAction,
            finishAction = navAction.finishAction
        )
        if (clockState.backImage?.urls?.regular != null) {
            ClockBottomMenu(
                constraintLayoutScope = this,
                isShow = clockMenuState.showMenu,
                backImageVO = clockState.backImage
            )
        }
    }
}

@Composable
private fun ClockBackground(
    modifier: Modifier = Modifier,
    backgroundImageUrl: String?
) {
    if (backgroundImageUrl != null) {
        GroovinUrlCrossFadeImage(url = backgroundImageUrl, modifier = modifier)
    } else {
        Box(modifier = modifier)
    }
}

@Composable
private fun ClockTopMenu(
    constraintLayoutScope: ConstraintLayoutScope,
    isShow: Boolean = false,
    settingAction: (() -> Unit)? = null,
    finishAction: (() -> Unit)? = null
) {
    ClockSystemMenu(
        constraintLayoutScope = constraintLayoutScope,
        modifier = Modifier.padding(8.dp),
        isShow = isShow,
        position = ClockSystemMenuPosition.TOP
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

@Composable
private fun ClockBottomMenu(
    constraintLayoutScope: ConstraintLayoutScope,
    isShow: Boolean = false,
    backImageVO: UnsplashImageVO
) {
    val photographerName = backImageVO.user?.name
    val photographerUrl = backImageVO.user?.links?.html
    if (photographerName == null || photographerUrl == null) return
    ClockSystemMenu(
        constraintLayoutScope = constraintLayoutScope,
        modifier = Modifier.padding(24.dp, 16.dp, 24.dp, 16.dp),
        isShow = isShow,
        position = ClockSystemMenuPosition.BOTTOM
    ) {
        val (photoInfo) = createRefs()
        val uriHandler = LocalUriHandler.current
        val photoInfoAnnotatedString = buildAnnotatedString {
            withStyle(style = SpanStyle(fontSize = 14.sp, color = MaterialTheme.colorScheme.onPrimary)) {
                append("Photo by ")
                withLink(
                    LinkAnnotation.Clickable(
                        tag = "url",
                        styles = TextLinkStyles(style = SpanStyle(textDecoration = TextDecoration.Underline)),
                        linkInteractionListener = {
                            uriHandler.openUri("$photographerUrl?utm_source=DeskClock&utm_medium=referral")
                        }
                    )
                ) {
                    append(photographerName)
                }
                append(" on ")
                withLink(
                    LinkAnnotation.Clickable(
                        tag = "url",
                        styles = TextLinkStyles(style = SpanStyle(textDecoration = TextDecoration.Underline)),
                        linkInteractionListener = {
                            uriHandler.openUri("https://unsplash.com/?utm_source=DeskClock&utm_medium=referral")
                        }
                    )
                ) {
                    append("Unsplash")
                }
            }
        }
        Text(
            modifier = Modifier
                .constrainAs(photoInfo) {
                    top.linkTo(parent.top)
                    end.linkTo(parent.end)
                },
            text = photoInfoAnnotatedString
        )
    }
}

@ExperimentalComposeUiApi
private fun Modifier.onActionDownListener(listener: (() -> Unit) ?): Modifier {
    return this.pointerInteropFilter {
        if (it.action == MotionEvent.ACTION_DOWN) {
            listener?.invoke()
        }
        true
    }
}

@Preview(widthDp = 640, heightDp = 360)
@Composable
fun PreviewClockScreenAmPm() {
    DeskClockTheme {
        ClockScreen(
            ClockViewModel.ClockState(
                clockTime = ClockTimeVO("10월 14일 목요일", "2:15", "AM"),
                displayOption = ClockTimeDisplayOptionVO(36, 120, 80, 20, Color.White.value),
                displayLocation = ClockTimeAdjustLocationVO(0.5f, 0.5f)
            ),
            ClockViewModel.ClockMenuState(
                showMenu = false
            ) {}
        )
    }
}

@Preview(widthDp = 640, heightDp = 360)
@Composable
fun PreviewClockScreen24Hour() {
    DeskClockTheme {
        ClockScreen(
            ClockViewModel.ClockState(
                clockTime = ClockTimeVO("10월 14일 목요일", "2:15", null),
                displayOption = ClockTimeDisplayOptionVO(36, 120, 80, 20, Color.White.value),
                displayLocation = ClockTimeAdjustLocationVO(0.5f, 0.5f)
            ),
            ClockViewModel.ClockMenuState(
                showMenu = false
            ) {}
        )
    }
}

@Preview(widthDp = 640, heightDp = 360)
@Composable
fun PreviewClockScreenWithMenu() {
    DeskClockTheme {
        ClockScreen(
            ClockViewModel.ClockState(
                clockTime = ClockTimeVO("10월 14일 목요일", "2:15", "AM"),
                displayOption = ClockTimeDisplayOptionVO(36, 120, 80, 20, Color.White.value),
                displayLocation = ClockTimeAdjustLocationVO(0.5f, 0.5f),
            ),
            ClockViewModel.ClockMenuState(
                showMenu = true
            ) {}
        )
    }
}