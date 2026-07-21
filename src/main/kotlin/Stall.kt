import kotlinx.serialization.json.Json

class Stall(var materialTier: Int, val calculator: FeedingPlanner) {
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

    fun levelHorse(horse: Horse, newLevels: Map<StatType, Int>) =
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