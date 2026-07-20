import kotlinx.serialization.json.Json
import java.io.File

class TUI(val calculator: FeedingPlanner) {

    private val horses = mutableListOf<Horse>()
    private val saveFile = File("horses.json")
    private val json = Json{
        prettyPrint = true
        encodeDefaults = true
    }

    fun start() {
        loadHorses()
        while (true) {
            println("")
            println("=== Horse Maths ===")
            println("1. Inspect horses")
            println("2. Add horse")
            println("3. Remove horse")
            println("4. Calculate feeding plan")
            println("0. Exit")

            when (readlnOrNull()?.trim()) {
                "1" -> showHorses()
                "2" -> addHorse()
                "3" -> removeHorse()
                "4" -> feedingPlan()
                "0" -> {
                    saveHorses()
                    println("Exiting...")
                    return
                }
                else -> println("Invalid option")
            }
        }
    }

    private fun showHorses() {
        println("=== Horses ===")
        horses.forEachIndexed { index, horse ->
            println("${index + 1}. $horse")
        }
    }

    private fun showNames() {
        horses.forEachIndexed { index, horse ->
            println("${index + 1}. ${horse.name}") }
    }

    private fun addHorse() {
        println("Enter horse name:")
        val name = readlnOrNull()?.trim().orEmpty().ifEmpty { "Horse #${horses.size + 1}" }

        println("Press 1 for default stats, 2 for custom stats")
        when (readlnOrNull()?.trim()) {
            "1" -> horses.add(Horse(name))
            "2" -> {
                val speed = inputStat("Speed")
                val acceleration = inputStat("Acceleration")
                val altitude = inputStat("Altitude")
                val energy = inputStat("Energy")
                val handling = inputStat("Handling")
                val toughness = inputStat("Toughness")
                val boost = inputStat("Boost")
                val training = inputStat("Training")
                horses.add(Horse(name, mutableMapOf(
                    StatType.SPEED to speed,
                    StatType.ACCELERATION to acceleration,
                    StatType.ALTITUDE to altitude,
                    StatType.ENERGY to energy,
                    StatType.HANDLING to handling,
                    StatType.TOUGHNESS to toughness,
                    StatType.BOOST to boost,
                    StatType.TRAINING to training
                )))
            }
        }
    }

    private fun inputStat(name: String): Stat {
        println("Enter $name level (empty = 1):")
        val level = readlnOrNull()?.trim()
            ?.takeIf { it.isNotEmpty() }
            ?.toIntOrNull()
            ?: 1

        val limit = inputRequiredInt("Enter $name limit:")
        val max = inputRequiredInt("Enter $name max:")

        return Stat(
            level = level,
            limit = limit,
            max = max
        )
    }

    private fun inputRequiredInt(prompt: String): Int {
        while (true) {
            println(prompt)

            val value = readlnOrNull()
                ?.trim()
                ?.toIntOrNull()

            if (value != null) {
                return value
            }

            println("Please enter a valid number.")
        }
    }

    /** 1-indexed remove function */
    private fun removeHorse() {
        println("Enter horse index to remove:")
        val index = readlnOrNull()?.trim()?.toIntOrNull() ?: return
        if (index in 1..horses.size) {
            horses.removeAt(index - 1)
            println("Horse #$index removed")
        } else {
            println("Invalid index")
        }
    }

    private fun feedingPlan() {
        println("=== Feeding Plan ===")
        println("Which horse do you want the feeding plan for?")
        showNames()

        val index = readlnOrNull()?.trim()?.toIntOrNull() ?: return
        val plan: List<Material> = calculator.calculatePlan(horses[index - 1], "greedy")
            .let { list -> list.sortedBy { material -> list.indexOf(material)}}

        println("Feeding plan for ${horses[index - 1].name}:")
        val formattedPlan = formatPlan(plan)
        println(formattedPlan)

        val amount = askFeedAmount(plan.size)
        val selectedFood = plan.take(amount)
            .joinToString(", ") { it.name }
            .ifEmpty { "nothing" }

        println("Feeding $selectedFood to ${horses[index - 1].name}")
        executePlan(horses[index - 1], plan.take(amount))
    }

    private fun askFeedAmount(maxAmount: Int): Int {
        while (true) {
            println("How many items do you want to feed?")
            val amount = readlnOrNull()?.trim()?.toIntOrNull()

            if (amount != null && amount in 0..maxAmount.coerceAtMost(5)) {
                return amount
            }
            println("Please enter a valid number between 0 and ${maxAmount.coerceAtMost(5)}.")
        }
    }

    private fun loadHorses() {
        if (!saveFile.exists()) {
            println("No saved horses found, creating new file")
            return
        }
        try {
            val savedHorses: List<Horse> = Json.decodeFromString(saveFile.readText())
            horses.clear()
            horses.addAll(savedHorses)
        } catch (e: Exception) {
            println("Could not load horses: ${e.message}")
        }
    }

    private fun saveHorses() {
        saveFile.writeText(json.encodeToString(horses))
        println("${horses.size} horses saved to horses.json")
    }
}