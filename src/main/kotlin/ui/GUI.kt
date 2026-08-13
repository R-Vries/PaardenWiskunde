package ui

// Application window imports
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application


// Imports from programm
import app.AppConfig
import data.StableRepository
import domain.horse.Horse
import domain.horse.Stat
import domain.horse.StatField
import domain.horse.UpdateResult
import domain.material.FeedValidation
import domain.stable.Stable
import domain.stable.Stall
import domain.stat.StatType
import planner.formatPlan
import kotlin.collections.get
import kotlin.text.ifEmpty
import kotlin.time.measureTimedValue

fun startGUI() = application {
    if (AppConfig.isDevelopment) {
        println("Running in DEVELOPMENT mode")
    }
    Window(
        onCloseRequest = ::exitApplication,
        title = "PaardenWiskunde"
    ) {
        App()
    }
}

@Composable
fun App() {

    // Houdt bij welk scherm wordt weergegeven
    var currentScreen by remember { mutableStateOf("menu") }

    when (currentScreen) {

        "menu" -> {
            Column {
                Text("Rick's Manege")

                Button(
                    onClick = {
                        currentScreen = "inspect"
                    }
                ) {
                    Text("1. Inspect horses")
                }

                Button(
                    onClick = {
                        currentScreen = "feeding"
                    }
                ) {
                    Text("2. Calculate feeding plan")
                }

                Button(
                    onClick = {
                        currentScreen = "add"
                    }
                ) {
                    Text("3. Add horse")
                }

                Button(
                    onClick = {
                        currentScreen = "remove"
                    }
                ) {
                    Text("4. Remove horse")
                }

                Button(
                    onClick = {
                        currentScreen = "rename"
                    }
                ) {
                    Text("5. Rename horse")
                }

                Button(
                    onClick = {
                        currentScreen = "edit"
                    }
                ) {
                    Text("6. Edit horse")
                }

                Button(
                    onClick = {
                        println("Returning to stall selection...")
                    }
                ) {
                    Text("0. Back to stall selection")
                }
            }
        }

        "inspect" -> {
            Column {
                Text("Inspect horses")
                Text("Hier komen straks de paarden.")

                Button(
                    onClick = {
                        currentScreen = "menu"
                    }
                ) {
                    Text("Back")
                }
            }
        }

        "feeding" -> {
            Column {
                Text("Calculate feeding plan")
                Text("Hier komt straks het voedingsplan.")

                Button(
                    onClick = {
                        currentScreen = "menu"
                    }
                ) {
                    Text("Back")
                }
            }
        }

        "add" -> {
            Column {
                Text("Add horse")
                Text("Hier kun je straks een paard toevoegen.")

                Button(
                    onClick = {
                        currentScreen = "menu"
                    }
                ) {
                    Text("Back")
                }
            }
        }

        "remove" -> {
            Column {
                Text("Remove horse")
                Text("Hier kun je straks een paard verwijderen.")

                Button(
                    onClick = {
                        currentScreen = "menu"
                    }
                ) {
                    Text("Back")
                }
            }
        }

        "rename" -> {
            Column {
                Text("Rename horse")
                Text("Hier kun je straks een paard hernoemen.")

                Button(
                    onClick = {
                        currentScreen = "menu"
                    }
                ) {
                    Text("Back")
                }
            }
        }

        "edit" -> {
            Column {
                Text("Edit horse")
                Text("Hier kun je straks een paard aanpassen.")

                Button(
                    onClick = {
                        currentScreen = "menu"
                    }
                ) {
                    Text("Back")
                }
            }
        }
    }
}