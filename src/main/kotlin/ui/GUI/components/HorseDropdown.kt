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
fun HorseDropdown(horse: Horse) {
    var expanded by remember { mutableStateOf(false) }

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

        if (expanded) {
            Column {
                Row {
                    Text(
                        text = "Stat",
                        modifier = Modifier.width(150.dp)
                    )

                    Text(
                        text = "Level",
                        modifier = Modifier.width(80.dp)
                    )

                    Text(
                        text = "Limit",
                        modifier = Modifier.width(80.dp)
                    )

                    Text(
                        text = "Max",
                        modifier = Modifier.width(80.dp)
                    )
                }

                StatType.entries.forEach { type ->
                    val stat = horse.stats.getValue(type)

                    Row {
                        Text(
                            text = type.name,
                            modifier = Modifier.width(150.dp)
                        )

                        Text(
                            text = stat.level.toString(),
                            modifier = Modifier.width(80.dp)
                        )

                        Text(
                            text = stat.limit.toString(),
                            modifier = Modifier.width(80.dp)
                        )

                        Text(
                            text = stat.max.toString(),
                            modifier = Modifier.width(80.dp)
                        )
                    }
                }
            }
        }
    }
}
