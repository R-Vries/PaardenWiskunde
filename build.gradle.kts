plugins {
    kotlin("plugin.serialization") version "2.2.20"
    kotlin("jvm") version "2.2.20"
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