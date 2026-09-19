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
fun RemoveHorseScreen(
    stall: Stall,
    onBack: () -> Unit
) {
    var selectedHorse by remember { mutableStateOf<Horse?>(null) }
    var removedHorseName by remember { mutableStateOf<String?>(null) }

    // Bevestiging nadat een paard verwijderd is
    if (removedHorseName != null) {
        Column {
            Text("Horse removed successfully!")
            Text("$removedHorseName has been removed from ${stall.name}.")

            Button(
                onClick = onBack
            ) {
                Text("Back")
            }
        }

        return
    }

    // Bevestiging voor verwijderen
    if (selectedHorse != null) {
        Column {
            Text(
                "Are you sure you want to remove ${selectedHorse!!.name}?"
            )

            Row {
                Button(
                    onClick = {
                        val horse = selectedHorse!!

                        stall.removeHorse(horse)

                        removedHorseName = horse.name
                        selectedHorse = null
                    }
                ) {
                    Text("Yes")
                }

                Button(
                    onClick = {
                        selectedHorse = null
                    }
                ) {
                    Text("No")
                }
            }
        }

        return
    }

    // Paardenlijst
    Column {
        Text("Remove horse from ${stall.name}")

        if (stall.horseCount == 0) {
            Text("No horses in this stall")
        } else {
            for (index in 0 until stall.horseCount) {
                val horse = stall.get(index)

                Button(
                    onClick = {
                        selectedHorse = horse
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
