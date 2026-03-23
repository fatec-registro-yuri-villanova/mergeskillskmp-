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
data class Course(
    val id: Int,
    val title: String,
    val description: String? = null,
    val icon: String? = null,
    val color: String? = null,
    @SerialName("total_lessons")
    val totalLessons: Int = 0,
    @SerialName("created_at")
    val createdAt: String? = null
)

@Serializable
data class Lesson(
    val id: Int,
    @SerialName("course_id")
    val courseId: Int? = null,
    val title: String,
    val description: String? = null,
    val order: Int? = null,
    @SerialName("created_at")
    val createdAt: String? = null
)

@Serializable
data class Question(
    val id: Int,
    @SerialName("lesson_id")
    val lessonId: Int? = null,
    val question: String,
    val code: String? = null,
    // Represented as a List of Strings parsing from JSONB
    val options: List<String>? = emptyList(),
    @SerialName("correct_answer")
    val correctAnswer: Int? = null,
    val order: Int? = null,
    @SerialName("created_at")
    val createdAt: String? = null
)

@Serializable
data class LessonProgress(
    val id: Int,
    @SerialName("user_id")
    val userId: Int? = null,
    @SerialName("lesson_id")
    val lessonId: Int? = null,
    @SerialName("is_completed")
    val isCompleted: Boolean? = false,
    @SerialName("completed_at")
    val completedAt: String? = null
)

@Serializable
data class QuestionAttempt(
    val id: Int,
    @SerialName("user_id")
    val userId: Int? = null,
    @SerialName("question_id")
    val questionId: Int? = null,
    @SerialName("selected_option")
    val selectedOption: Int? = null,
    @SerialName("is_correct")
    val isCorrect: Boolean? = false,
    val timestamp: String? = null
)

@Serializable
data class LoginRequest(
    val email: String,
    val password: String? = null
)

@Serializable
data class RegisterRequest(
    val email: String,
    val username: String? = null,
    val password: String? = null
)

@Serializable
data class AuthResponse(
    @SerialName("access_token")
    val accessToken: String,
    val user: User? = null
)

@Serializable
data class CourseInsert(
    val title: String,
    val description: String? = null,
    val icon: String? = null,
    val color: String? = null
)

@Serializable
data class LessonInsert(
    @SerialName("course_id")
    val courseId: Int,
    val title: String,
    val description: String? = null,
    val order: Int? = null
)
