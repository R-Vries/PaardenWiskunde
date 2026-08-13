package ui

//Imports for window
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.width
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.layout.padding
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color

//Imports for calculations
import app.AppConfig
import data.StableRepository
import domain.stable.Stable
import domain.stable.Stall
import domain.horse.Horse
import domain.stat.StatType

fun startGUI() = application {
    if (AppConfig.isDevelopment) {
            println("Running in DEVELOPMENT mode")
        }
    StableRepository.load()

    Window(
        onCloseRequest = {
            StableRepository.save()
            exitApplication()
        },
        title = "PaardenWiskunde"
    ) {
        App(
            onExit = {
                StableRepository.save()
                exitApplication()
            }
        )
    }
}

@Composable
fun App(
    onExit: () -> Unit
) {
    var currentScreen by remember { mutableStateOf("stallSelection") }
    var selectedStall by remember { mutableStateOf<Stall?>(null) }

    when (currentScreen) {

       
        //Stall selection
        "stallSelection" -> StallSelectionScreen(
            onStallSelected = { stall ->
                selectedStall = stall
                currentScreen = "mainMenu"
            },
            onExit = onExit
        )

        //Main menu
        "mainMenu" -> {
            selectedStall?.let { stall ->
                MainMenu(
                    stall = stall,
                    onBack = {
                        currentScreen = "stallSelection"
                    },
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
            }
        }

        //Inspect horses
        "inspect" -> {
            selectedStall?.let { stall ->
                InspectHorsesScreen(
                    stall = stall,
                    onBack = {
                        currentScreen = "mainMenu"
                    }
                )
            }
        }

        //Feeding plan
        "feeding" -> {
            selectedStall?.let { stall ->
                FeedingPlanScreen(
                    stall = stall,
                    onBack = {
                        currentScreen = "mainMenu"
                    }
                )
            }
        }

        //Add horse
        "add" -> {
            selectedStall?.let { stall ->
                AddHorseScreen(
                    stall = stall,
                    onBack = {
                        currentScreen = "mainMenu"
                    }
                )
            }
        }

        //Remove horse
        "remove" -> {
            selectedStall?.let { stall ->
                RemoveHorseScreen(
                    stall = stall,
                    onBack = {
                        currentScreen = "mainMenu"
                    }
                )
            }
        }

        //Rename horse
        "rename" -> {
            selectedStall?.let { stall ->
                RenameHorseScreen(
                    stall = stall,
                    onBack = {
                        currentScreen = "mainMenu"
                    }
                )
            }
        }

        //Edit horse
        "edit" -> {
            selectedStall?.let { stall ->
                EditHorseScreen(
                    stall = stall,
                    onBack = {
                        currentScreen = "mainMenu"
                    }
                )
            }
        }
    }
}

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

@Composable
fun AddStallScreen(
    onStallAdded: (Stall) -> Unit,
    onBack: () -> Unit
) {
    var stallName by remember { mutableStateOf("") }

    Column {
        Text("Add new stall")

        TextField(
            value = stallName,
            onValueChange = {
                stallName = it
            },
            label = {
                Text("Stall name")
            }
        )

        Button(
            onClick = {
                val name = stallName.trim()
                    .ifEmpty {
                        "Stable #${Stable.stalls.size + 1}"
                    }

                val stall = Stable.addStall(name)
                onStallAdded(stall)
            }
        ) {
            Text("Add")
        }

        Button(
            onClick = onBack
        ) {
            Text("Back")
        }
    }
}

@Composable
fun MainMenu(
    stall: Stall,
    onBack: () -> Unit,
    onInspectHorses: () -> Unit,
    onFeedingPlan: () -> Unit,
    onAddHorse: () -> Unit,
    onRemoveHorse: () -> Unit,
    onRenameHorse: () -> Unit,
    onEditHorse: () -> Unit
) {
    Column {
        Text("${stall.name}'s menu")

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

        Button(
            onClick = onBack
        ) {
            Text("0. Back to stall selection")
        }
    }
}

@Composable
fun InspectHorsesScreen(
    stall: Stall,
    onBack: () -> Unit
) {
    Column {
        Text("${stall.name}'s Horses")

        if (stall.horseCount == 0) {
            Text("No horses in this stall")
        } else {
            for (index in 0 until stall.horseCount) {
                val horse = stall.get(index)

                HorseDropdown(horse)
            }
        }

        Button(
            onClick = onBack
        ) {
            Text("Back")
        }
    }
}

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

@Composable
fun FeedingPlanScreen(
    stall: Stall,
    onBack: () -> Unit
) {
    Column {
        Text("Calculate feeding plan")
        Text("Stall: ${stall.name}")

        Button(
            onClick = onBack
        ) {
            Text("Back")
        }
    }
}

@Composable
fun AddHorseScreen(
    stall: Stall,
    onBack: () -> Unit
) {
    Column {
        Text("Add horse")
        Text("Add a horse to ${stall.name}")

        Button(
            onClick = onBack
        ) {
            Text("Back")
        }
    }
}

@Composable
fun RemoveHorseScreen(
    stall: Stall,
    onBack: () -> Unit
) {
    Column {
        Text("Remove horse")
        Text("Remove a horse from ${stall.name}")

        Button(
            onClick = onBack
        ) {
            Text("Back")
        }
    }
}

@Composable
fun RenameHorseScreen(
    stall: Stall,
    onBack: () -> Unit
) {
    Column {
        Text("Rename horse")
        Text("Rename a horse in ${stall.name}")

        Button(
            onClick = onBack
        ) {
            Text("Back")
        }
    }
}

@Composable
fun EditHorseScreen(
    stall: Stall,
    onBack: () -> Unit
) {
    Column {
        Text("Edit horse")
        Text("Edit a horse in ${stall.name}")

        Button(
            onClick = onBack
        ) {
            Text("Back")
        }
    }
}