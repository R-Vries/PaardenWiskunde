import java.io.File

fun main() {
    val mr = MaterialRepository
    val tui = TUI(Stall(mr.maxTier, FeedingPlanner(mr.materials)))
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

//TODO allow selection of food tier
