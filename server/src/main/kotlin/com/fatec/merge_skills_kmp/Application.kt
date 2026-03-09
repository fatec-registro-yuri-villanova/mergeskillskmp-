package com.fatec.merge_skills_kmp

import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.postgrest.postgrest

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

    // Pipeline configuration
    install(ContentNegotiation) {
        json(Json {
            prettyPrint = true
            isLenient = true
            ignoreUnknownKeys = true
        })
    }
    
    // Core routes
    routing {
        get("/") {
            call.respondText("API LDDM/PDM (mergeskillskmp). Tente acessar /api/health")
        }
        
        get("/api/health") {
            call.respond(
                HealthResponse(
                    status = "Status UP",
                    timestamp = System.currentTimeMillis()
                )
            )
        }

        get("/api/courses") {
            try {
                // Remove safe call (?) so if supabase is null we get an exception, 
                // and if postgrest fails due to RLS, RestException is thrown.
                val supabaseClient = supabase ?: throw IllegalStateException("SupabaseClient is null. Check Environment Variables.")
                val courses = supabaseClient.postgrest["courses"].select()
                call.respondText(courses.data)
            } catch (e: Exception) {
                e.printStackTrace()
                call.respondText("{\"error\": \"${e.message}\"}", status = io.ktor.http.HttpStatusCode.InternalServerError)
            }
        }
    }
}