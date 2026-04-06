package com.fatec.merge_skills_kmp.domain.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class User(
    val id: Int,
    val username: String,
    val email: String? = null,
    val name: String? = null,
    val password: String? = null,
    @SerialName("profile_picture")
    val profilePicture: String? = null,
    @SerialName("streak_count")
    val streakCount: Int = 0,
    @SerialName("last_activity_at")
    val lastActivityAt: String? = null,
    @SerialName("streak_freezes")
    val streakFreezes: Int = 0,
    @SerialName("longest_streak")
    val longestStreak: Int = 0,
    val role: String = "user",
    @SerialName("created_at")
    val createdAt: String? = null
)

@Serializable
data class UpdateUserRequest(
    val name: String? = null,
    @SerialName("profile_picture")
    val profilePictureUrl: String? = null
)

@Serializable
data class InsertUser(
    val username: String,
    val email: String,
    val name: String? = null,
    val password: String? = null,
    @SerialName("profile_picture")
    val profilePicture: String? = null,
    val role: String = "user"
)
