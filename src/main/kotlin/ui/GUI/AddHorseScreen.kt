package ui.GUI

import ui.GUI.components.CustomHorseStatsTable
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
fun AddHorseScreen(
    stall: Stall,
    onBack: () -> Unit
) {
    var horseName by remember { mutableStateOf("") }
    var customStats by remember { mutableStateOf(false) }

    // Maak een tijdelijk paard om de standaardstats op te halen
    val defaultHorse = remember {
        Horse("Default")
    }

    // Begin de custom stats met dezelfde waardes als de default stats
    val stats = remember {
        mutableStateMapOf<StatType, Stat>().apply {
            putAll(defaultHorse.stats)
        }
    }

    Column {
        Text("Add horse to ${stall.name}")

        TextField(
            value = horseName,
            onValueChange = {
                horseName = it
            },
            label = {
                Text("Horse name")
            }
        )

        Row {
            Button(
                onClick = {
                    customStats = false
                },
                enabled = customStats
            ) {
                Text("Default stats")
            }

            Button(
                onClick = {
                    customStats = true
                },
                enabled = !customStats
            ) {
                Text("Custom stats")
            }
        }

        if (customStats) {
            CustomHorseStatsTable(stats)
        }

        Button(
            onClick = {
                val name = horseName.trim()
                    .ifEmpty {
                        "Horse #${stall.horseCount + 1}"
                    }

                if (customStats) {
                    stall.addHorse(name, stats.toMap())
                } else {
                    stall.addHorse(Horse(name))
                }

                onBack()
            }
        ) {
            Text("Add horse")
        }

        Button(
            onClick = onBack
        ) {
            Text("Back")
        }
    }
}
