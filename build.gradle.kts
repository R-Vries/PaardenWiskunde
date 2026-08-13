plugins {
    kotlin("plugin.serialization") version "2.2.20"
    kotlin("jvm") version "2.2.20"
    id("org.jetbrains.compose") version "1.8.2"
    id("org.jetbrains.kotlin.plugin.compose") version "2.2.20"
    id("com.gradleup.shadow") version "8.3.0"
}

group = "nl.sgvtegel"
version = "1.2.0"
val appName = "PaardenWiskunde"

val serializationVersion = "1.11.0"

repositories {
    google()
    mavenCentral()
}

dependencies {
    testImplementation(kotlin("test"))
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:$serializationVersion")
    implementation(compose.desktop.currentOs)
    implementation(compose.material3)
}

compose.desktop {
    application {
        mainClass = "app.MainKt"
    }
}

tasks.test {
    useJUnitPlatform()
}
kotlin {
    jvmToolchain(21)
}

tasks.withType<JavaExec>().configureEach {
    if (name == "run") {
        systemProperty("app.mode", "development")
    }
}

tasks.register<Exec>("packageInstaller") {
    dependsOn("shadowJar")

    description = "Builds the installer for Windows"
    group = "distribution"

    val installerDir = layout.buildDirectory.dir("installer").get().asFile

    doFirst {
        delete(installerDir)
        installerDir.mkdirs()
    }

    val jpackage = File(
        System.getProperty("java.home"),
        "bin/jpackage.exe"
    )

    commandLine(
        jpackage.absolutePath,
        "--input",
        layout.buildDirectory.dir("libs").get().asFile.absolutePath,
        "--main-jar",
        "$appName-$version-all.jar",
        "--main-class",
        "app.MainKt",
        "--name",
        appName,
        "--type",
        "exe",
        "--dest",
        layout.buildDirectory.dir("installer").get().asFile.absolutePath,
        "--win-console",
        "--win-menu",
        "--win-per-user-install",
        "--win-menu-group",
        appName,
        "--icon",
        file("distribution/PaardenWiskunde.ico"),
        "--vendor",
        "r-vries",
        "--app-version",
        version.toString()
    )
}