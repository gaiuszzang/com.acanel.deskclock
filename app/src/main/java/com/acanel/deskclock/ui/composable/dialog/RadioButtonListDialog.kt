package com.acanel.deskclock.ui.composable.dialog

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

sealed interface LazyRadioButtonListDialogContent<T> {
    class Loading<T>: LazyRadioButtonListDialogContent<T>
    class Loaded<T>(
        val content: List<T>,
        val selectedIndex: Int
    ): LazyRadioButtonListDialogContent<T>
}

@Immutable
data class LazyRadioButtonListDialogState<T> (
    val isShow: Boolean,
    val title: String,
    val itemToValue: (T) -> String,
    val loadContent: suspend () -> LazyRadioButtonListDialogContent.Loaded<T>,
    val onSelected: (T) -> Unit,
    val onDismiss: () -> Unit
)

@Composable
fun <T> LazyRadioButtonListDialog(
    state: LazyRadioButtonListDialogState<T>
) {
    if (!state.isShow) return
    var dialogContent: LazyRadioButtonListDialogContent<T> by remember { mutableStateOf(LazyRadioButtonListDialogContent.Loading()) }
    var selectedIndex by remember { mutableIntStateOf(0) }
    val listState: LazyListState = rememberLazyListState()

    LaunchedEffect(true) {
        val loadContent = state.loadContent()
        dialogContent = loadContent
        selectedIndex = loadContent.selectedIndex
        if (selectedIndex in 0 until loadContent.content.size) {
            listState.scrollToItem(selectedIndex)
        }
    }

    GroovinOkayCancelDialog(
        title = state.title,
        cancelable = true,
        onPositiveClick = {
            if (dialogContent is LazyRadioButtonListDialogContent.Loaded) {
                state.onSelected((dialogContent as LazyRadioButtonListDialogContent.Loaded<T>).content[selectedIndex])
            }
            state.onDismiss()
        },
        onCancelClick = state.onDismiss
    ) {
        Box(
            modifier = Modifier.fillMaxWidth().height(300.dp).wrapContentSize(Alignment.Center)
        ) {
            if (dialogContent is LazyRadioButtonListDialogContent.Loading) {
                Text(text = "Loading...")
            } else if (dialogContent is LazyRadioButtonListDialogContent.Loaded) {
                val content = dialogContent as LazyRadioButtonListDialogContent.Loaded<T>
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize(),
                    state = listState
                ) {
                    itemsIndexed(content.content) { index, item ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable(true) { selectedIndex = index },
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(
                                selected = index == selectedIndex,
                                onClick = { selectedIndex = index },
                                colors = RadioButtonDefaults.colors(
                                    selectedColor = MaterialTheme.colorScheme.primary,
                                    unselectedColor = Color.LightGray
                                )
                            )
                            Text(text = state.itemToValue(item), color = Color.Black)
                        }
                    }
                }
            }
        }
    }
}