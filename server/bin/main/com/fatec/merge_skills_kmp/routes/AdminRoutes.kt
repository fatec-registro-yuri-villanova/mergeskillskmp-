package com.fatec.merge_skills_kmp.routes

import com.fatec.merge_skills_kmp.services.SeedService
import io.github.jan.supabase.SupabaseClient
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.call
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.post
import io.ktor.server.routing.route

import kotlinx.serialization.Serializable

@Serializable
data class SeedResponse(val message: String, val new_courses_inserted: Int)

@Serializable
data class SeedErrorResponse(val error: String)

fun Route.adminRoutes(client: SupabaseClient) {
    route("/admin") {
        post("/seed") {
            try {
                val seedService = SeedService(client)
                val newCoursesInserted = seedService.executeSeed()

                call.respond(
                    HttpStatusCode.OK, 
                    SeedResponse(
                        message = "Database seeded successfully", 
                        new_courses_inserted = newCoursesInserted
                    )
                )
            } catch (e: Exception) {
                call.respond(
                    HttpStatusCode.InternalServerError, 
                    SeedErrorResponse(error = "Failed to seed database: ${e.message}")
                )
            }
        }
    }
}

