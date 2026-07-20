data class Material(
    val name: String,
    val stats: Map<StatType, Int>
)

enum class StatType {
    SPEED, ACCELERATION, ALTITUDE, ENERGY, HANDLING, TOUGHNESS, BOOST, TRAINING
}

val copperIngot = Material(
    "Copper Ingot",
    mapOf(
        StatType.ENERGY to 4,
        StatType.TOUGHNESS to 8
    )
)

val copperGem = Material(
    "Copper Gem",
    mapOf(
        StatType.SPEED to 4,
        StatType.ENERGY to 2,
        StatType.TRAINING to 6
    )
)

val oakPlank = Material(
    "Oak Plank",
    mapOf(
        StatType.SPEED to 2,
        StatType.ACCELERATION to 6,
        StatType.TOUGHNESS to 4
    )
)

val oakPaper = Material(
    "Oak Paper",
    mapOf(
        StatType.ALTITUDE to 8,
        StatType.BOOST to 4
    )
)

val wheatString = Material(
    "Wheat String",
    mapOf(
        StatType.ACCELERATION to 2,
        StatType.HANDLING to 4,
        StatType.BOOST to 6
    )
)

val wheatGrains = Material(
    "Wheat Grains",
    mapOf(
        StatType.SPEED to 8,
        StatType.ALTITUDE to 4
    )
)

val gudgeonOil = Material(
    "Gudgeon Oil",
    mapOf(
        StatType.ALTITUDE to 2,
        StatType.HANDLING to 6,
        StatType.TRAINING to 4
    )
)

val gudgeonMeat = Material(
    "Gudgeon Meat",
    mapOf(
        StatType.ACCELERATION to 4,
        StatType.ENERGY to 8
    )
)


