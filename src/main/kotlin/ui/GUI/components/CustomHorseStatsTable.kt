package ui.GUI.components

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
fun CustomHorseStatsTable(
    stats: MutableMap<StatType, Stat>
) {
    val types = StatType.entries

    // Voor iedere stat:
    // [0] = level
    // [1] = limit
    // [2] = max
    val focusRequesters = remember {
        types.associateWith {
            listOf(
                FocusRequester(),
                FocusRequester(),
                FocusRequester()
            )
        }
    }

    Column {
        // Header
        Row {
            Text(
                text = "Stat",
                modifier = Modifier.width(150.dp)
            )

            Text(
                text = "Level",
                modifier = Modifier.width(100.dp)
            )

            Text(
                text = "Limit",
                modifier = Modifier.width(100.dp)
            )

            Text(
                text = "Max",
                modifier = Modifier.width(100.dp)
            )
        }

        types.forEachIndexed { index, type ->
            val stat = stats.getValue(type)
            val requesters = focusRequesters.getValue(type)

            // We gebruiken per veld TextFieldValue zodat we
            // de tekst volledig kunnen selecteren.
            var levelValue by remember(type) {
                mutableStateOf(
                    TextFieldValue(
                        text = stat.level.toString(),
                        selection = TextRange(
                            0,
                            stat.level.toString().length
                        )
                    )
                )
            }

            var limitValue by remember(type) {
                mutableStateOf(
                    TextFieldValue(
                        text = stat.limit.toString(),
                        selection = TextRange(
                            0,
                            stat.limit.toString().length
                        )
                    )
                )
            }

            var maxValue by remember(type) {
                mutableStateOf(
                    TextFieldValue(
                        text = stat.max.toString(),
                        selection = TextRange(
                            0,
                            stat.max.toString().length
                        )
                    )
                )
            }

            Row {
                Text(
                    text = type.name,
                    modifier = Modifier.width(150.dp)
                )

                // -------------------
                // LEVEL
                // -------------------
                TextField(
                    value = levelValue,
                    onValueChange = { newValue ->
                        levelValue = newValue

                        newValue.text.toIntOrNull()?.let { newLevel ->
                            stats[type] = Stat(
                                level = newLevel,
                                limit = stat.limit,
                                max = stat.max
                            )
                        }
                    },
                    modifier = Modifier
                        .width(100.dp)
                        .focusRequester(requesters[0])
                        .onFocusChanged { focusState ->
                            if (focusState.isFocused) {
                                levelValue = levelValue.copy(
                                    selection = TextRange(
                                        0,
                                        levelValue.text.length
                                    )
                                )
                            }
                        }
                        .onPreviewKeyEvent { event ->

                            // TAB → Limit
                            if (
                                event.key == Key.Tab &&
                                event.type == KeyEventType.KeyDown &&
                                !event.isShiftPressed
                            ) {
                                requesters[1].requestFocus()
                                true
                            }

                            // SHIFT + TAB → vorige stat
                            else if (
                                event.key == Key.Tab &&
                                event.type == KeyEventType.KeyDown &&
                                event.isShiftPressed
                            ) {
                                if (index > 0) {
                                    focusRequesters
                                        .getValue(types[index - 1])[2]
                                        .requestFocus()
                                }
                                true
                            } else {
                                false
                            }
                        }
                )

                // -------------------
                // LIMIT
                // -------------------
                TextField(
                    value = limitValue,
                    onValueChange = { newValue ->
                        limitValue = newValue

                        newValue.text.toIntOrNull()?.let { newLimit ->
                            stats[type] = Stat(
                                level = stat.level,
                                limit = newLimit,
                                max = stat.max
                            )
                        }
                    },
                    modifier = Modifier
                        .width(100.dp)
                        .focusRequester(requesters[1])
                        .onFocusChanged { focusState ->
                            if (focusState.isFocused) {
                                limitValue = limitValue.copy(
                                    selection = TextRange(
                                        0,
                                        limitValue.text.length
                                    )
                                )
                            }
                        }
                        .onPreviewKeyEvent { event ->

                            // TAB → Max
                            if (
                                event.key == Key.Tab &&
                                event.type == KeyEventType.KeyDown &&
                                !event.isShiftPressed
                            ) {
                                requesters[2].requestFocus()
                                true
                            }

                            // SHIFT + TAB → Level
                            else if (
                                event.key == Key.Tab &&
                                event.type == KeyEventType.KeyDown &&
                                event.isShiftPressed
                            ) {
                                requesters[0].requestFocus()
                                true
                            } else {
                                false
                            }
                        }
                )

                // -------------------
                // MAX
                // -------------------
                TextField(
                    value = maxValue,
                    onValueChange = { newValue ->
                        maxValue = newValue

                        newValue.text.toIntOrNull()?.let { newMax ->
                            stats[type] = Stat(
                                level = stat.level,
                                limit = stat.limit,
                                max = newMax
                            )
                        }
                    },
                    modifier = Modifier
                        .width(100.dp)
                        .focusRequester(requesters[2])
                        .onFocusChanged { focusState ->
                            if (focusState.isFocused) {
                                maxValue = maxValue.copy(
                                    selection = TextRange(
                                        0,
                                        maxValue.text.length
                                    )
                                )
                            }
                        }
                        .onPreviewKeyEvent { event ->

                            // TAB → Level volgende stat
                            if (
                                event.key == Key.Tab &&
                                event.type == KeyEventType.KeyDown &&
                                !event.isShiftPressed
                            ) {
                                if (index < types.lastIndex) {
                                    focusRequesters
                                        .getValue(types[index + 1])[0]
                                        .requestFocus()
                                }
                                true
                            }

                            // SHIFT + TAB → Limit
                            else if (
                                event.key == Key.Tab &&
                                event.type == KeyEventType.KeyDown &&
                                event.isShiftPressed
                            ) {
                                requesters[1].requestFocus()
                                true
                            } else {
                                false
                            }
                        }
                )
            }
        }
    }
}
