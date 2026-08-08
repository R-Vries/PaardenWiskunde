package domain.material

import domain.stat.StatType
import kotlinx.serialization.Serializable

@Serializable
data class Material(
    val name: String,
    val tier: Int,
    val stats: Map<StatType, Int>
)