package com.example.unitransport.features.auth.presentation

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.unitransport.core.base.UiState
import com.example.unitransport.data.repository.AuthRepository
import com.example.unitransport.features.auth.model.UserRole
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {

    var email by mutableStateOf("")
        private set
    var password by mutableStateOf("")
        private set
    var passwordVisible by mutableStateOf(false)
        private set
    var emailError by mutableStateOf<String?>(null)
        private set
    var passwordError by mutableStateOf<String?>(null)
        private set

    private val _loginState =
        MutableStateFlow<UiState<UserRole>>(UiState.Idle)
    val loginState: StateFlow<UiState<UserRole>> =
        _loginState.asStateFlow()

    fun onEmailChange(value: String) {
        email = value; emailError = null
    }

    fun onPasswordChange(value: String) {
        password = value; passwordError = null
    }

    fun togglePasswordVisibility() {
        passwordVisible = !passwordVisible
    }

    private fun validate(): Boolean {
        var isValid = true
        if (email.isBlank()) {
            emailError = "Email is required"
            isValid = false
        } else if (!android.util.Patterns.EMAIL_ADDRESS
                .matcher(email).matches()) {
            emailError = "Enter a valid email address"
            isValid = false
        }
        if (password.isBlank()) {
            passwordError = "Password is required"
            isValid = false
        } else if (password.length < 6) {
            passwordError = "Password must be at least 6 characters"
            isValid = false
        }
        return isValid
    }

    fun onLoginClick(onSuccess: (UserRole) -> Unit) {
        if (!validate()) return
        viewModelScope.launch {
            _loginState.value = UiState.Loading
            // Real Firebase Authentication call
            val result = authRepository.login(email, password)
            result.fold(
                onSuccess = { role ->
                    _loginState.value = UiState.Success(role)
                    onSuccess(role)
                },
                onFailure = { error ->
                    _loginState.value = UiState.Error(
                        when {
                            error.message?.contains("password") == true ->
                                "Incorrect password. Please try again."
                            error.message?.contains("user") == true ->
                                "No account found with this email."
                            error.message?.contains("network") == true ->
                                "No internet connection. Please check your network."
                            else -> "Login failed. Please try again."
                        }
                    )
                }
            )
        }
    }

    fun resetState() { _loginState.value = UiState.Idle }
}