package com.fatec.merge_skills_kmp.domain.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

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
data class QuestionInsert(
    @SerialName("lesson_id")
    val lessonId: Int,
    val question: String,
    val code: String? = null,
    val options: List<String>,
    @SerialName("correct_answer")
    val correctAnswer: Int,
    val order: Int
)
