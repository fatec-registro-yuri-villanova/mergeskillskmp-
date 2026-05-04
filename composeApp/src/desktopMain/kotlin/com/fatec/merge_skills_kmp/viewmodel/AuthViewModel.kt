package com.fatec.merge_skills_kmp.viewmodel

import com.fatec.merge_skills_kmp.domain.models.LoginRequest
import com.fatec.merge_skills_kmp.domain.models.User
import com.fatec.merge_skills_kmp.domain.repository.UserRepository
import com.russhwolf.settings.Settings
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class AuthViewModel(
    private val repository: UserRepository,
    private val settings: Settings
) {
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    private val _authState = MutableStateFlow<AuthUiState>(AuthUiState.Idle)
    val authState: StateFlow<AuthUiState> = _authState.asStateFlow()

    sealed class AuthUiState {
        object Idle : AuthUiState()
        object Loading : AuthUiState()
        data class Success(val user: User) : AuthUiState()
        data class Error(val message: String) : AuthUiState()
        object NotAdmin : AuthUiState()
    }

    suspend fun isLoggedIn(): Boolean {
        val userId = settings.getIntOrNull("user_id")
        return userId != null && userId > 0
    }

    fun login(email: String, password: String) {
        scope.launch {
            _authState.value = AuthUiState.Loading
            repository.login(LoginRequest(email, password))
                .onSuccess { response ->
                    val user = response.user
                    if (user != null && user.role == "admin") {
                        saveSession(user)
                        _authState.value = AuthUiState.Success(user)
                    } else {
                        _authState.value = AuthUiState.NotAdmin
                    }
                }
                .onFailure { error ->
                    _authState.value = AuthUiState.Error(error.message ?: "Erro desconhecido")
                }
        }
    }

    fun logout() {
        clearSession()
        _authState.value = AuthUiState.Idle
    }

    private fun saveSession(user: User) {
        settings.putInt("user_id", user.id)
        settings.putString("user_name", user.name ?: user.username)
        settings.putString("user_role", user.role)
    }

    private fun clearSession() {
        settings.remove("user_id")
        settings.remove("user_name")
        settings.remove("user_role")
    }
}
