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
fun EditHorseStatsTable(
    stall: Stall,
    horse: Horse
) {
    val types = StatType.entries

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

            val stat = horse.stats.getValue(type)
            val requesters = focusRequesters.getValue(type)

            val levelValue = remember(type, horse) {
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

            val limitValue = remember(type, horse) {
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

            val maxValue = remember(type, horse) {
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

                // -------------------------
                // STAT
                // -------------------------

                Text(
                    text = type.name,
                    modifier = Modifier.width(150.dp)
                )

                // -------------------------
                // LEVEL
                // -------------------------

                TextField(
                    value = levelValue.value,
                    onValueChange = { newValue ->
                        levelValue.value = newValue

                        newValue.text.toIntOrNull()?.let { newLevel ->
                            stall.updateHorseStat(
                                horse,
                                type,
                                StatField.LEVEL,
                                newLevel
                            )
                        }
                    },
                    modifier = Modifier
                        .width(100.dp)
                        .focusRequester(requesters[0])
                        .onFocusChanged { focusState ->
                            if (focusState.isFocused) {
                                levelValue.value =
                                    levelValue.value.copy(
                                        selection = TextRange(
                                            0,
                                            levelValue.value.text.length
                                        )
                                    )
                            }
                        }
                        .onPreviewKeyEvent { event ->

                            // Tab -> Limit
                            if (
                                event.key == Key.Tab &&
                                event.type == KeyEventType.KeyDown &&
                                !event.isShiftPressed
                            ) {
                                requesters[1].requestFocus()
                                true
                            }

                            // Shift + Tab -> vorige Max
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

                // -------------------------
                // LIMIT
                // -------------------------

                TextField(
                    value = limitValue.value,
                    onValueChange = { newValue ->
                        limitValue.value = newValue

                        newValue.text.toIntOrNull()?.let { newLimit ->
                            stall.updateHorseStat(
                                horse,
                                type,
                                StatField.LIMIT,
                                newLimit
                            )
                        }
                    },
                    modifier = Modifier
                        .width(100.dp)
                        .focusRequester(requesters[1])
                        .onFocusChanged { focusState ->
                            if (focusState.isFocused) {
                                limitValue.value =
                                    limitValue.value.copy(
                                        selection = TextRange(
                                            0,
                                            limitValue.value.text.length
                                        )
                                    )
                            }
                        }
                        .onPreviewKeyEvent { event ->

                            // Tab -> Max
                            if (
                                event.key == Key.Tab &&
                                event.type == KeyEventType.KeyDown &&
                                !event.isShiftPressed
                            ) {
                                requesters[2].requestFocus()
                                true
                            }

                            // Shift + Tab -> Level
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

                // -------------------------
                // MAX
                // -------------------------

                TextField(
                    value = maxValue.value,
                    onValueChange = { newValue ->
                        maxValue.value = newValue

                        newValue.text.toIntOrNull()?.let { newMax ->
                            stall.updateHorseStat(
                                horse,
                                type,
                                StatField.MAX,
                                newMax
                            )
                        }
                    },
                    modifier = Modifier
                        .width(100.dp)
                        .focusRequester(requesters[2])
                        .onFocusChanged { focusState ->
                            if (focusState.isFocused) {
                                maxValue.value =
                                    maxValue.value.copy(
                                        selection = TextRange(
                                            0,
                                            maxValue.value.text.length
                                        )
                                    )
                            }
                        }
                        .onPreviewKeyEvent { event ->

                            // Tab -> volgende stat
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

                            // Shift + Tab -> Limit
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
