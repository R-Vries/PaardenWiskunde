import java.io.File

object StableRepository {
    private val saveFile = File(AppConfig.dataDirectory, "horses.json")

    fun load(): String {
        if (!saveFile.exists()) {
            saveFile.parentFile.mkdirs()
            saveFile.writeText("[]")
            Stable.import("[]")
            return "No saved horses found, created ${saveFile.absolutePath}"
        }

        return try {
            Stable.import(saveFile.readText())
            "Successfully loaded horses from ${saveFile.absolutePath}"
        } catch (e: Exception) {
            "Could not load horses: ${e.message}"
        }
    }

    fun save() {
        saveFile.parentFile.mkdirs()
        saveFile.writeText(Stable.getJson())
        println("Saved horses to ${saveFile.absolutePath}")
    }
}