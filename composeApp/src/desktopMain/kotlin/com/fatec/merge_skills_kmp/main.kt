package com.fatec.merge_skills_kmp

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.WindowPlacement
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import com.fatec.merge_skills_kmp.di.desktopAppModule
import com.fatec.merge_skills_kmp.features.admin.CmsScreen
import com.fatec.merge_skills_kmp.features.auth.LoginScreen
import com.fatec.merge_skills_kmp.viewmodel.AuthViewModel
import kotlinx.coroutines.runBlocking
import org.koin.core.context.startKoin

sealed class Screen {
    object Login : Screen()
    object Cms : Screen()
}

fun main() {
    val koin = startKoin {
        modules(desktopAppModule)
    }.koin

    val authViewModel = koin.get<AuthViewModel>()

    application {
        var currentScreen by remember {
            val initial: Screen = if (runBlocking { authViewModel.isLoggedIn() }) Screen.Cms else Screen.Login
            mutableStateOf(initial)
        }

        Window(
            onCloseRequest = ::exitApplication,
            title = "MergeSkills CMS",
            state = rememberWindowState(
                placement = WindowPlacement.Maximized,
                width = 1280.dp,
                height = 800.dp
            )
        ) {
            MaterialTheme(colorScheme = darkColorScheme()) {
                when (currentScreen) {
                    Screen.Login -> LoginScreen(
                        viewModel = authViewModel,
                        onLoginSuccess = { currentScreen = Screen.Cms }
                    )
                    Screen.Cms -> CmsScreen(
                        authViewModel = authViewModel,
                        onLogout = { currentScreen = Screen.Login }
                    )
                }
            }
        }
    }
}