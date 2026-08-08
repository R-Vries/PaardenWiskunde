package domain.stable

import data.MaterialRepository.materials
import domain.horse.Horse
import domain.horse.Stat
import domain.horse.StatField
import domain.horse.UpdateResult
import domain.material.FeedValidation
import domain.material.Material
import domain.stat.StatType
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import planner.FeedingPlanner

/**
 * Stall containing multiple horses. Contains functions for all operations related to horses.
 *
 * @property name The name of the stall.
 * @property horses A mutable list of horses in the stall.
 * @property feedingSlots The number of feeding slots available in the stall.
 */
@Serializable
class Stall(
    val name: String,
    private val horses: MutableList<Horse> = mutableListOf(),
    val feedingSlots: Int = 5
) {
    /** Getter for the number of horses in the stall */
    val horseCount: Int
        get() = horses.size

    @Transient
    val feedingPlanner = FeedingPlanner(materials)

    /**
     * Calculates a feeding plan for a given horse based on the provided maximum tier.
     *
     * @param horse The horse for which the feeding plan is to be calculated.
     * @param maxTier The maximum tier of the food that should be included in the plan.
     * @return A list of materials that should be fed to the horse to max out its limits.
     */
    fun feedingPlan(horse: Horse, maxTier: Int): List<Material> =
        feedingPlanner.calculatePlan(horse, maxTier)
            .let { list -> list.sortedBy { material -> list.indexOf(material)}}

    /**
     * Adds a new horse to this stall
     */
    fun addHorse(horse: Horse) {
        // no limit on the number of horses, since horses can also be tracked without having them in a stall in Wynn
        horses.add(horse)
    }

    /**
     * Removes a horse from the stall.
     */
    fun removeHorse(horse: Horse) {
        horses.remove(horse)
    }

    /**
     * Renames a horse in the stall.
     */
    fun renameHorse(horse: Horse, newName: String) {
        horse.rename(newName)
    }

    /**
     * Executes a feeding plan by feeding the specified horse with the provided materials.
     *
     * @param horse The horse to be fed.
     * @param plan The list of materials to be fed to the horse.
     */
    fun executePlan(horse: Horse, plan: List<Material>) = plan.forEach { horse.feed(it)}

    /**
     * Adds a new horse with the specified name and statistical attributes to the collection of horses.
     *
     * @param name The name of the horse to be added.
     * @param stats A map where each key represents a type of stat (e.g., SPEED, ENERGY),
     * and the corresponding value indicates the detailed properties of that stat.
     */
    fun addHorse(name: String, stats: Map<StatType, Stat>) =
        addHorse(Horse(name, stats.toMutableMap()))

    fun updateHorseStat(
        horse: Horse,
        type: StatType,
        field: StatField,
        value: Int
    ): UpdateResult {
        if (horse !in horses) {
            return UpdateResult.Error("domain.horse.Horse does not belong to this stall.")
        }

        val stat = horse.stats[type]
            ?: return UpdateResult.Error("domain.horse.Stat not found.")

        return when (field) {
            StatField.LEVEL -> stat.updateLevel(value)
            StatField.LIMIT -> stat.updateLimit(value)
            StatField.MAX -> stat.updateMax(value)
        }
    }

    /**
     * Gets the horse at the specified index.
     * @param index The index of the horse to retrieve.
     * @throws IndexOutOfBoundsException if the index is out of bounds.
     */
    fun get(index: Int): Horse = horses[index]

    override fun toString(): String {
        return horses.mapIndexed { index, horse ->
            "${index + 1}. $horse"
        }.joinToString("\n")
    }

    /**
     * Validates the amount of food to be fed. Checks whether the amount is between 0 and the available food from the plan.
     * Warns the user if the amount exceeds the feeding slots.
     * @param amount The amount of food to be fed.
     * @param availableFood The available number of food items from the feeding plan.
     * @return domain.material.FeedValidation.Valid if the amount is valid, domain.material.FeedValidation.Warning if the amount exceeds the feeding slots, domain.material.FeedValidation.Invalid otherwise.
     */
    fun validateFeedAmount(amount: Int, availableFood: Int): FeedValidation {
        return when {
            amount < 0 ->
                FeedValidation.Invalid("Amount must be positive")
            amount > availableFood ->
                FeedValidation.Invalid("Not enough food available")
            amount > feedingSlots ->
                FeedValidation.Warning("You can only fit $feedingSlots items in the feeding slots at once.")
            else -> FeedValidation.Valid
        }
    }
}