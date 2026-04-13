package com.fatec.merge_skills_kmp.plugins

import com.fatec.merge_skills_kmp.routes.courseRoutes
import com.fatec.merge_skills_kmp.routes.authRoutes
import com.fatec.merge_skills_kmp.routes.lessonRoutes
import com.fatec.merge_skills_kmp.routes.progressRoutes
import com.fatec.merge_skills_kmp.routes.uploadRoutes
import com.fatec.merge_skills_kmp.routes.adminRoutes
import io.github.jan.supabase.SupabaseClient
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

        if (supabase != null) {
            authRoutes(supabase)
            courseRoutes(supabase)
            lessonRoutes(supabase)
            progressRoutes(supabase)
            uploadRoutes(supabase)
            adminRoutes(supabase)
        } else {
            route("/api") {
                get("{...}") {
                    call.respondText("SupabaseClient is null. Check Environment Variables.", status = io.ktor.http.HttpStatusCode.InternalServerError)
                }
            }
        }
    }
}
