import kotlin.collections.component1
import kotlin.collections.component2

class FeedingPlanner(
    private val materials: List<Material>
) {

    fun calculatePlan(horse: Horse, maxTier: Int): List<Material> {
        // maxTier = 0 means no limit
        val bestMaterials = materials
            .filter { it.tier <= horse.highestStat() && (maxTier == 0 || it.tier <= maxTier) }
            .let { filtered ->
                val max = filtered.maxBy { it.tier }
                filtered.filter { it.tier == max.tier}
            }
        return calculatePlanBfs(horse, bestMaterials)
    }

    private fun calculatePlanBfs(horse: Horse, materials: List<Material>): List<Material> {
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

/** Returns the highest stat level of the horse */
fun Horse.highestStat(): Int {
    return stats.values.maxOf { it.level }
}