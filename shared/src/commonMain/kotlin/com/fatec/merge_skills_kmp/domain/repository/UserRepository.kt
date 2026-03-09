package com.fatec.merge_skills_kmp.domain.repository

import com.fatec.merge_skills_kmp.domain.models.AuthResponse
import com.fatec.merge_skills_kmp.domain.models.LoginRequest
import com.fatec.merge_skills_kmp.domain.models.RegisterRequest
import com.fatec.merge_skills_kmp.domain.models.User

interface UserRepository {
    suspend fun login(request: LoginRequest): Result<AuthResponse>
    suspend fun register(request: RegisterRequest): Result<AuthResponse>
    suspend fun getUserProfile(userId: Int): Result<User>
    suspend fun updateStreak(userId: Int): Result<User>
}
