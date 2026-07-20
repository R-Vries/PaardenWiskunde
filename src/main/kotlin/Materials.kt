data class Material(
    val name: String,
    val speed: Int = 0,
    val acceleration: Int = 0,
    val altitude: Int = 0,
    val energy: Int = 0,
    val handling: Int = 0,
    val toughness: Int = 0,
    val boost: Int = 0,
    val training: Int = 0
)

val copperIngot = Material("Copper Ingot", energy=4, toughness=8)
val copperGem = Material("Copper Gem", speed=4, energy=2, training=6)
val oakPlank = Material("Oak Plank", speed=2, acceleration=6, toughness=4)
val oakPaper = Material("Oak Paper", altitude=8, boost=4)
val wheatString = Material("Wheat String", acceleration=2, handling=4, boost=6)
val wheatGrains = Material("Wheat Grains", speed=8, altitude=4)
val gudgeonOil = Material("Gudgeon Oil", altitude=2, handling=6, training=4)
val gudgeonMeat = Material("Gudgeon Meat", acceleration=4, energy=8)

