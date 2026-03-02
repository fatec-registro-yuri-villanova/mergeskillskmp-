package com.fatec.merge_skills_kmp.domain.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class User(
    val id: String,
    val username: String,
    val email: String,
    @SerialName("streak_count")
    val streakCount: Int = 0,
    @SerialName("streak_freezes")
    val streakFreezes: Int = 0,
    @SerialName("last_activity_at")
    val lastActivityAt: String? = null,
    @SerialName("created_at")
    val createdAt: String? = null,
    @SerialName("updated_at")
    val updatedAt: String? = null
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
    val courseId: Int,
    val title: String,
    val description: String? = null,
    val order: Int = 0,
    @SerialName("created_at")
    val createdAt: String? = null
)

@Serializable
data class Question(
    val id: Int,
    @SerialName("lesson_id")
    val lessonId: Int,
    val question: String,
    val code: String? = null,
    // Represented as a List of Strings parsing from JSONB
    val options: List<String>,
    @SerialName("correct_answer")
    val correctAnswer: Int,
    @SerialName("created_at")
    val createdAt: String? = null
)

@Serializable
data class LessonProgress(
    val id: Int,
    @SerialName("user_id")
    val userId: String,
    @SerialName("lesson_id")
    val lessonId: Int,
    @SerialName("is_completed")
    val isCompleted: Boolean = false,
    @SerialName("created_at")
    val createdAt: String? = null
)

@Serializable
data class QuestionAttempt(
    val id: Int,
    @SerialName("user_id")
    val userId: String,
    @SerialName("question_id")
    val questionId: Int,
    @SerialName("is_correct")
    val isCorrect: Boolean,
    @SerialName("selected_option")
    val selectedOption: Int,
    @SerialName("created_at")
    val createdAt: String? = null
)
