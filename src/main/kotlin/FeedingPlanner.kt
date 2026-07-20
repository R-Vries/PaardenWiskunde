import kotlin.collections.component1
import kotlin.collections.component2

class FeedingPlanner(
    private val materials: List<Material>
) {

    fun chooseBestMaterial(statType: StatType): Material {
        return materials.maxBy {
            it.gainFor(statType)
        }
    }

    /** Greedy approach to calculate the best feeding plan */
    private fun calculatePlanGreedy(horse: Horse): List<Material> {
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

    fun calculatePlan(horse: Horse, strategy: String = "greedy"): List<Material> {
        return when (strategy) {
            "greedy" -> calculatePlanGreedy(horse)
            "bfs" -> calculatePlanBfs(horse)
            else -> throw IllegalArgumentException("Unknown strategy: $strategy")
        }
    }

    private fun calculatePlanBfs(horse: Horse): List<Material> {
        val queue = ArrayDeque<PlanNode>()
        val visited = mutableSetOf<String>()   // store visited nodes as hashes

        queue.add(PlanNode(horse.deepCopy(), emptyList()))

        while (queue.isNotEmpty()) {
            val current = queue.removeFirst()

            if (current.horse.isMaxed()) return current.plan

            val hash = current.horse.stateHash()
            if (!visited.add(hash)) continue

            for (food in materials) {
                val next = current.horse.deepCopy()
                next.feed(food)

                queue.add(PlanNode(next, current.plan + food))
            }
        }
        return emptyList()
    }

}

/** A node in the plan graph, containing the current stats of the horse and the list of materials fed */
private data class PlanNode(
    val horse: Horse,
    val plan: List<Material>
)

fun executePlan(horse: Horse, plan: List<Material>) = plan.forEach { horse.feed(it)}

fun formatPlan(plan: List<Material>): String =
    plan
        .groupingBy { it.name }
        .eachCount()
        .entries
        .joinToString("\n") { (name, count) ->
            "${count}x $name"
        }

fun Horse.deepCopy(): Horse {
    return copy(
        stats = stats.mapValues { (_, stat) ->
            stat.copy()
        }.toMutableMap()

    )
}

fun Horse.stateHash(): String {
    return stats.values
        .joinToString(",") { stat ->
            stat.limit.toString()
        }
}