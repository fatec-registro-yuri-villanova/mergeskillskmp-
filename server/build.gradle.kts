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

val localProperties = Properties()
val localPropertiesFile = rootProject.file("local.properties")
if (localPropertiesFile.exists()) {
    localProperties.load(FileInputStream(localPropertiesFile))
}

application {
    mainClass.set("com.fatec.merge_skills_kmp.ApplicationKt")
    
    val supabaseUrl = localProperties.getProperty("SUPABASE_URL")
    val supabaseKey = localProperties.getProperty("SUPABASE_KEY")
    
    val jvmArgs = mutableListOf<String>()
    jvmArgs.add("-Dio.ktor.development=true")
    
    if (supabaseUrl != null && supabaseKey != null) {
        jvmArgs.add("-DSUPABASE_URL=$supabaseUrl")
        jvmArgs.add("-DSUPABASE_KEY=$supabaseKey")
    }
    
    applicationDefaultJvmArgs = jvmArgs
}

dependencies {
    implementation(projects.shared)
    implementation(libs.ktor.server.core)
    implementation(libs.ktor.server.netty)
    implementation(libs.ktor.server.content.negotiation)
    implementation(libs.ktor.server.status.pages)
    implementation(libs.ktor.server.cors)
    implementation(libs.ktor.serialization.kotlinx.json)
    implementation(libs.logback)
    implementation(libs.supabase.storage)

    // Database (Exposed for server-side if needed, but SDK first)
    implementation(libs.exposed.core)
    implementation(libs.exposed.jdbc)
    implementation(libs.exposed.dao)
    implementation(libs.exposed.kotlin.datetime)
    implementation(libs.hikari)
    implementation(libs.postgresql)
}