package planner

import domain.horse.Horse
import domain.material.Material

fun calculatePlanBfs(horse: Horse, materials: List<Material>, stats: SearchStats): List<Material> {
    val queue = ArrayDeque<PlanNode>()
    val visited = mutableSetOf<String>()   // store visited nodes as hashes

    queue.add(PlanNode(horse.deepCopy(), emptyList()))

    stats.maxQueueSize = queue.size

    while (queue.isNotEmpty()) {
        val current = queue.removeFirst()

        if (current.horse.isMaxed()) return current.plan

        val hash = current.horse.stateHash()
        if (!visited.add(hash)) {
            stats.duplicateStates++
            continue
        }

        stats.expandedStates++

        for (food in materials) {
            val next = current.horse.deepCopy()
            next.feed(food)

            stats.generatedStates++

            val nextHash = next.stateHash()
            if (nextHash == hash) {
                stats.duplicateStates++
                continue
            }

            queue.add(PlanNode(next, current.plan + food))

            stats.maxQueueSize = maxOf(
                stats.maxQueueSize,
                queue.size
            )
        }
    }
    return emptyList()
}

/** A node in the plan graph, containing the current stats of the horse and the list of materials fed */
private data class PlanNode(
    val horse: Horse,
    val plan: List<Material>
)