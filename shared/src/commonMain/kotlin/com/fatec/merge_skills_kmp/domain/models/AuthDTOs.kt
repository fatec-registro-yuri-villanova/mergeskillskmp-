package com.fatec.merge_skills_kmp.domain.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class LoginRequest(
    val email: String,
    val password: String
)

@Serializable
data class RegisterRequest(
    val username: String,
    val email: String,
    val password: String
)

@Serializable
data class AuthResponse(
    val user: User,
    val token: String,
    @SerialName("refresh_token")
    val refreshToken: String? = null
)
