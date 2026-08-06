import kotlinx.serialization.Serializable

@Serializable
data class Stat(
    var level: Int,
    var limit: Int,
    var max: Int
) {

    /** Increase the limit by the amount specified, up until the maximum */
    fun increaseLimit(amount: Int) {
        updateLimit(limit + amount)
    }

    fun updateLevel(newLevel: Int): UpdateResult {
        if (newLevel < 0) {
            return UpdateResult.Error("Level cannot be negative.")
        }

        if (newLevel > limit) {
            return UpdateResult.Error("Level cannot exceed limit ($limit).")
        }

        level = newLevel
        return UpdateResult.Success
    }

    fun updateLimit(newLimit: Int): UpdateResult {
        if (newLimit < level) {
            return UpdateResult.Error("Limit cannot be lower than level ($level).")
        }

        if (newLimit > max) {
            return UpdateResult.Error("Limit cannot exceed max ($max).")
        }

        limit = newLimit
        return UpdateResult.Success
    }

    fun updateMax(newMax: Int): UpdateResult {
        if (newMax < 1) {
            return UpdateResult.Error("Max must be at least 1.")
        }

        if (newMax < limit) {
            return UpdateResult.Error("Max cannot be lower than limit ($limit).")
        }

        max = newMax
        return UpdateResult.Success
    }
}