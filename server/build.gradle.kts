plugins {
    alias(libs.plugins.kotlinJvm)
    alias(libs.plugins.ktor)
    alias(libs.plugins.kotlinSerialization)
    application
}

group = "com.fatec.merge_skills_kmp"
version = "1.0.0"
application {
    mainClass.set("com.fatec.merge_skills_kmp.ApplicationKt")
    
    val isDevelopment: Boolean = project.ext.has("development")
    
    val localProperties = java.util.Properties()
    val localPropertiesFile = rootProject.file("local.properties")
    if (localPropertiesFile.exists()) {
        localProperties.load(java.io.FileInputStream(localPropertiesFile))
    }

    val jdbcUrl = localProperties.getProperty("JDBC_DATABASE_URL")
    val jdbcUser = localProperties.getProperty("JDBC_DATABASE_USER")
    val jdbcPassword = localProperties.getProperty("JDBC_DATABASE_PASSWORD")

    val jvmArgs = mutableListOf<String>()
    jvmArgs.add("-Dio.ktor.development=$isDevelopment")
    
    if (jdbcUrl != null) {
        jvmArgs.add("-DJDBC_DATABASE_URL=$jdbcUrl")
        jdbcUser?.let { jvmArgs.add("-DJDBC_DATABASE_USER=$it") }
        jdbcPassword?.let { jvmArgs.add("-DJDBC_DATABASE_PASSWORD=$it") }
    }
    
    applicationDefaultJvmArgs = jvmArgs
}

dependencies {
    implementation(projects.shared)
    implementation(libs.logback)
    implementation(libs.ktor.serverCore)
    implementation(libs.ktor.serverNetty)
    implementation(libs.ktor.server.content.negotiation)
    implementation(libs.ktor.serialization.kotlinx.json)
    
    // Database
    implementation(libs.exposed.core)
    implementation(libs.exposed.jdbc)
    implementation(libs.exposed.dao)
    implementation(libs.exposed.kotlin.datetime)
    implementation(libs.hikari)
    implementation(libs.postgresql)

    testImplementation(libs.ktor.serverTestHost)
    testImplementation(libs.kotlin.testJunit)
}