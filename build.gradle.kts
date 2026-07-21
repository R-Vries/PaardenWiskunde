plugins {
    kotlin("plugin.serialization") version "2.2.20"
    kotlin("jvm") version "2.2.20"
    application
    id("com.gradleup.shadow") version "8.3.0"
}

group = "nl.sgvtegel"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(kotlin("test"))
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.11.0")
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
tasks.withType<JavaExec>() {
    standardInput = System.`in`
}

tasks.register<Copy>("deploy") {
    description = "Builds the shadow jar to the Apps directory"
    group = "distribution"
    dependsOn("clean", "shadowJar")

    from("build/libs/PaardenWiskunde-1.0-SNAPSHOT-all.jar") {
        rename {
            "PaardenWiskunde.jar"
        }
    }

    from("distribution/PaardenWiskunde.bat")

    into("${System.getProperty("user.home")}/AppData/Local/PaardenWiskunde")
}

tasks.named<JavaExec>("run") {
    systemProperty("app.mode", "development")
}