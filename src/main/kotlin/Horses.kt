import kotlinx.serialization.Serializable

fun defaultStat() = Stat(0, 10, 30)

@Serializable
data class Horse(
    val name: String,
    val speed: Stat = defaultStat(),
    val acceleration: Stat = defaultStat(),
    val altitude: Stat = defaultStat(),
    val energy: Stat = defaultStat(),
    val handling: Stat = defaultStat(),
    val toughness: Stat = defaultStat(),
    val boost: Stat = defaultStat(),
    val training: Stat = defaultStat()
) {
    /** Increase each stat's limit by the amount specified by the material */
    fun feed(material: Material) {
        speed.increase(material.speed)
        acceleration.increase(material.acceleration)
        altitude.increase(material.altitude)
        energy.increase(material.energy)
        handling.increase(material.handling)
        toughness.increase(material.toughness)
        boost.increase(material.boost)
        training.increase(material.training)
    }

    override fun toString(): String {
        return """    $name
        Speed:        ${speed.level}/${speed.limit}/${speed.max}
        Acceleration: ${acceleration.level}/${acceleration.limit}/${acceleration.max}
        Altitude:     ${altitude.level}/${altitude.limit}/${altitude.max}
        Energy:       ${energy.level}/${energy.limit}/${energy.max}
        Handling:     ${handling.level}/${handling.limit}/${handling.max}
        Toughness:    ${toughness.level}/${toughness.limit}/${toughness.max}
        Boost:        ${boost.level}/${boost.limit}/${boost.max}
        Training:     ${training.level}/${training.limit}/${training.max}
    """.trimIndent()
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