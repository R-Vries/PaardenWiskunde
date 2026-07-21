import java.io.File
import kotlin.collections.take
import kotlin.text.ifEmpty

class TUI(val stall: Stall) {

    private val saveFile = File(AppConfig.dataDirectory, "horses.json")

    fun start() {
        println(
            if (AppConfig.isDevelopment)
                "Running in DEVELOPMENT mode"
            else
                "Running in PRODUCTION mode"
        )
        loadHorses()
        while (true) {
            println("")
            println("========= Horse Maths =========")
            println("1. Inspect horses")
            println("2. Add horse")
            println("3. Remove horse")
            println("4. Calculate feeding plan")
            println("5. Rename horse")
            println("6. Level up horse")
            println("7. Set material level limit")
            println("0. Exit")

            when (readlnOrNull()?.trim()) {
                "1" -> showHorses()
                "2" -> addHorse()
                "3" -> removeHorse()
                "4" -> feedingPlan()
                "5" -> renameHorse()
                "6" -> levelHorse()
                "7" -> setMaterialLevel()
                "0" -> {
                    saveHorses()
                    println("Exiting...")
                    return
                }
                else -> println("Invalid option")
            }
        }
    }

    private fun setMaterialLevel() {
        //TODO do something with the return value
        stall.setMaterialTier(inputRequiredInt("Enter material tier:"))
    }

    private fun showHorses() {
        println("========= Horses =========")
        println(stall)
    }

    private fun addHorse() {
        println("Enter horse name:")
        val name = readlnOrNull()?.trim().orEmpty().ifEmpty { "Horse #${stall.horseCount + 1}" }

        println("Press 1 for default stats, 2 for custom stats")
        when (readlnOrNull()?.trim()) {
            "1" -> stall.add(Horse(name))
            "2" -> {
                val speed = inputStat("Speed")
                val acceleration = inputStat("Acceleration")
                val altitude = inputStat("Altitude")
                val energy = inputStat("Energy")
                val handling = inputStat("Handling")
                val toughness = inputStat("Toughness")
                val boost = inputStat("Boost")
                val training = inputStat("Training")
                stall.add(Horse(name, mutableMapOf(
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

    private fun selectHorse(): Horse? {
        while (true) {
            showHorses()
            println("Enter horse index: (enter to go back)")
            val index = readlnOrNull()?.trim()?.toIntOrNull() ?: return null
            if (index !in 1..stall.horseCount) {
                println("Invalid index, try again")
                continue
            }
            return stall.get(index - 1)
        }
    }

    private fun renameHorse() {
        val horse = selectHorse()
        println("Enter the new name: ")
        horse?.rename(readlnOrNull()?.trim().orEmpty().ifEmpty { horse.name })
    }

    private fun levelHorse() {
        println("=== Level up horse ===")
        val horse = selectHorse()?: return
        val newLevels = horse.stats.keys.associateWith { type ->
            inputRequiredInt("What is the new ${type.name} level?")
        }
        stall.levelHorse(horse, newLevels)
        //TODO display error message if level up failed
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
        val horse = selectHorse()?: return
        stall.remove(horse)
    }

    private fun feedingPlan() {
        val horse = selectHorse()?: return
        val plan = stall.feedingPlan(horse)

        println("Feeding plan for ${horse.name}:")
        val formattedPlan = formatPlan(plan)
        println(formattedPlan)

        val amount = askFeedAmount(plan.size)
        val selectedFood = plan.take(amount)
            .joinToString(", ") { it.name }
            .ifEmpty { "nothing" }

        println("Feeding $selectedFood to ${horse.name}")
        stall.executePlan(horse, plan.take(amount))
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
            stall.loadHorses(saveFile.readText())
        } catch (e: Exception) {
            println("Could not load horses: ${e.message}")
        }
    }

    private fun saveHorses() {
        saveFile.writeText(stall.getJson())
        println("${stall.horseCount} horses saved to ${saveFile.absolutePath}")
    }
}