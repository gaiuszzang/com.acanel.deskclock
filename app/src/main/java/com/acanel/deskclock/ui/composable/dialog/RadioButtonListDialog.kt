package com.acanel.deskclock.ui.composable.dialog

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.MaterialTheme
import androidx.compose.material.RadioButton
import androidx.compose.material.RadioButtonDefaults
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.acanel.deskclock.ui.theme.Black
import com.acanel.groovin.composable.GroovinOkayCancelDialog
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

sealed interface LazyRadioButtonListDialogContent<T> {
    class Loading<T>: LazyRadioButtonListDialogContent<T>
    class Loaded<T>(
        val content: List<T>,
        val selectedIndex: Int
    ): LazyRadioButtonListDialogContent<T>
}

data class LazyRadioButtonListDialogState<T> (
    val isShow: Boolean,
    val title: String,
    val itemToValue: (T) -> String,
    val loadContent: suspend () -> LazyRadioButtonListDialogContent.Loaded<T>,
    val onSelected: suspend (T) -> Unit,
    val onDismiss: () -> Unit
)

@Composable
fun <T> LazyRadioButtonListDialog(
    state: LazyRadioButtonListDialogState<T>,
    parentScope: CoroutineScope
) {
    if (!state.isShow) return
    var dialogContent: LazyRadioButtonListDialogContent<T> by remember { mutableStateOf(LazyRadioButtonListDialogContent.Loading()) }
    var selectedIndex by remember { mutableStateOf(0) }
    val listState: LazyListState = rememberLazyListState()

    LaunchedEffect(true) {
        val loadContent = state.loadContent()
        dialogContent = loadContent
        selectedIndex = loadContent.selectedIndex
        listState.scrollToItem(selectedIndex)
    }

    GroovinOkayCancelDialog(
        title = state.title,
        cancelable = true,
        onPositiveClick = {
            parentScope.launch {
                if (dialogContent is LazyRadioButtonListDialogContent.Loaded) {
                    state.onSelected((dialogContent as LazyRadioButtonListDialogContent.Loaded<T>).content[selectedIndex])
                }
                state.onDismiss()
            }
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
                                    selectedColor = MaterialTheme.colors.primary,
                                    unselectedColor = Color.LightGray
                                )
                            )
                            Text(text = state.itemToValue(item), color = Black)
                        }
                    }
                }
            }
        }
    }
}