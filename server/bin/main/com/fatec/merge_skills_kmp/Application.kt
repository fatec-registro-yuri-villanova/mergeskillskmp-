package com.fatec.merge_skills_kmp

import com.fatec.merge_skills_kmp.plugins.*
import io.github.jan.supabase.SupabaseClient
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import kotlinx.serialization.Serializable


fun main() {
    val port = System.getenv("PORT")?.toIntOrNull() ?: 8080
    embeddedServer(Netty, port = port, host = "0.0.0.0", module = Application::module)
        .start(wait = true)
}

@Serializable
data class HealthResponse(
    val status: String,
    val timestamp: Long
)

fun Application.module() {
    // Configurações do Supabase (tentativa 1: JVM args/Env Vars)
    var supabaseUrl = System.getProperty("SUPABASE_URL") ?: System.getenv("SUPABASE_URL")
    var supabaseKey = System.getProperty("SUPABASE_KEY") ?: System.getenv("SUPABASE_KEY")

    // Fallback absoluto: Ler o arquivo local.properties diretamente (para IntelliJ direto no botão de Play)
    if (supabaseUrl == null || supabaseKey == null) {
        val properties = java.util.Properties()
        val localPropFiles = listOf(
            java.io.File("local.properties"),
            java.io.File("../local.properties"),
            java.io.File("../../local.properties")
        )
        val file = localPropFiles.firstOrNull { it.exists() }
        if (file != null) {
            properties.load(java.io.FileInputStream(file))
            supabaseUrl = properties.getProperty("SUPABASE_URL")
            supabaseKey = properties.getProperty("SUPABASE_KEY")
        }
    }
    
    var supabase: SupabaseClient? = null
    
    if (supabaseUrl != null && supabaseKey != null) {
        supabase = createAppSupabaseClient(supabaseUrl, supabaseKey)
    }

    // Pipeline configuration via Modular Plugins (Aula 05)
    configureSerialization()
    configureCORS()
    configureStatusPages()
    configureRouting(supabase)
}