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

    // Form fields
    var email by mutableStateOf("")
        private set
    var password by mutableStateOf("")
        private set
    var selectedRole by mutableStateOf(UserRole.STUDENT)
        private set
    var passwordVisible by mutableStateOf(false)
        private set
    var roleDropdownExpanded by mutableStateOf(false)
        private set

    // Validation errors
    var emailError by mutableStateOf<String?>(null)
        private set
    var passwordError by mutableStateOf<String?>(null)
        private set

    // Login state
    private val _loginState = MutableStateFlow<UiState<UserRole>>(UiState.Idle)
    val loginState: StateFlow<UiState<UserRole>> = _loginState.asStateFlow()

    fun onEmailChange(value: String) {
        email = value
        emailError = null
    }

    fun onPasswordChange(value: String) {
        password = value
        passwordError = null
    }

    fun onRoleSelected(role: UserRole) {
        selectedRole = role
        roleDropdownExpanded = false
    }

    fun togglePasswordVisibility() {
        passwordVisible = !passwordVisible
    }

    fun toggleRoleDropdown() {
        roleDropdownExpanded = !roleDropdownExpanded
    }

    fun dismissRoleDropdown() {
        roleDropdownExpanded = false
    }

    private fun validate(): Boolean {
        var isValid = true

        if (email.isBlank()) {
            emailError = "Email is required"
            isValid = false
        } else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
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
            // Simulate API call with mock delay
            delay(2000)
            _loginState.value = UiState.Success(selectedRole)
            onSuccess(selectedRole)
        }
    }

    fun resetState() {
        _loginState.value = UiState.Idle
    }
}