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
            call.respondText("API LDDM/PDM (Aula 01). Tente acessar /api/health")
        }
        
        get("/api/health") {
            call.respond(
                HealthResponse(
                    status = "Status UP",
                    timestamp = System.currentTimeMillis()
                )
            )
        }
    }
}