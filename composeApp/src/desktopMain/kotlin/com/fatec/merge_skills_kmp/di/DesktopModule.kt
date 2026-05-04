package com.fatec.merge_skills_kmp.di

import com.fatec.merge_skills_kmp.data.KtorUserRepository
import com.fatec.merge_skills_kmp.domain.repository.UserRepository
import com.fatec.merge_skills_kmp.viewmodel.AuthViewModel
import com.russhwolf.settings.PreferencesSettings
import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import org.koin.dsl.module
import java.util.prefs.Preferences

val desktopAppModule = module {

    single<HttpClient> {
        HttpClient(CIO) {
            install(ContentNegotiation) {
                json(Json { ignoreUnknownKeys = true })
            }
        }
    }

    single<UserRepository> {
        KtorUserRepository(
            client = get(),
            baseUrl = "https://lddm-api-inicial-1.onrender.com"
        )
    }

    single<com.russhwolf.settings.Settings> {
        PreferencesSettings(Preferences.userRoot().node("mergeskills-cms"))
    }

    single {
        AuthViewModel(
            repository = get(),
            settings = get()
        )
    }
}
