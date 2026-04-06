package com.fatec.merge_skills_kmp.domain.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

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
data class CourseInsert(
    val title: String,
    val description: String? = null,
    val icon: String? = null,
    val color: String? = null
)
