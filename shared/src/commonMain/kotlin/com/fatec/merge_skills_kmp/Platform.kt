package com.fatec.merge_skills_kmp

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform