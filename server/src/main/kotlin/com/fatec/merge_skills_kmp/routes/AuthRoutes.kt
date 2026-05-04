package com.fatec.merge_skills_kmp.routes

import com.fatec.merge_skills_kmp.domain.models.AuthResponse
import com.fatec.merge_skills_kmp.domain.models.InsertUser
import com.fatec.merge_skills_kmp.domain.models.LoginRequest
import com.fatec.merge_skills_kmp.domain.models.RegisterRequest
import com.fatec.merge_skills_kmp.domain.models.UpdateUserRequest
import com.fatec.merge_skills_kmp.domain.models.User
import com.fatec.merge_skills_kmp.domain.models.ApiError
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.postgrest.from
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.call
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.get
import io.ktor.server.routing.post
import io.ktor.server.routing.put
import io.ktor.server.routing.route

fun Route.authRoutes(supabase: SupabaseClient) {
    route("/auth") {
        get("/users/{userId}") {
            val userId = call.parameters["userId"]?.toIntOrNull()
            if (userId == null) {
                call.respond(
                    HttpStatusCode.BadRequest,
                    ApiError(code = "INVALID_USER_ID", message = "User ID must be an integer")
                )
                return@get
            }
            val user = supabase.from("users").select {
                filter { eq("id", userId) }
            }.decodeSingleOrNull<User>()

            if (user != null) {
                call.respond(user)
            } else {
                call.respond(
                    HttpStatusCode.NotFound,
                    ApiError(code = "USER_NOT_FOUND", message = "User with ID $userId not found")
                )
            }
        }
        
        post("/login") {
            val request = call.receive<LoginRequest>()
            
            val users = supabase.from("users").select {
                filter {
                    eq("email", request.email)
                    eq("password", request.password ?: "")
                }
            }.decodeList<User>()

            if (users.isNotEmpty()) {
                val user = users.first()
                call.respond(AuthResponse(token = "", user = user))
            } else {
                call.respond(
                    HttpStatusCode.Unauthorized,
                    ApiError(
                        code = "INVALID_CREDENTIALS",
                        message = "E-mail ou senha incorretos"
                    )
                )
            }
        }

        post("/register") {
            val request = call.receive<RegisterRequest>()
            
            val existing = supabase.from("users").select {
                filter {
                    eq("email", request.email)
                }
            }.decodeList<User>()

            if (existing.isNotEmpty()) {
                call.respond(
                    HttpStatusCode.Conflict,
                    ApiError(code = "EMAIL_ALREADY_EXISTS", message = "Email ${request.email} is already registered")
                )
                return@post
            }

            val newUserInsert = InsertUser(
                username = request.username ?: request.email.substringBefore("@"),
                email = request.email,
                name = request.name,
                password = request.password ?: "123456",
                profilePicture = request.profilePictureUrl
            )

            val insertedUser = supabase.from("users").insert(newUserInsert) {
                select() 
            }.decodeSingle<User>()

            call.respond(AuthResponse(token = "", user = insertedUser))
        }

        put("/users/{userId}") {
            val userId = call.parameters["userId"]?.toIntOrNull()
            if (userId == null) {
                call.respond(
                    HttpStatusCode.BadRequest,
                    ApiError(code = "INVALID_USER_ID", message = "User ID must be an integer")
                )
                return@put
            }

            val request = call.receive<UpdateUserRequest>()

            try {
                val updatedUser = supabase.from("users").update(
                    {
                        request.name?.let { set("name", it) }
                        request.profilePictureUrl?.let { set("profile_picture", it) }
                    }
                ) {
                    filter {
                        eq("id", userId)
                    }
                    select() // To return the updated row
                }.decodeSingle<User>()

                call.respond(AuthResponse(token = "", user = updatedUser))
            } catch (e: Exception) {
                call.respond(
                    HttpStatusCode.InternalServerError,
                    ApiError(code = "UPDATE_FAILED", message = "Failed to update user profile", details = mapOf("error" to (e.message ?: "Unknown")))
                )
            }
        }
    }
}
