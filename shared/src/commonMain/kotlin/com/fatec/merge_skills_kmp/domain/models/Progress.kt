package com.fatec.merge_skills_kmp.domain.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

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
