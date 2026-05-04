package com.fatec.merge_skills_kmp.domain.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class LoginRequest(
    val email: String,
    val password: String? = null
)

@Serializable
data class RegisterRequest(
    val email: String,
    val username: String? = null,
    val name: String? = null,
    val password: String? = null,
    @SerialName("profile_picture")
    val profilePictureUrl: String? = null
)

@Serializable
data class AuthResponse(
    val token: String? = null,
    val user: User? = null
)
