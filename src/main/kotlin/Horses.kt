import kotlinx.serialization.Serializable
import kotlin.collections.get

val defaultStats = StatType.entries.associateWith {
    Stat(1, 10, 30)
}.toMutableMap()

@Serializable
data class Horse(
    var name: String,
    val stats: MutableMap<StatType, Stat> = defaultStats
) {
    /** Increase each stat's limit by the amount specified by the material */
    fun feed(material: Material) {
        material.stats.forEach { (type, amount) ->
            stats[type]?.increase(amount)
        }
    }

    fun lowestStat(): StatType =
        stats.maxBy { (_, stat) -> stat.max - stat.limit }.key

    fun isMaxed(): Boolean = stats.all { (_, stat) -> stat.limit == stat.max }


    override fun toString(): String {
        return buildString {
            appendLine(name)

            stats.forEach { (type, stat) ->
                appendLine(
                    "    ${type.name.lowercase().replaceFirstChar { it.uppercase() }}: ".padEnd(20) +
                            "${stat.level}/${stat.limit}/${stat.max}"
                )
            }
        }.trimIndent()
    }

    fun rename(newName: String) {
        name = newName
    }

    /**
     * Increases the levels of specific stats for the horse based on the provided mapping.
     *
     * @param newLevels A map where the key is the type of stat and the value is the new level to be set for that stat.
     * @return A list of messages reporting the results of each stat's level up. No errors lead to an empty list of messages.
     */
    fun levelUp(newLevels: Map<StatType, Int>): List<String> {
        return newLevels.mapNotNull { (type, level) ->
            stats[type]?.levelUp(level)?.let { error ->
                "$type: $error"
            }
        }
    }
}

@Serializable
data class Stat(
    var level: Int,
    var limit: Int,
    val max: Int
) {

    /** Increase the limit by the amount specified, up until the maximum */
    fun increase(amount: Int) {
        limit = (limit + amount).coerceAtMost(max)
    }

    fun levelUp(newLevel: Int): String? {
        return when {
            newLevel < 1 -> {
                level = 1
                "Level must be at least 1. Level has been set to 1."
            }
            newLevel > limit -> {
                level = limit
                "Level $newLevel is higher than the limit ($limit). Level has been set to $limit."
            }
            else -> {
                level = newLevel
                null
            }
        }
    }
}


