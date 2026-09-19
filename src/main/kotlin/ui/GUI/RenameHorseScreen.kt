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
fun RenameHorseScreen(
    stall: Stall,
    onBack: () -> Unit
) {
    var selectedHorse by remember { mutableStateOf<Horse?>(null) }
    var newHorseName by remember { mutableStateOf("") }
    var renamedHorseName by remember { mutableStateOf<String?>(null) }
    var oldHorseName by remember { mutableStateOf<String?>(null) }

    // Succesmelding na het hernoemen
    if (renamedHorseName != null && oldHorseName != null) {
        Column {
            Text("Horse renamed successfully!")
            Text("$oldHorseName has been renamed to $renamedHorseName.")

            Button(
                onClick = onBack
            ) {
                Text("Back")
            }
        }

        return
    }

    // Scherm om een nieuwe naam in te voeren
    if (selectedHorse != null) {
        Column {
            Text("Rename ${selectedHorse!!.name}")

            TextField(
                value = newHorseName,
                onValueChange = {
                    newHorseName = it
                },
                label = {
                    Text("New horse name")
                }
            )

            Row {
                Button(
                    onClick = {
                        val name = newHorseName.trim()

                        if (name.isNotEmpty()) {
                            val oldName = selectedHorse!!.name

                            stall.renameHorse(
                                selectedHorse!!,
                                name
                            )

                            oldHorseName = oldName
                            renamedHorseName = name
                            selectedHorse = null
                        }
                    },
                    enabled = newHorseName.trim().isNotEmpty()
                ) {
                    Text("Rename")
                }

                Button(
                    onClick = {
                        selectedHorse = null
                        newHorseName = ""
                    }
                ) {
                    Text("Cancel")
                }
            }
        }

        return
    }

    // Paardenlijst
    Column {
        Text("Rename horse in ${stall.name}")

        if (stall.horseCount == 0) {
            Text("No horses in this stall")
        } else {
            for (index in 0 until stall.horseCount) {
                val horse = stall.get(index)

                Button(
                    onClick = {
                        selectedHorse = horse
                        newHorseName = horse.name
                    }
                ) {
                    Text(horse.name)
                }
            }
        }

        Button(
            onClick = onBack
        ) {
            Text("Back")
        }
    }
}
