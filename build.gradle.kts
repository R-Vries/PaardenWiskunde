plugins {
    kotlin("plugin.serialization") version "2.2.20"
    kotlin("jvm") version "2.2.20"
    application
    id("com.gradleup.shadow") version "8.3.0"
}

group = "nl.sgvtegel"
version = "1.2.0"
val appName = "PaardenWiskunde"

val serializationVersion = "1.11.0"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(kotlin("test"))
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:$serializationVersion")
}

tasks.test {
    useJUnitPlatform()
}
kotlin {
    jvmToolchain(21)
}

application {
    mainClass.set("MainKt")
}

// Necessary to allow input in the terminal
tasks.withType<JavaExec> {
    standardInput = System.`in`
}

tasks.named<JavaExec>("run") {
    systemProperty("app.mode", "development")
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
        "MainKt",
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