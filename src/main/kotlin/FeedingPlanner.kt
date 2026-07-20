class FeedingPlanner(
    private val materials: List<Material>
) {

    fun chooseBestMaterial(statType: StatType): Material {
        return materials.maxBy {
            it.gainFor(statType)
        }
    }

    /** Greedy approach to calculate the best feeding plan */
    fun calculatePlan(horse: Horse): List<Material> {
        val plan = mutableListOf<Material>()
        val horseCopy = horse.deepCopy()

        while (!horseCopy.isMaxed()) {
            val targetStat = horseCopy.lowestStat()
            val food = chooseBestMaterial(targetStat)
            plan.add(food)
            horseCopy.feed(food)
        }

        return plan.toList()
    }
}

fun executePlan(horse: Horse, plan: List<Material>) = plan.forEach { horse.feed(it)}

fun Horse.deepCopy(): Horse {
    return copy(
        stats = stats.mapValues { (_, stat) ->
            stat.copy()
        }.toMutableMap()

    )
}