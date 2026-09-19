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
fun EditHorseDropdown(
    stall: Stall,
    horse: Horse,
    onEdited: () -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    val editedStats = remember(horse) {
        mutableStateMapOf<StatType, Stat>().apply {
            putAll(horse.stats)
        }
    }

    Column {
        Button(
            onClick = {
                expanded = !expanded
            }
        ) {
            Text(
                if (expanded) {
                    "${horse.name} ▲"
                } else {
                    "${horse.name} ▼"
                }
            )
            }
        }

        if (expanded) {
            CustomHorseStatsTable(editedStats)

            Button(
                onClick = {
                    editedStats.forEach { (type, newStat) ->
                        val oldStat = horse.stats.getValue(type)

                        if (newStat.level != oldStat.level) {
                            stall.updateHorseStat(
                                horse,
                                type,
                                StatField.LEVEL,
                                newStat.level
                            )
                        }

                        if (newStat.limit != oldStat.limit) {
                            stall.updateHorseStat(
                                horse,
                                type,
                                StatField.LIMIT,
                                newStat.limit
                            )
                        }

                        if (newStat.max != oldStat.max) {
                            stall.updateHorseStat(
                                horse,
                                type,
                                StatField.MAX,
                                newStat.max
                            )
                        }
                    }

                    expanded = false
                    onEdited()
                }
            ) {
                Text("Accept changes")
            }
        }
    }
