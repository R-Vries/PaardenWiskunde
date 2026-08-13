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
    var currentScreen by remember { mutableStateOf("menu") }

    when (currentScreen) {
        "menu" -> MainMenu(
            onInspectHorses = {
                currentScreen = "inspect"
            },
            onFeedingPlan = {
                currentScreen = "feeding"
            },
            onAddHorse = {
                currentScreen = "add"
            },
            onRemoveHorse = {
                currentScreen = "remove"
            },
            onRenameHorse = {
                currentScreen = "rename"
            },
            onEditHorse = {
                currentScreen = "edit"
            }
        )

        "inspect" -> InspectHorsesScreen(
            onBack = {
                currentScreen = "menu"
            }
        )

        "feeding" -> FeedingPlanScreen(
            onBack = {
                currentScreen = "menu"
            }
        )

        "add" -> AddHorseScreen(
            onBack = {
                currentScreen = "menu"
            }
        )

        "remove" -> RemoveHorseScreen(
            onBack = {
                currentScreen = "menu"
            }
        )

        "rename" -> RenameHorseScreen(
            onBack = {
                currentScreen = "menu"
            }
        )

        "edit" -> EditHorseScreen(
            onBack = {
                currentScreen = "menu"
            }
        )
    }
}

@Composable
fun MainMenu(
    onInspectHorses: () -> Unit,
    onFeedingPlan: () -> Unit,
    onAddHorse: () -> Unit,
    onRemoveHorse: () -> Unit,
    onRenameHorse: () -> Unit,
    onEditHorse: () -> Unit
) {
    Column {
        Text("Rick's Manege")

        Button(
            onClick = onInspectHorses
        ) {
            Text("1. Inspect horses")
        }

        Button(
            onClick = onFeedingPlan
        ) {
            Text("2. Calculate feeding plan")
        }

        Button(
            onClick = onAddHorse
        ) {
            Text("3. Add horse")
        }

        Button(
            onClick = onRemoveHorse
        ) {
            Text("4. Remove horse")
        }

        Button(
            onClick = onRenameHorse
        ) {
            Text("5. Rename horse")
        }

        Button(
            onClick = onEditHorse
        ) {
            Text("6. Edit horse")
        }
    }
}

@Composable
fun InspectHorsesScreen(
    onBack: () -> Unit
) {
    Column {
        Text("Inspect horses")
        Text("Hier komen straks de paarden.")

        Button(
            onClick = onBack
        ) {
            Text("Back")
        }
    }
}

@Composable
fun FeedingPlanScreen(
    onBack: () -> Unit
) {
    Column {
        Text("Calculate feeding plan")
        Text("Hier komt straks het voedingsplan.")

        Button(
            onClick = onBack
        ) {
            Text("Back")
        }
    }
}

@Composable
fun AddHorseScreen(
    onBack: () -> Unit
) {
    Column {
        Text("Add horse")
        Text("Hier kun je straks een paard toevoegen.")

        Button(
            onClick = onBack
        ) {
            Text("Back")
        }
    }
}

@Composable
fun RemoveHorseScreen(
    onBack: () -> Unit
) {
    Column {
        Text("Remove horse")
        Text("Hier kun je straks een paard verwijderen.")

        Button(
            onClick = onBack
        ) {
            Text("Back")
        }
    }
}

@Composable
fun RenameHorseScreen(
    onBack: () -> Unit
) {
    Column {
        Text("Rename horse")
        Text("Hier kun je straks een paard hernoemen.")

        Button(
            onClick = onBack
        ) {
            Text("Back")
        }
    }
}

@Composable
fun EditHorseScreen(
    onBack: () -> Unit
) {
    Column {
        Text("Edit horse")
        Text("Hier kun je straks een paard aanpassen.")

        Button(
            onClick = onBack
        ) {
            Text("Back")
        }
    }
}