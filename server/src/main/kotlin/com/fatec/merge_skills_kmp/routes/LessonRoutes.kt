package com.fatec.merge_skills_kmp.routes

import com.fatec.merge_skills_kmp.domain.models.Lesson
import com.fatec.merge_skills_kmp.domain.models.LessonInsert
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

fun Route.lessonRoutes(supabase: SupabaseClient) {
    route("/api/courses/{id}/lessons") {
        get {
            try {
                val courseId = call.parameters["id"]?.toIntOrNull()
                if (courseId == null) {
                    call.respond(HttpStatusCode.BadRequest, mapOf("error" to "ID do curso inválido"))
                    return@get
                }
                
                val lessons = supabase.postgrest["lessons"].select {
                    filter {
                        eq("course_id", courseId)
                    }
                }.decodeList<Lesson>()
                
                call.respond(lessons)
            } catch (e: Exception) {
                call.respond(HttpStatusCode.InternalServerError, mapOf("error" to e.message))
            }
        }
    }

    route("/api/lessons") {
        post {
            try {
                val lesson = call.receive<LessonInsert>()
                val result = supabase.postgrest["lessons"].insert(lesson).decodeSingle<Lesson>()
                call.respond(HttpStatusCode.Created, result)
            } catch (e: Exception) {
                call.respond(HttpStatusCode.BadRequest, mapOf("error" to "Falha ao criar lição: ${e.message}"))
            }
        }
    }
}
