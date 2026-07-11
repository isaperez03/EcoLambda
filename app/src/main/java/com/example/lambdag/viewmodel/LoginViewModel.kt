package com.example.lambdag.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.ktx.Firebase
import com.google.firebase.auth.ktx.auth
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

data class LoginUiState(
    val email: String = "",
    val password: String = "",
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)

class LoginViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()

    private val _loginResult = MutableSharedFlow<Boolean>()
    val loginResult: SharedFlow<Boolean> = _loginResult.asSharedFlow()

    fun onEmailChange(email: String) {
        _uiState.update { it.copy(email = email, errorMessage = null) }
    }

    fun onPasswordChange(password: String) {
        _uiState.update { it.copy(password = password, errorMessage = null) }
    }

    fun onLoginClick() {
        val email = _uiState.value.email.trim()
        val pwd   = _uiState.value.password

        // Guardia: no intentamos si están vacíos
        if (email.isEmpty() || pwd.isEmpty()) {
            _uiState.update {
                it.copy(
                    isLoading = false,
                    errorMessage = "Correo y contraseña son requeridos"
                )
            }
            return
        }

        _uiState.update { it.copy(isLoading = true, errorMessage = null) }

        Firebase.auth.signInWithEmailAndPassword(email, pwd)
            .addOnCompleteListener { task ->
                viewModelScope.launch {
                    if (task.isSuccessful) {
                        _loginResult.emit(true)
                        _uiState.update { it.copy(isLoading = false) }
                    } else {
                        _loginResult.emit(false)
                        _uiState.update {
                            it.copy(
                                isLoading = false,
                                errorMessage = task.exception?.message ?: "Error desconocido"
                            )
                        }
                    }
                }
            }
    }
}
