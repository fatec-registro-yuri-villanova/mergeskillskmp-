package com.fatec.merge_skills_kmp

import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application

fun main() = application {
    Window(
        onCloseRequest = ::exitApplication,
        title = "Mergeskillskmp",
    ) {
        App()
    }
}