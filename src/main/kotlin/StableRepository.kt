import java.io.File

object StableRepository {
    private val saveFile = File(AppConfig.dataDirectory, "horses.json")

    fun load(): String {
        if (!saveFile.exists()) {
            return "No saved horses found, creating new file"
        }
        try {
            Stable.import(saveFile.readText())
            return "Successfully loaded horses from ${saveFile.absolutePath}"
        } catch (e: Exception) {
            return "Could not load horses: ${e.message}"
        }
    }

    fun save() {
        saveFile.writeText(Stable.getJson())
        println("Saved horses to ${saveFile.absolutePath}")
    }
}