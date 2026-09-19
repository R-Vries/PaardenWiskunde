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
fun FeedingPlanScreen(
    stall: Stall,
    onBack: () -> Unit
) {
    var selectedHorse by remember { mutableStateOf<Horse?>(null) }

    var maxTierText by remember { mutableStateOf("") }
    var useHighestPossible by remember { mutableStateOf(false) }

    var plan by remember { mutableStateOf<List<Material>?>(null) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    // Bewaart de indexen van de aangevinkte items.
    //
    // Hierdoor kunnen dubbele ingrediënten onafhankelijk
    // van elkaar geselecteerd worden.
    var selectedItems by remember {
        mutableStateOf(setOf<Int>())
    }

    // Bericht na het voeren.
    var feedMessage by remember {
        mutableStateOf<String?>(null)
    }

    val scope = rememberCoroutineScope()
    var calculating by remember { mutableStateOf(false) }

    // Er moet een paard geselecteerd zijn én een tier gekozen zijn
    // voordat Calculate gebruikt kan worden.
    val tierSelected =
        useHighestPossible || maxTierText.isNotBlank()

    val canCalculate =
        selectedHorse != null &&
        tierSelected &&
        !calculating

    Row(
        modifier = Modifier.padding(10.dp)
    ) {

        // =========================
        // LINKERKANT
        // =========================

        Column(
            modifier = Modifier.width(350.dp)
        ) {
            Text("Calculate feeding plan")
            Text("Stall: ${stall.name}")

            Spacer(
                modifier = Modifier.height(10.dp)
            )

            Text("Select horse:")

            if (stall.horseCount == 0) {
                Text("No horses in this stall")
            } else {
                for (index in 0 until stall.horseCount) {
                    val horse = stall.get(index)

                    Button(
                        onClick = {
                            selectedHorse = horse

                            errorMessage = null
                            feedMessage = null
                            selectedItems = emptySet()

                            // Laad het laatst opgeslagen plan van dit paard.
                            plan = stall.getPlan(horse)
                        }
                    ) {
                        Text(
                            if (selectedHorse == horse) {
                                "${horse.name} ✓"
                            } else {
                                horse.name
                            }
                        )
                    }
                }
            }

            Spacer(
                modifier = Modifier.height(10.dp)
            )

            Text("Maximum food tier:")

            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                TextField(
                    value = maxTierText,
                    onValueChange = { value ->

                        // Alleen cijfers toestaan.
                        if (value.all { it.isDigit() }) {
                            maxTierText = value

                            // Zodra de gebruiker zelf een tier invult,
                            // is Highest possible niet meer geselecteerd.
                            useHighestPossible = false

                            errorMessage = null
                        }
                    },
                    label = {
                        Text("Max tier")
                    },
                    modifier = Modifier.width(120.dp)
                )

                Spacer(
                    modifier = Modifier.width(10.dp)
                )

                Button(
                    onClick = {
                        // Highest possible is nu geselecteerd.
                        useHighestPossible = true

                        // Geen 0 meer in het tekstveld.
                        maxTierText = ""

                        errorMessage = null
                    },
                    enabled = !useHighestPossible
                ) {
                    Text("Highest possible")
                }
            }

            Spacer(
                modifier = Modifier.height(10.dp)
            )

            Button(
                onClick = {
                    val horse = selectedHorse

                    if (horse == null) {
                        errorMessage = "Please select a horse first."
                        return@Button
                    }

                    val maxTier = if (useHighestPossible) {
                        0
                    } else {
                        maxTierText.toIntOrNull()
                    }

                    if (maxTier == null) {
                        errorMessage = "Please enter a valid max tier."
                        return@Button
                    }

                    if (maxTier < 0) {
                        errorMessage = "Max tier cannot be negative."
                        return@Button
                    }

                    errorMessage = null
                    feedMessage = null
                    calculating = true
                    selectedItems = emptySet()

                    scope.launch {
                        try {
                            // Altijd een NIEUW plan berekenen wanneer
                            // de gebruiker expliciet op Calculate drukt.
                            val calculatedPlan = withContext(Dispatchers.Default) {
                                stall.feedingPlan(horse, maxTier)
                            }

                            plan = calculatedPlan

                            if (calculatedPlan.isEmpty()) {
                                errorMessage =
                                    "No suitable feeding plan could be found."
                            }
                        } catch (e: Exception) {
                            e.printStackTrace()

                            plan = null
                            selectedItems = emptySet()

                            errorMessage =
                                e.message
                                    ?: "Could not calculate feeding plan."
                        } finally {
                            calculating = false
                        }
                    }
                },
                enabled = canCalculate
            ) {
                Text(
                    if (calculating) {
                        "Calculating..."
                    } else {
                        "Calculate feeding plan"
                    }
                )
            }

            Spacer(
                modifier = Modifier.height(10.dp)
            )

            Button(
                onClick = onBack
            ) {
                Text("Back")
            }
        }

        // =========================
        // RECHTERKANT
        // =========================

        Column(
            modifier = Modifier.padding(start = 30.dp)
        ) {
            Text("Feeding plan")

            if (plan == null) {
                Text("Select a horse and calculate a plan.")
            } else if (plan!!.isEmpty()) {
                Text("No suitable feeding plan could be found.")
            } else {

                // Alle items blijven zichtbaar.
                plan!!.forEachIndexed { index, material ->

                    val checked = index in selectedItems

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(vertical = 2.dp)
                    ) {
                        Checkbox(
                            checked = checked,
                            onCheckedChange = { isChecked ->

                                selectedItems =
                                    if (isChecked) {
                                        selectedItems + index
                                    } else {
                                        selectedItems - index
                                    }
                            }
                        )

                        Text(
                            text = material.name,
                            color = if (checked) {
                                Color.Gray
                            } else {
                                Color.Unspecified
                            },
                            modifier = Modifier.padding(start = 4.dp)
                        )
                    }
                }

                Spacer(
                    modifier = Modifier.height(10.dp)
                )

                // =========================
                // FEED BUTTON
                // =========================

                Button(
                    onClick = {
                    val horse = selectedHorse
                    val currentPlan = plan

                    if (horse != null && currentPlan != null) {

                        // Alleen de aangevinkte items ophalen
                        val selectedFood = currentPlan
                            .filterIndexed { index, _ ->
                                index in selectedItems
                            }

                        if (selectedFood.isNotEmpty()) {

                            // Deze items daadwerkelijk voeren
                            stall.executePlan(
                                horse,
                                selectedFood
                            )

                            // Bepaal wat er nog over is
                            val remainingPlan = currentPlan
                                .filterIndexed { index, _ ->
                                    index !in selectedItems
                                }

                            // Update het opgeslagen laatste plan van het paard
                            horse.latestPlan = remainingPlan

                            // Meteen opslaan naar JSON
                            StableRepository.save()

                            // Bericht tonen
                            val foodNames = selectedFood.joinToString(", ") {
                                it.name
                            }

                            feedMessage =
                                "${horse.name} has been fed: $foodNames"

                            // GUI bijwerken
                            plan = remainingPlan
                            selectedItems = emptySet()
                        }
                    }
                },
                    enabled = selectedItems.isNotEmpty()
                ) {
                    Text("Feed")
                }

                // =========================
                // FEED MELDING
                // =========================

                feedMessage?.let { message ->

                    Spacer(
                        modifier = Modifier.height(10.dp)
                    )

                    Text(
                        text = message,
                        color = Color(0xFF388E3C)
                    )
                }
            }

        }
    }
}
