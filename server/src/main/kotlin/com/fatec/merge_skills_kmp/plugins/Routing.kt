package com.fatec.merge_skills_kmp.plugins

import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.postgrest.postgrest
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Application.configureRouting(supabase: SupabaseClient?) {
    routing {
        get("/") {
            call.respondText("MergeSkills API is running!")
        }
        
        get("/api/health") {
            call.respondText("""{"status":"ok"}""", io.ktor.http.ContentType.Application.Json)
        }

        get("/api/courses") {
            try {
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
