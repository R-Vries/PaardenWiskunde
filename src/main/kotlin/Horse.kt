import kotlinx.serialization.Serializable

@Serializable
data class Horse(
    var name: String,
    val stats: MutableMap<StatType, Stat> = StatType.entries.associateWith {
        Stat(1, 10, 30)
    }.toMutableMap()
) {
    val potency: Int = stats.values.sumOf { it.max }
    /** Increase each stat's limit by the amount specified by the material */
    fun feed(material: Material) {
        material.stats.forEach { (type, amount) ->
            stats[type]?.increaseLimit(amount)
        }
    }

    fun isMaxed(): Boolean = stats.all { (_, stat) -> stat.limit == stat.max }

    override fun toString(): String {
        return buildString {
            appendLine("$name ($potency) ${if (isMaxed()) "(max)" else ""}")

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
//kaas=sus

