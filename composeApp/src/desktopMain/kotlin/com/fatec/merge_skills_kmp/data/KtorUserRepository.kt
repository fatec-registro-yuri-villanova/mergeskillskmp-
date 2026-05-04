package com.fatec.merge_skills_kmp.data

import com.fatec.merge_skills_kmp.domain.models.ApiError
import com.fatec.merge_skills_kmp.domain.models.AuthResponse
import com.fatec.merge_skills_kmp.domain.models.LoginRequest
import com.fatec.merge_skills_kmp.domain.models.RegisterRequest
import com.fatec.merge_skills_kmp.domain.models.User
import com.fatec.merge_skills_kmp.domain.repository.UserRepository
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.http.isSuccess

class KtorUserRepository(
    private val client: HttpClient,
    private val baseUrl: String
) : UserRepository {

    private suspend inline fun <reified T> safeCall(block: () -> HttpResponse): Result<T> {
        return try {
            val response = block()
            if (response.status.isSuccess()) {
                Result.success(response.body<T>())
            } else {
                val errorMessage = try {
                    response.body<ApiError>().message
                } catch (e: Exception) {
                    try { response.bodyAsText() } catch (ignore: Exception) { "HTTP ${response.status.value}" }
                }
                Result.failure(IllegalStateException(errorMessage))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun login(request: LoginRequest): Result<AuthResponse> = safeCall {
        client.post("$baseUrl/auth/login") {
            contentType(ContentType.Application.Json)
            setBody(request)
        }
    }

    override suspend fun register(request: RegisterRequest): Result<AuthResponse> = safeCall {
        client.post("$baseUrl/auth/register") {
            contentType(ContentType.Application.Json)
            setBody(request)
        }
    }

    override suspend fun getUserProfile(userId: Int): Result<User> = safeCall {
        client.get("$baseUrl/auth/users/$userId")
    }

    override suspend fun updateStreak(userId: Int): Result<User> = safeCall {
        client.post("$baseUrl/users/$userId/streak")
    }
}
