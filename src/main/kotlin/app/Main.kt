package app

import ui.TUI
import java.io.File

fun main() {
    TUI.start()
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
