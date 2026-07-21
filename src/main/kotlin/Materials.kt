import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

@Serializable
data class Material(
    val name: String,
    val tier: Int,
    val stats: Map<StatType, Int>
)

enum class StatType {
    SPEED, ACCELERATION, ALTITUDE, ENERGY, HANDLING, TOUGHNESS, BOOST, TRAINING
}

object MaterialRepository {
    val materials = loadMaterials()

    val maxTier = materials.maxOf { it.tier }

    private fun loadMaterials(): List<Material> = Json.decodeFromString<List<Material>>(
        object {}.javaClass
            .getResourceAsStream("/materials.json")!!
            .bufferedReader()
            .readText()
    )
}