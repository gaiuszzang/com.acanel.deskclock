package com.acanel.deskclock.ui.composable.dialog

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.constraintlayout.compose.ConstraintLayout
import com.acanel.deskclock.ui.theme.AppTheme

@Composable
fun GroovinDialogSurface(content: @Composable () -> Unit) {
    Surface(
        shape = RoundedCornerShape(16.dp),
        shadowElevation = 0.dp,
        color = MaterialTheme.colorScheme.onSurface,
        contentColor = MaterialTheme.colorScheme.surface,
        modifier = Modifier.fillMaxWidth().padding(6.dp)
    ) {
        content()
    }
}

@Composable
fun GroovinDialog(
    cancelable: Boolean = true,
    onDismiss: () -> Unit = {},
    content: @Composable () -> Unit
) {
    Dialog(onDismissRequest = { if (cancelable) onDismiss() }) {
        AppTheme {
            GroovinDialogSurface {
                content()
            }
        }
    }
}

@Composable
fun GroovinOkayCancelDialog(
    showOkayButton: Boolean = true,
    showCancelButton: Boolean = true,
    cancelable: Boolean = true,
    okayButtonText: String? = null,
    cancelButtonText: String? = null,
    onCancelClick: () -> Unit = {},
    onPositiveClick: () -> Unit = {},
    title: String? = null,
    content: @Composable () -> Unit
) {
    GroovinDialog(
        cancelable = cancelable,
        onDismiss = onCancelClick
    ) {
        Column(modifier = Modifier.padding(16.dp, 10.dp, 16.dp, 10.dp)) {
            if (title != null) {
                Row(
                    modifier = Modifier.padding(0.dp, 6.dp, 0.dp, 6.dp)
                ) {
                    Text(
                        text = title,
                        color = Color.Black,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
            Row(
                modifier = Modifier.padding(0.dp, 6.dp, 0.dp, 6.dp)
            ) {
                content()
            }
            Row {
                ConstraintLayout(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    val (cancelButton, okayButton) = createRefs()
                    if (showCancelButton) {
                        TextButton(
                            onClick = onCancelClick,
                            modifier = Modifier
                                .constrainAs(cancelButton) {
                                    top.linkTo(parent.top)
                                    bottom.linkTo(parent.bottom)
                                    start.linkTo(parent.start)
                                    end.linkTo(if (showOkayButton) okayButton.start else parent.end)
                                }
                                .fillMaxWidth(if (showOkayButton) 0.5f else 1f)
                        ) {
                            Text(
                                text = cancelButtonText ?: "CANCEL",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                    if (showOkayButton) {
                        TextButton(
                            onClick = onPositiveClick,
                            modifier = Modifier
                                .constrainAs(okayButton) {
                                    top.linkTo(parent.top)
                                    bottom.linkTo(parent.bottom)
                                    start.linkTo(if (showCancelButton) cancelButton.end else parent.start)
                                    end.linkTo(parent.end)
                                }
                                .fillMaxWidth(if (showCancelButton) 0.5f else 1f)
                        ) {
                            Text(
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                text = okayButtonText ?: "OK"
                            )
                        }
                    }
                }
            }
        }
    }
}

@Preview
@Composable
fun PreviewGroovinOkayCancelDialog() {
    AppTheme {
        GroovinOkayCancelDialog(
            title = "Preview Dialog"
        ) {
            Text("Contents")
        }
    }
}