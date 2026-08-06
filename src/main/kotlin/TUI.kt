import kotlin.collections.take
import kotlin.text.ifEmpty
import kotlin.time.measureTimedValue

object TUI {
    fun start() {
        if (AppConfig.isDevelopment) {
            println("Running in DEVELOPMENT mode")
        }
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
            println("6. Edit horse")
            println("0. Back to stall selection")

            val choice = inputRequiredInt("Select option:")
            when (choice) {
                1 -> showHorses(stall)
                2 -> feedingPlan(stall)
                3 -> addHorse(stall)
                4 -> removeHorse(stall)
                5 -> renameHorse(stall)
                6 -> editHorse(stall)
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

        val (plan, duration) = measureTimedValue {
            stall.feedingPlan(horse, maxTier)
        }

        if (AppConfig.isDevelopment) {
            with(stall.calculator.lastSearchStats) {
                println()
                println("=== Search statistics ===")
                println("Plan calculated in ${duration.inWholeMilliseconds} ms")
                println("Expanded states : $expandedStates")
                println("Generated      : $generatedStates")
                println("Duplicate      : $duplicateStates")
                println("Max queue size : $maxQueueSize")
                println()
            }
        }

        println("Feeding plan for ${horse.name}:")
        println(formatPlan(plan))

        val amount = askFeedAmount(stall, plan.size)
        val selectedFood = plan.take(amount)
            .joinToString(", ") { it.name }
            .ifEmpty { "nothing" }

        println("Feeding $selectedFood to ${horse.name}")
        stall.executePlan(horse, plan.take(amount))
    }

    private fun addHorse(stall: Stall) {
        val name = inputRequiredString("Enter horse name:").ifEmpty { "Horse #${stall.horseCount + 1}" }

        when (inputRequiredString("Press 1 for default stats, 2 for custom stats:")) {
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
        val newName = inputRequiredString("Enter the new name:")
        horse?.rename(newName.ifEmpty { horse.name })
    }

    private fun editHorse(stall: Stall) {
        val horse = selectHorse(stall)?: return

        while (true) {
            printHeader("Edit ${horse.name}")
            StatType.entries.forEachIndexed { index, type ->
                val stat = horse.stats.getValue(type)

                println(
                    "${index + 1}. ${type.name.padEnd(15)}" +
                            "${stat.level}/${stat.limit}/${stat.max}"
                )
            }
            println("0. Back")

            val choice = inputRequiredInt("Select stat:")

            if (choice == 0) return
            if (choice !in 1..StatType.entries.size) {
                println("Invalid option")
                continue
            }
            val type = StatType.entries[choice - 1]
            editStat(horse, type)
        }
    }

    private fun editStat(horse: Horse, type: StatType) {
        while (true) {
            val stat = horse.stats.getValue(type)

            println()
            println("=== ${type.name} ===")
            println("1. Level: ${stat.level}")
            println("2. Limit: ${stat.limit}")
            println("3. Max:   ${stat.max}")
            println("0. Back")

            val result = when (inputRequiredInt("Choose a value to edit:")) {
                1 -> stat.updateLevel(
                    inputRequiredInt("Enter new level:")
                )

                2 -> stat.updateLimit(
                    inputRequiredInt("Enter new limit:")
                )

                3 -> stat.updateMax(
                    inputRequiredInt("Enter new max:")
                )

                0 -> return

                else -> {
                    println("Invalid choice.")
                    continue
                }
            }

            when (result) {
                UpdateResult.Success ->
                    println("${type.name} updated.")

                is UpdateResult.Error ->
                    println("Could not update ${type.name}: ${result.message}")
            }
        }
    }

    private fun selectHorse(stall: Stall): Horse? {
        while (true) {
            showHorses(stall)
            val index = inputRequiredString("Enter horse index (enter to go back):")
                .trim()
                .toIntOrNull()
                ?: return null
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

    private fun inputRequiredString(prompt: String): String {
        print("$prompt ")
        return readlnOrNull()?.trim() ?: ""
    }

    private fun askFeedAmount(stall: Stall, availableFood: Int): Int {
        while (true) {
            val amount = inputRequiredInt("How many items do you want to feed?")

            when (val validation = stall.validateFeedAmount(amount, availableFood)) {
                FeedValidation.Valid -> return amount

                is FeedValidation.Invalid -> {
                    println(validation.message)
                }

                is FeedValidation.Warning -> {
                    println(validation.message)
                    if (inputRequiredString("Continue? (y/n)").lowercase() == "y") {
                        return amount
                    }
                }
            }
        }
    }

    private fun printHeader(title: String, width: Int = 40) {
        val padding = (width - title.length) / 2
        println("${"=".repeat(padding)} $title ${"=".repeat(width - padding - title.length)}")
    }
}