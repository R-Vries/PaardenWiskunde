import kotlinx.serialization.json.Json

object Stable {
    val stalls = mutableListOf<Stall>()
    private val json = Json {
        prettyPrint = true
        encodeDefaults = true
    }

    fun addStall(name: String): Stall {
        val stall = Stall(name)
        stalls.add(Stall(name))
        return stall
    }

    fun import(json: String) {
        // import the stalls from json (a list of stalls which are lists of horses)
        stalls.addAll(Json.decodeFromString<List<Stall>>(json))
    }

    fun getJson(): String = json.encodeToString(stalls)

    fun getStall(index: Int): Stall = stalls[index]
}