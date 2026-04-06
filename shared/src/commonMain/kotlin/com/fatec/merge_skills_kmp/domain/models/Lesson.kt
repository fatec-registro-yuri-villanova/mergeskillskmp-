package com.fatec.merge_skills_kmp.domain.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

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
data class LessonInsert(
    @SerialName("course_id")
    val courseId: Int,
    val title: String,
    val description: String? = null,
    val order: Int? = null
)
