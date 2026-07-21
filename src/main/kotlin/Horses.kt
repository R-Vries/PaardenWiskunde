import kotlinx.serialization.Serializable

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
}

@Serializable
data class Stat(
    val level: Int,
    var limit: Int,
    val max: Int
) {

    /** Increase the limit by the amount specified, up until the maximum */
    fun increase(amount: Int) {
        limit = (limit + amount).coerceAtMost(max)
    }
}


