package app
import ui.GUI.startGUI
import ui.TUI
import java.io.File

// fun main() {
//     TUI.start()
// }

fun main() {
    startGUI()
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
