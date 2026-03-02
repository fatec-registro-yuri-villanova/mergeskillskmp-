package com.fatec.merge_skills_kmp

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier

fun main() = application {
    Window(
        onCloseRequest = ::exitApplication,
        title = "Mergeskillskmp",
    ) {
        Box(modifier = Modifier.fillMaxSize().background(Color(0xFF0F0F0F))) {
            App()
        }
    }
}