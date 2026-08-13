package ui

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

import data.StableRepository
import domain.stable.Stable
import domain.stable.Stall

fun startGUI() = application {
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

        Stable.stalls.forEach { stall ->
            Button(
                onClick = {
                    onStallSelected(stall)
                }
            ) {
                Text(stall.name)
            }
        }

        Button(
            onClick = {
                showAddStallScreen = true
            }
        ) {
            Text("Add new stall")
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