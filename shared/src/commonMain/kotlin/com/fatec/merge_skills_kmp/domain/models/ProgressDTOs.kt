package com.fatec.merge_skills_kmp.domain.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class SubmitAnswerRequest(
    @SerialName("userId")
    val userId: Int,
    @SerialName("questionId")
    val questionId: Int,
    @SerialName("selectedOption")
    val selectedOption: Int
)

@Serializable
data class SubmitAnswerResponse(
    @SerialName("isCorrect")
    val isCorrect: Boolean,
    @SerialName("correctAnswer")
    val correctAnswer: Int?,
    val message: String,
    val newStreak: Int? = null,
    val streakFreezes: Int? = null
)

@Serializable
data class ResetProgressRequest(
    @SerialName("userId")
    val userId: Int,
    @SerialName("lessonId")
    val lessonId: Int
)

@Serializable
data class ProgressHistoryResponse(
    val completedLessons: List<LessonProgress>
)
