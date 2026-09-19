package domain.stable

import kotlinx.serialization.json.Json

/**
 * Stable containing multiple stalls.
 * Each stall could represent a different user/account, since a user can only have one stall.
 */
object Stable {
    val stalls = mutableListOf<Stall>()
    private val json = Json {
        prettyPrint = true
        encodeDefaults = true
    }

    /**
     * Adds a new stall to the stable.
     *
     * @param name The name of the stall to be added.
     * @return The newly created stall.
     */
    fun addStall(name: String): Stall {
        val stall = Stall(name)
        stalls.add(stall)
        return stall
    }

    /**
     * Removes a stall from the stable.
     *
     * @param stall The stall to be removed.
     * @return true if the stall was removed, false otherwise (e.g., if the stall was not found in the stable).
     */
    fun removeStall(stall: Stall): Boolean {
        return stalls.remove(stall)
    }

    /** Import a stable from a JSON string of stalls */
    fun import(json: String) {
        // import the stalls from JSON (a list of stalls which are lists of horses)
        stalls.addAll(Json.Default.decodeFromString<List<Stall>>(json))
    }

    /** Encode the stable to a JSON string */
    fun getJson(): String = json.encodeToString(stalls)

    /** Get stall by index */
    fun getStall(index: Int): Stall = stalls[index]
}