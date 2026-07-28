import kotlin.collections.take
import kotlin.text.ifEmpty

object TUI {
    fun start() {
        println(
            if (AppConfig.isDevelopment)
                "Running in DEVELOPMENT mode"
            else
                "Running in PRODUCTION mode"
        )
        StableRepository.load()
        while (true) {
            val stall = selectStall() ?: break
            stallMenu(stall)
        }
        StableRepository.save()
        println("Exiting...")
    }

    private fun selectStall(): Stall? {
        printHeader("PaardenWiskunde")

        Stable.stalls.forEachIndexed { index, stall ->
            println("${index + 1}. ${stall.name}")
        }

        println("${Stable.stalls.size + 1}. Add new stall")
        println("0. Exit")

        return when (val choice = inputRequiredInt("Select stall:")) {
            0 -> null
            Stable.stalls.size + 1 -> {
                println("Enter stall name:")
                val name = readlnOrNull()?.trim()?: "Stable #${Stable.stalls.size + 1}"
                Stable.addStall(name)
            }
            in 1..Stable.stalls.size -> Stable.getStall(choice - 1)
            else -> {
                println("Invalid Option")
                selectStall()
            }
        }
    }

    private fun stallMenu(stall: Stall) {
        while (true) {
            printHeader("${stall.name}'s Stall")
            println("1. Inspect horses")
            println("2. Calculate feeding plan")
            println("3. Add horse")
            println("4. Remove horse")
            println("5. Rename horse")
            println("6. Level up horse")
            println("0. Back to stall selection")

            val choice = inputRequiredInt("Select option:")
            when (choice) {
                1 -> showHorses(stall)
                2 -> feedingPlan(stall)
                3 -> addHorse(stall)
                4 -> removeHorse(stall)
                5 -> renameHorse(stall)
                6 -> levelHorse(stall)
                0 -> return

                else -> println("Invalid option")

            }
        }
    }

    private fun showHorses(stall: Stall) {
        printHeader("${stall.name}'s Horses")
        if (stall.horseCount == 0) {
            println("No horses in this stall")
            return
        }
        println(stall)
    }

    private fun feedingPlan(stall: Stall) {
        val horse = selectHorse(stall)?: return
        val maxTier = inputRequiredInt("Enter max tier (0 for highest possible):")
        val plan = stall.feedingPlan(horse, maxTier)

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

    private fun addHorse(stall: Stall) {
        println("Enter horse name:")
        val name = readlnOrNull()?.trim().orEmpty().ifEmpty { "Horse #${stall.horseCount + 1}" }

        println("Press 1 for default stats, 2 for custom stats")
        when (readlnOrNull()?.trim()) {
            "1" -> stall.add(Horse(name))
            "2" -> {
                val stats = StatType.entries.associateWith { type ->
                    inputStat(type.name.lowercase().replaceFirstChar { it.uppercase() })
                }
                stall.add(name, stats)
            }
        }
    }

    /** 1-indexed remove function */
    private fun removeHorse(stall: Stall) {
        val horse = selectHorse(stall)?: return
        stall.remove(horse)
    }

    private fun renameHorse(stall: Stall) {
        val horse = selectHorse(stall)
        println("Enter the new name: ")
        horse?.rename(readlnOrNull()?.trim().orEmpty().ifEmpty { horse.name })
    }

    private fun levelHorse(stall: Stall) {
        printHeader("Level up horse")
        val horse = selectHorse(stall)?: return
        val newLevels = horse.stats.keys.associateWith { type ->
            inputRequiredInt("What is the new ${type.name} level?")
        }
        stall.levelHorse(horse, newLevels).forEach { println(it) }
    }

    private fun selectHorse(stall: Stall): Horse? {
        while (true) {
            showHorses(stall)
            print("Enter horse index (enter to go back):")
            val index = readlnOrNull()?.trim()?.toIntOrNull() ?: return null
            if (index !in 1..stall.horseCount) {
                println("Invalid index, try again")
                continue
            }
            return stall.get(index - 1)
        }
    }

    private fun inputStat(name: String): Stat {
        print("Enter $name level (empty = 1): ")
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
            print("$prompt ")

            val value = readlnOrNull()
                ?.trim()
                ?.toIntOrNull()

            if (value != null) {
                return value
            }

            println("Please enter a valid number.")
        }
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

    private fun printHeader(title: String, width: Int = 40) {
        val padding = (width - title.length) / 2
        println("${"=".repeat(padding)} $title ${"=".repeat(width - padding - title.length)}")
    }
}