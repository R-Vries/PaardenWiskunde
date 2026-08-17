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
import androidx.compose.foundation.layout.padding
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.runtime.mutableStateMapOf

//Imports voor tabel
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.type
import androidx.compose.ui.input.key.isShiftPressed
import androidx.compose.ui.input.key.onPreviewKeyEvent
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.TextFieldValue

//Imports for calculations
import app.AppConfig
import data.StableRepository
import domain.stable.Stable
import domain.stable.Stall
import domain.horse.Horse
import domain.stat.StatType
import domain.horse.Stat
import domain.horse.StatField

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

@Composable
fun CustomHorseStatsTable(
    stats: MutableMap<StatType, Stat>
) {
    val types = StatType.entries

    // Voor iedere stat:
    // [0] = level
    // [1] = limit
    // [2] = max
    val focusRequesters = remember {
        types.associateWith {
            listOf(
                FocusRequester(),
                FocusRequester(),
                FocusRequester()
            )
        }
    }

    Column {
        // Header
        Row {
            Text(
                text = "Stat",
                modifier = Modifier.width(150.dp)
            )

            Text(
                text = "Level",
                modifier = Modifier.width(100.dp)
            )

            Text(
                text = "Limit",
                modifier = Modifier.width(100.dp)
            )

            Text(
                text = "Max",
                modifier = Modifier.width(100.dp)
            )
        }

        types.forEachIndexed { index, type ->
            val stat = stats.getValue(type)
            val requesters = focusRequesters.getValue(type)

            // We gebruiken per veld TextFieldValue zodat we
            // de tekst volledig kunnen selecteren.
            var levelValue by remember(type) {
                mutableStateOf(
                    TextFieldValue(
                        text = stat.level.toString(),
                        selection = TextRange(
                            0,
                            stat.level.toString().length
                        )
                    )
                )
            }

            var limitValue by remember(type) {
                mutableStateOf(
                    TextFieldValue(
                        text = stat.limit.toString(),
                        selection = TextRange(
                            0,
                            stat.limit.toString().length
                        )
                    )
                )
            }

            var maxValue by remember(type) {
                mutableStateOf(
                    TextFieldValue(
                        text = stat.max.toString(),
                        selection = TextRange(
                            0,
                            stat.max.toString().length
                        )
                    )
                )
            }

            Row {
                Text(
                    text = type.name,
                    modifier = Modifier.width(150.dp)
                )

                // -------------------
                // LEVEL
                // -------------------
                TextField(
                    value = levelValue,
                    onValueChange = { newValue ->
                        levelValue = newValue

                        newValue.text.toIntOrNull()?.let { newLevel ->
                            stats[type] = Stat(
                                level = newLevel,
                                limit = stat.limit,
                                max = stat.max
                            )
                        }
                    },
                    modifier = Modifier
                        .width(100.dp)
                        .focusRequester(requesters[0])
                        .onFocusChanged { focusState ->
                            if (focusState.isFocused) {
                                levelValue = levelValue.copy(
                                    selection = TextRange(
                                        0,
                                        levelValue.text.length
                                    )
                                )
                            }
                        }
                        .onPreviewKeyEvent { event ->

                            // TAB → Limit
                            if (
                                event.key == Key.Tab &&
                                event.type == KeyEventType.KeyDown &&
                                !event.isShiftPressed
                            ) {
                                requesters[1].requestFocus()
                                true
                            }

                            // SHIFT + TAB → vorige stat
                            else if (
                                event.key == Key.Tab &&
                                event.type == KeyEventType.KeyDown &&
                                event.isShiftPressed
                            ) {
                                if (index > 0) {
                                    focusRequesters
                                        .getValue(types[index - 1])[2]
                                        .requestFocus()
                                }
                                true
                            } else {
                                false
                            }
                        }
                )

                // -------------------
                // LIMIT
                // -------------------
                TextField(
                    value = limitValue,
                    onValueChange = { newValue ->
                        limitValue = newValue

                        newValue.text.toIntOrNull()?.let { newLimit ->
                            stats[type] = Stat(
                                level = stat.level,
                                limit = newLimit,
                                max = stat.max
                            )
                        }
                    },
                    modifier = Modifier
                        .width(100.dp)
                        .focusRequester(requesters[1])
                        .onFocusChanged { focusState ->
                            if (focusState.isFocused) {
                                limitValue = limitValue.copy(
                                    selection = TextRange(
                                        0,
                                        limitValue.text.length
                                    )
                                )
                            }
                        }
                        .onPreviewKeyEvent { event ->

                            // TAB → Max
                            if (
                                event.key == Key.Tab &&
                                event.type == KeyEventType.KeyDown &&
                                !event.isShiftPressed
                            ) {
                                requesters[2].requestFocus()
                                true
                            }

                            // SHIFT + TAB → Level
                            else if (
                                event.key == Key.Tab &&
                                event.type == KeyEventType.KeyDown &&
                                event.isShiftPressed
                            ) {
                                requesters[0].requestFocus()
                                true
                            } else {
                                false
                            }
                        }
                )

                // -------------------
                // MAX
                // -------------------
                TextField(
                    value = maxValue,
                    onValueChange = { newValue ->
                        maxValue = newValue

                        newValue.text.toIntOrNull()?.let { newMax ->
                            stats[type] = Stat(
                                level = stat.level,
                                limit = stat.limit,
                                max = newMax
                            )
                        }
                    },
                    modifier = Modifier
                        .width(100.dp)
                        .focusRequester(requesters[2])
                        .onFocusChanged { focusState ->
                            if (focusState.isFocused) {
                                maxValue = maxValue.copy(
                                    selection = TextRange(
                                        0,
                                        maxValue.text.length
                                    )
                                )
                            }
                        }
                        .onPreviewKeyEvent { event ->

                            // TAB → Level volgende stat
                            if (
                                event.key == Key.Tab &&
                                event.type == KeyEventType.KeyDown &&
                                !event.isShiftPressed
                            ) {
                                if (index < types.lastIndex) {
                                    focusRequesters
                                        .getValue(types[index + 1])[0]
                                        .requestFocus()
                                }
                                true
                            }

                            // SHIFT + TAB → Limit
                            else if (
                                event.key == Key.Tab &&
                                event.type == KeyEventType.KeyDown &&
                                event.isShiftPressed
                            ) {
                                requesters[1].requestFocus()
                                true
                            } else {
                                false
                            }
                        }
                )
            }
        }
    }
}

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

@Composable
fun EditHorseScreen(
    stall: Stall,
    onBack: () -> Unit
) {
    var editedHorseName by remember { mutableStateOf<String?>(null) }

    if (editedHorseName != null) {
        Column {
            Text("$editedHorseName has been edited.")

            Button(
                onClick = onBack
            ) {
                Text("Back")
            }
        }

        return
    }

    Column {
        Text("Edit horses in ${stall.name}")

        if (stall.horseCount == 0) {
            Text("No horses in this stall")
        } else {
            for (index in 0 until stall.horseCount) {
                val horse = stall.get(index)

                EditHorseDropdown(
                    stall = stall,
                    horse = horse,
                    onEdited = {
                        editedHorseName = horse.name
                    }
                )
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

@Composable
fun EditHorseStatsTable(
    stall: Stall,
    horse: Horse
) {
    val types = StatType.entries

    val focusRequesters = remember {
        types.associateWith {
            listOf(
                FocusRequester(),
                FocusRequester(),
                FocusRequester()
            )
        }
    }

    Column {
        // Header
        Row {
            Text(
                text = "Stat",
                modifier = Modifier.width(150.dp)
            )

            Text(
                text = "Level",
                modifier = Modifier.width(100.dp)
            )

            Text(
                text = "Limit",
                modifier = Modifier.width(100.dp)
            )

            Text(
                text = "Max",
                modifier = Modifier.width(100.dp)
            )
        }

        types.forEachIndexed { index, type ->

            val stat = horse.stats.getValue(type)
            val requesters = focusRequesters.getValue(type)

            val levelValue = remember(type, horse) {
                mutableStateOf(
                    TextFieldValue(
                        text = stat.level.toString(),
                        selection = TextRange(
                            0,
                            stat.level.toString().length
                        )
                    )
                )
            }

            val limitValue = remember(type, horse) {
                mutableStateOf(
                    TextFieldValue(
                        text = stat.limit.toString(),
                        selection = TextRange(
                            0,
                            stat.limit.toString().length
                        )
                    )
                )
            }

            val maxValue = remember(type, horse) {
                mutableStateOf(
                    TextFieldValue(
                        text = stat.max.toString(),
                        selection = TextRange(
                            0,
                            stat.max.toString().length
                        )
                    )
                )
            }
            Row {

                // -------------------------
                // STAT
                // -------------------------

                Text(
                    text = type.name,
                    modifier = Modifier.width(150.dp)
                )

                // -------------------------
                // LEVEL
                // -------------------------

                TextField(
                    value = levelValue.value,
                    onValueChange = { newValue ->
                        levelValue.value = newValue

                        newValue.text.toIntOrNull()?.let { newLevel ->
                            stall.updateHorseStat(
                                horse,
                                type,
                                StatField.LEVEL,
                                newLevel
                            )
                        }
                    },
                    modifier = Modifier
                        .width(100.dp)
                        .focusRequester(requesters[0])
                        .onFocusChanged { focusState ->
                            if (focusState.isFocused) {
                                levelValue.value =
                                    levelValue.value.copy(
                                        selection = TextRange(
                                            0,
                                            levelValue.value.text.length
                                        )
                                    )
                            }
                        }
                        .onPreviewKeyEvent { event ->

                            // Tab -> Limit
                            if (
                                event.key == Key.Tab &&
                                event.type == KeyEventType.KeyDown &&
                                !event.isShiftPressed
                            ) {
                                requesters[1].requestFocus()
                                true
                            }

                            // Shift + Tab -> vorige Max
                            else if (
                                event.key == Key.Tab &&
                                event.type == KeyEventType.KeyDown &&
                                event.isShiftPressed
                            ) {
                                if (index > 0) {
                                    focusRequesters
                                        .getValue(types[index - 1])[2]
                                        .requestFocus()
                                }

                                true
                            } else {
                                false
                            }
                        }
                )

                // -------------------------
                // LIMIT
                // -------------------------

                TextField(
                    value = limitValue.value,
                    onValueChange = { newValue ->
                        limitValue.value = newValue

                        newValue.text.toIntOrNull()?.let { newLimit ->
                            stall.updateHorseStat(
                                horse,
                                type,
                                StatField.LIMIT,
                                newLimit
                            )
                        }
                    },
                    modifier = Modifier
                        .width(100.dp)
                        .focusRequester(requesters[1])
                        .onFocusChanged { focusState ->
                            if (focusState.isFocused) {
                                limitValue.value =
                                    limitValue.value.copy(
                                        selection = TextRange(
                                            0,
                                            limitValue.value.text.length
                                        )
                                    )
                            }
                        }
                        .onPreviewKeyEvent { event ->

                            // Tab -> Max
                            if (
                                event.key == Key.Tab &&
                                event.type == KeyEventType.KeyDown &&
                                !event.isShiftPressed
                            ) {
                                requesters[2].requestFocus()
                                true
                            }

                            // Shift + Tab -> Level
                            else if (
                                event.key == Key.Tab &&
                                event.type == KeyEventType.KeyDown &&
                                event.isShiftPressed
                            ) {
                                requesters[0].requestFocus()
                                true
                            } else {
                                false
                            }
                        }
                )

                // -------------------------
                // MAX
                // -------------------------

                TextField(
                    value = maxValue.value,
                    onValueChange = { newValue ->
                        maxValue.value = newValue

                        newValue.text.toIntOrNull()?.let { newMax ->
                            stall.updateHorseStat(
                                horse,
                                type,
                                StatField.MAX,
                                newMax
                            )
                        }
                    },
                    modifier = Modifier
                        .width(100.dp)
                        .focusRequester(requesters[2])
                        .onFocusChanged { focusState ->
                            if (focusState.isFocused) {
                                maxValue.value =
                                    maxValue.value.copy(
                                        selection = TextRange(
                                            0,
                                            maxValue.value.text.length
                                        )
                                    )
                            }
                        }
                        .onPreviewKeyEvent { event ->

                            // Tab -> volgende stat
                            if (
                                event.key == Key.Tab &&
                                event.type == KeyEventType.KeyDown &&
                                !event.isShiftPressed
                            ) {
                                if (index < types.lastIndex) {
                                    focusRequesters
                                        .getValue(types[index + 1])[0]
                                        .requestFocus()
                                }

                                true
                            }

                            // Shift + Tab -> Limit
                            else if (
                                event.key == Key.Tab &&
                                event.type == KeyEventType.KeyDown &&
                                event.isShiftPressed
                            ) {
                                requesters[1].requestFocus()
                                true
                            } else {
                                false
                            }
                        }
                )
            }
        }
    }
}
