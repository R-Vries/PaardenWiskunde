package ui.GUI

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.layout.padding
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import app.AppConfig
import data.StableRepository
import domain.stable.Stall

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

    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .verticalScroll(scrollState)
            .padding(10.dp)
    ) {
        when (currentScreen) {

            // =========================
            // STALL SELECTION
            // =========================

            "stallSelection" -> StallSelectionScreen(
                onStallSelected = { stall ->
                    selectedStall = stall
                    currentScreen = "mainMenu"
                },
                onExit = onExit
            )

            // =========================
            // MAIN MENU
            // =========================

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

            // =========================
            // INSPECT
            // =========================

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

            // =========================
            // FEEDING
            // =========================

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

            // =========================
            // ADD HORSE
            // =========================

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

            // =========================
            // REMOVE HORSE
            // =========================

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

            // =========================
            // RENAME HORSE
            // =========================

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

            // =========================
            // EDIT HORSE
            // =========================

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
}
