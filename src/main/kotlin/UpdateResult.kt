sealed class UpdateResult {
    object Success : UpdateResult()
    data class Error(val message: String) : UpdateResult()
}