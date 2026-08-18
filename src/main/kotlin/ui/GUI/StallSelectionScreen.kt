package ui.GUI

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import androidx.compose.ui.Alignment
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.isShiftPressed
import androidx.compose.ui.input.key.onPreviewKeyEvent
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.type
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.TextFieldValue
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import app.AppConfig
import data.StableRepository
import domain.horse.Horse
import domain.horse.Stat
import domain.horse.StatField
import domain.material.Material
import domain.stable.Stable
import domain.stable.Stall
import domain.stat.StatType

@Composable
fun StallSelectionScreen(
    onStallSelected: (Stall) -> Unit,
    onExit: () -> Unit
) {
    var showAddStallScreen by remember { mutableStateOf(false) }

    if (showAddStallScreen) {
        AddStallScreen(
            onStallAdded = { stall ->
                showAddStallScreen = false
                onStallSelected(stall)
            },
            onBack = {
                showAddStallScreen = false
            }
        )

        return
    }

    Column {
        Text("PaardenWiskunde")
        Text("Select a stall")

        Stable.stalls
            .take(6)
            .chunked(3)
            .forEach { row ->
                Row {
                    row.forEach { stall ->
                        Button(
                            onClick = {
                                onStallSelected(stall)
                            }
                        ) {
                            Text(stall.name)
                        }
                    }
                }
            }

        Button(
            onClick = {
                if (Stable.stalls.size < 6) {
                    showAddStallScreen = true
                }
            },
            enabled = Stable.stalls.size < 6
        ) {
            Text(
                text = if (Stable.stalls.size < 6) {
                    "Add new stall"
                } else {
                    "Maximum of 6 stalls reached"
                },
                color = if (Stable.stalls.size < 6) {
                    Color.Unspecified
                } else {
                    Color.Red
                }
            )
        }

        Button(
            onClick = onExit
        ) {
            Text("Exit")
        }
    }
}
