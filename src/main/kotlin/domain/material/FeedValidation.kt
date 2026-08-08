package domain.material

/**
 * Represents the result of a validation check on the amount of food to be fed.
 */
sealed interface FeedValidation {
    data object Valid : FeedValidation
    data class Warning(val message: String) : FeedValidation
    data class Invalid(val message: String) : FeedValidation
}