package com.example.unitransport.features.auth.presentation

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.unitransport.core.base.UiState
import com.example.unitransport.features.auth.model.UserRole
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor() : ViewModel() {

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
        email = value
        emailError = null
    }

    fun onPasswordChange(value: String) {
        password = value
        passwordError = null
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

    // Mock role detection by email prefix
    // In Step 16 this becomes a Firestore lookup of user role
    private fun detectRoleFromEmail(email: String): UserRole {
        return when {
            email.startsWith("driver") -> UserRole.DRIVER
            email.startsWith("officer") -> UserRole.TRANSPORT_OFFICER
            email.startsWith("admin") -> UserRole.ADMINISTRATOR
            else -> UserRole.STUDENT
        }
    }

    fun onLoginClick(onSuccess: (UserRole) -> Unit) {
        if (!validate()) return
        viewModelScope.launch {
            _loginState.value = UiState.Loading
            delay(2000)
            // Mock: detect role from email prefix for testing
            // e.g. driver@test.com → Driver dashboard
            // e.g. officer@test.com → Officer dashboard
            // e.g. admin@test.com → Admin dashboard
            // e.g. anything else → Booker dashboard
            val role = detectRoleFromEmail(email.lowercase())
            _loginState.value = UiState.Success(role)
            onSuccess(role)
        }
    }

    fun resetState() {
        _loginState.value = UiState.Idle
    }
}