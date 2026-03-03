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
    // Configurações do Supabase (obtidas via JVM args definidos no build.gradle.kts)
    val supabaseUrl = System.getProperty("SUPABASE_URL")
    val supabaseKey = System.getProperty("SUPABASE_KEY")
    
    var supabase: SupabaseClient? = null
    
    if (supabaseUrl != null && supabaseKey != null) {
        supabase = createAppSupabaseClient(supabaseUrl, supabaseKey)
    }

    // Pipeline configuration via Modular Plugins (Aula 05)
    configureSerialization()
    configureCORS()
    configureStatusPages()
    configureRouting()
}