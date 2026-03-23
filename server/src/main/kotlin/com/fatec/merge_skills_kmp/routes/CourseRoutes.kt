package com.fatec.merge_skills_kmp.routes

import com.fatec.merge_skills_kmp.domain.models.Course
import com.fatec.merge_skills_kmp.domain.models.CourseInsert
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.postgrest.postgrest
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.call
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.get
import io.ktor.server.routing.post
import io.ktor.server.routing.route

fun Route.courseRoutes(supabase: SupabaseClient) {
    route("/api/courses") {
        get {
            try {
                val courses = supabase.postgrest["courses"].select().decodeList<Course>()
                call.respond(courses)
            } catch (e: Exception) {
                call.respond(HttpStatusCode.InternalServerError, mapOf("error" to e.message))
            }
        }

        post {
            try {
                val course = call.receive<CourseInsert>()
                val result = supabase.postgrest["courses"].insert(course) {
                    select()
                }.decodeSingle<Course>()
                call.respond(HttpStatusCode.Created, result)
            } catch (e: Exception) {
                call.respond(HttpStatusCode.BadRequest, mapOf("error" to "Falha ao criar curso: ${e.message}"))
            }
        }
    }
}
