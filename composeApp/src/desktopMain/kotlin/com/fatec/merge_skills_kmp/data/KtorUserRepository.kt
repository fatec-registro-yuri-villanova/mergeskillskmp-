package com.fatec.merge_skills_kmp.data

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
import io.ktor.http.ContentType
import io.ktor.http.contentType

class KtorUserRepository(
    private val client: HttpClient,
    private val baseUrl: String
) : UserRepository {

    override suspend fun login(request: LoginRequest): Result<AuthResponse> = runCatching {
        client.post("$baseUrl/auth/login") {
            contentType(ContentType.Application.Json)
            setBody(request)
        }.body()
    }

    override suspend fun register(request: RegisterRequest): Result<AuthResponse> = runCatching {
        client.post("$baseUrl/auth/register") {
            contentType(ContentType.Application.Json)
            setBody(request)
        }.body()
    }

    override suspend fun getUserProfile(userId: Int): Result<User> = runCatching {
        client.get("$baseUrl/users/$userId").body()
    }

    override suspend fun updateStreak(userId: Int): Result<User> = runCatching {
        client.post("$baseUrl/users/$userId/streak").body()
    }
}
