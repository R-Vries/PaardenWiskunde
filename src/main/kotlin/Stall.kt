import kotlinx.serialization.json.Json

class Stall(val calculator: FeedingPlanner) {
    private val horses = mutableListOf<Horse>()
    var horseCount = 0

    private val json = Json{
        prettyPrint = true
        encodeDefaults = true
    }

    fun setMaterialTier(tier: Int): String? =
        calculator.setMaterialTier(tier)


    fun feedingPlan(horse: Horse): List<Material> =
        calculator.calculatePlan(horse)
            .let { list -> list.sortedBy { material -> list.indexOf(material)}}

    fun executePlan(horse: Horse, plan: List<Material>) = plan.forEach { horse.feed(it)}

    //TODO ugly that this gets a horse. Maybe an index is better? Causes changes everywhere that selectHorse is used...
    fun levelHorse(horse: Horse, newLevels: Map<StatType, Int>): List<String> =
        horse.levelUp(newLevels)

    fun getJson(): String = json.encodeToString(horses)

    fun loadHorses(json: String) {
        horses.clear()
        horses.addAll(Json.decodeFromString(json))
        horseCount = horses.size
    }

    fun add(horse: Horse) {
        horses.add(horse)
        horseCount++
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
        horseCount--
    }

    fun get(index: Int): Horse = horses[index]

    override fun toString(): String {
        return horses.mapIndexed { index, horse ->
            "${index + 1}. $horse"
        }.joinToString("\n")
    }
}