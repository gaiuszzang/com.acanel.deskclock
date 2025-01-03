package com.acanel.deskclock.ui.composable.clock

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import com.acanel.deskclock.entity.ClockTimeDisplayOptionVO
import com.acanel.deskclock.entity.ClockTimeVO
import com.acanel.deskclock.ui.theme.DeskClockTheme


@Composable
fun ClockTime(
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

@Preview(widthDp = 640, heightDp = 360)
@Composable
fun PreviewClockTime() {
    DeskClockTheme {
        ConstraintLayout(
            modifier = Modifier.fillMaxSize().background(Color.Cyan)
        ) {
            val clockTime = createRef()
            ClockTime(
                clockTime = ClockTimeVO("10월 14일 목요일", "2:15", null),
                clockTimeDisplayOption = ClockTimeDisplayOptionVO(36, 120, 80, 20, Color.White.value),
                modifier = Modifier
                    .constrainAs(clockTime) {
                        centerVerticallyTo(parent, 0.5f)
                        centerHorizontallyTo(parent, 0.5f)
                    }
                    .height(IntrinsicSize.Max)
                    .width(IntrinsicSize.Max)
            )
        }
    }
}