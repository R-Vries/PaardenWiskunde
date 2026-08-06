import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

@Serializable
class Stall(
    val name: String,
    private val horses: MutableList<Horse> = mutableListOf(),
    val feedingSlots: Int = 5
) {
    val horseCount: Int
        get() = horses.size

    @Transient
    val calculator = FeedingPlanner(MaterialRepository.materials)

    fun feedingPlan(horse: Horse, maxTier: Int): List<Material> =
        calculator.calculatePlan(horse, maxTier)
            .let { list -> list.sortedBy { material -> list.indexOf(material)}}

    fun executePlan(horse: Horse, plan: List<Material>) = plan.forEach { horse.feed(it)}

    fun add(horse: Horse) {
        horses.add(horse)
    }

    /**
     * Adds a new horse with the specified name and statistical attributes to the collection of horses.
     *
     * @param name The name of the horse to be added.
     * @param stats A map where each key represents a type of stat (e.g., SPEED, ENERGY),
     * and the corresponding value indicates the detailed properties of that stat.
     */
    fun add(name: String, stats: Map<StatType, Stat>) =
        add(Horse(name, stats.toMutableMap()))

    fun remove(horse: Horse) {
        horses.remove(horse)
    }

    fun get(index: Int): Horse = horses[index]

    override fun toString(): String {
        return horses.mapIndexed { index, horse ->
            "${index + 1}. $horse"
        }.joinToString("\n")
    }

    fun validateFeedAmount(amount: Int, availableFeed: Int): FeedValidation {
        return when {
            amount < 0 ->
                FeedValidation.Invalid("Amount must be positive")
            amount > availableFeed ->
                FeedValidation.Invalid("Not enough food available")
            amount > feedingSlots ->
                FeedValidation.Warning("You can only fit $feedingSlots items in the feeding slots at once.")
            else -> FeedValidation.Valid
        }
    }
}


sealed interface FeedValidation {
    data object Valid : FeedValidation
    data class Warning(val message: String) : FeedValidation
    data class Invalid(val message: String) : FeedValidation
}