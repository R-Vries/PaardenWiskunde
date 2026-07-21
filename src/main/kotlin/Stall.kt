import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

@Serializable
class Stall(
    val name: String,
    private val horses: MutableList<Horse> = mutableListOf()
) {
    val horseCount: Int
        get() = horses.size

    @Transient
    private val calculator = FeedingPlanner(MaterialRepository.materials)

    fun setMaterialTier(tier: Int): String? =
        calculator.setMaterialTier(tier)

    fun feedingPlan(horse: Horse): List<Material> =
        calculator.calculatePlan(horse)
            .let { list -> list.sortedBy { material -> list.indexOf(material)}}

    fun executePlan(horse: Horse, plan: List<Material>) = plan.forEach { horse.feed(it)}

    //ugly that this gets a horse. Maybe an index is better? Causes changes everywhere that selectHorse is used...
    fun levelHorse(horse: Horse, newLevels: Map<StatType, Int>): List<String> =
        horse.levelUp(newLevels)

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
}