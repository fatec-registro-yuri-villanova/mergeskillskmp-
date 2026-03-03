package com.fatec.merge_skills_kmp.domain.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CourseResponse(
    val course: Course,
    val lessons: List<Lesson>? = null
)

@Serializable
data class LessonResponse(
    val lesson: Lesson,
    val questions: List<Question>? = null
)

@Serializable
data class QuestionAttemptRequest(
    @SerialName("question_id")
    val questionId: Int,
    @SerialName("selected_option")
    val selectedOption: Int
)

@Serializable
data class QuestionAttemptResponse(
    @SerialName("is_correct")
    val isCorrect: Boolean,
    @SerialName("correct_answer")
    val correctAnswer: Int,
    val attempt: QuestionAttempt
)
