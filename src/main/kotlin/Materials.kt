import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import java.io.File

@Serializable
data class Material(
    val name: String,
    val tier: Int,
    val stats: Map<StatType, Int>
)

fun Material.gainFor(stat: StatType): Int = stats[stat] ?: 0

enum class StatType {
    SPEED, ACCELERATION, ALTITUDE, ENERGY, HANDLING, TOUGHNESS, BOOST, TRAINING
}

val materials: List<Material> = Json.decodeFromString<List<Material>>(File("materials.json").readText())

