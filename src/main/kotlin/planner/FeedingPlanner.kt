package planner

import domain.horse.Horse
import domain.material.Material
import kotlin.collections.component1
import kotlin.collections.component2

class FeedingPlanner(
    private val materials: List<Material>
) {

    var lastSearchStats = SearchStats()
    var algorithm = SearchAlgorithm.ASTAR

    fun calculatePlan(horse: Horse, maxTier: Int): List<Material> {
        val stats = SearchStats()
        // maxTier = 0 means no limit
        val bestMaterials = materials
            .filter { it.tier <= horse.highestStat() && (maxTier == 0 || it.tier <= maxTier) }
            .let { filtered ->
                val max = filtered.maxBy { it.tier }
                filtered.filter { it.tier == max.tier }
            }
        val plan = when (algorithm) {
            SearchAlgorithm.BFS -> calculatePlanBfs(horse, bestMaterials, stats)
            SearchAlgorithm.ASTAR -> calculatePlanAStar(horse, bestMaterials, stats)
        }
        lastSearchStats = stats
        return plan
    }
}

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