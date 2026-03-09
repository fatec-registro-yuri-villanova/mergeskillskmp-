import java.io.FileInputStream
import java.util.Properties

plugins {
    alias(libs.plugins.kotlinJvm)
    alias(libs.plugins.kotlinSerialization)
    application
}

group = "com.fatec.merge_skills_kmp"
version = "1.0.0"

kotlin {
    jvmToolchain(21)
}

application {
    mainClass.set("com.fatec.merge_skills_kmp.ApplicationKt")

    val jvmArgs = mutableListOf<String>()
    jvmArgs.add("-Dio.ktor.development=true")

    applicationDefaultJvmArgs = jvmArgs
}

dependencies {
    implementation(projects.shared)
    implementation(libs.ktor.server.core)
    implementation(libs.ktor.server.netty)
    implementation(libs.ktor.server.content.negotiation)
    implementation(libs.ktor.server.status.pages)
    implementation(libs.ktor.serialization.kotlinx.json)
    implementation(libs.logback)
}