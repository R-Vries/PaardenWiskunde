import java.io.File

fun main() {
    val tui = TUI(Stall(FeedingPlanner(MaterialRepository.materials)))
    tui.start()
}

object AppConfig {
    val isDevelopment: Boolean
        get() = System.getProperty("app.mode") == "development"

    val dataDirectory: File
        get() =
            if (isDevelopment) {
                File("debug-data")
            } else {
                File(
                    System.getenv("APPDATA"),
                    "PaardenWiskunde"
                )
            }
}
