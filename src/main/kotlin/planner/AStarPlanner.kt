package planner

import domain.horse.Horse
import domain.material.Material
import domain.stat.StatType
import java.util.PriorityQueue

fun calculatePlanAStar(horse: Horse, materials: List<Material>, stats: SearchStats): List<Material> {
    if (horse.isMaxed()) return emptyList()

    val queue = PriorityQueue(
        compareBy<AStarNode> { it.estimatedTotalCost }
            .thenBy { it.cost }
    )

    val startHorse = horse.deepCopy()
    val startHash = startHorse.stateHash()

    queue.add(
        AStarNode(
            horse = startHorse,
            plan = emptyList(),
            cost = 0,
            estimatedTotalCost = heuristic(startHorse, materials)
        )
    )

    val bestCostByState = mutableMapOf(
        startHash to 0
    )

    while (queue.isNotEmpty()) {
        val current = queue.poll()
        stats.expandedStates++

        if (current.horse.isMaxed()) {
            return current.plan
        }

        for (material in materials) {
            val nextHorse = current.horse.deepCopy()
            nextHorse.feed(material)

            if (nextHorse.stateHash() == current.horse.stateHash()) {
                stats.duplicateStates++
                continue
            }

            val nextCost = current.cost + 1
            val nextHash = nextHorse.stateHash()

            val knownBestCost = bestCostByState[nextHash]

            if (knownBestCost != null && knownBestCost <= nextCost) {
                stats.duplicateStates++
                continue
            }

            bestCostByState[nextHash] = nextCost

            stats.generatedStates++

            queue.add(
                AStarNode(
                    horse = nextHorse,
                    plan = current.plan + material,
                    cost = nextCost,
                    estimatedTotalCost =
                        nextCost + heuristic(nextHorse, materials)
                )
            )

            stats.maxQueueSize = maxOf(stats.maxQueueSize, queue.size)
        }
    }

    return emptyList()
}

private fun heuristic(
    horse: Horse,
    materials: List<Material>
): Int {
    val totalRemaining = horse.stats.values.sumOf { stat ->
        (stat.max - stat.limit).coerceAtLeast(0)
    }

    val totalGainPerMaterial = materials.first()
        .stats
        .values
        .sum()

    val totalBound =
        (totalRemaining + totalGainPerMaterial - 1) / totalGainPerMaterial

    val statBound = StatType.entries.maxOf { type ->
        val stat = horse.stats.getValue(type)
        val remaining = (stat.max - stat.limit).coerceAtLeast(0)

        if (remaining == 0) {
            0
        } else {
            val maxGain = materials.maxOf { material ->
                material.stats[type] ?: 0
            }

            if (maxGain == 0) {
                Int.MAX_VALUE
            } else {
                (remaining + maxGain - 1) / maxGain
            }
        }
    }

    return maxOf(totalBound, statBound)
}


private data class AStarNode(
    val horse: Horse,
    val plan: List<Material>,
    val cost: Int,
    val estimatedTotalCost: Int
)