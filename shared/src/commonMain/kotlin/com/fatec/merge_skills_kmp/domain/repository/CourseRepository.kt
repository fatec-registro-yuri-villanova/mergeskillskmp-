package com.fatec.merge_skills_kmp.domain.repository

import com.fatec.merge_skills_kmp.domain.models.CourseResponse
import com.fatec.merge_skills_kmp.domain.models.LessonResponse
import com.fatec.merge_skills_kmp.domain.models.QuestionAttemptRequest
import com.fatec.merge_skills_kmp.domain.models.QuestionAttemptResponse

interface CourseRepository {
    suspend fun getCourses(): Result<List<CourseResponse>>
    suspend fun getCourseDetails(courseId: Int): Result<CourseResponse>
    suspend fun getLessonDetails(lessonId: Int): Result<LessonResponse>
    suspend fun submitQuestionAttempt(userId: String, request: QuestionAttemptRequest): Result<QuestionAttemptResponse>
}
