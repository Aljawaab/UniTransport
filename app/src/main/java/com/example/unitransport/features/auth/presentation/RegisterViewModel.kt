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
class RegisterViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {

    var fullName by mutableStateOf("")
        private set
    var email by mutableStateOf("")
        private set
    var department by mutableStateOf("")
        private set
    var password by mutableStateOf("")
        private set
    var confirmPassword by mutableStateOf("")
        private set
    var selectedRole by mutableStateOf(UserRole.STUDENT)
        private set
    var passwordVisible by mutableStateOf(false)
        private set
    var confirmPasswordVisible by mutableStateOf(false)
        private set
    var roleDropdownExpanded by mutableStateOf(false)
        private set

    var fullNameError by mutableStateOf<String?>(null)
        private set
    var emailError by mutableStateOf<String?>(null)
        private set
    var departmentError by mutableStateOf<String?>(null)
        private set
    var passwordError by mutableStateOf<String?>(null)
        private set
    var confirmPasswordError by mutableStateOf<String?>(null)
        private set

    private val _registerState =
        MutableStateFlow<UiState<Unit>>(UiState.Idle)
    val registerState: StateFlow<UiState<Unit>> =
        _registerState.asStateFlow()

    fun onFullNameChange(v: String) { fullName = v; fullNameError = null }
    fun onEmailChange(v: String) { email = v; emailError = null }
    fun onDepartmentChange(v: String) { department = v; departmentError = null }
    fun onPasswordChange(v: String) { password = v; passwordError = null }
    fun onConfirmPasswordChange(v: String) {
        confirmPassword = v; confirmPasswordError = null
    }
    fun onRoleSelected(role: UserRole) {
        selectedRole = role; roleDropdownExpanded = false
    }
    fun togglePasswordVisibility() { passwordVisible = !passwordVisible }
    fun toggleConfirmPasswordVisibility() {
        confirmPasswordVisible = !confirmPasswordVisible
    }
    fun toggleRoleDropdown() { roleDropdownExpanded = !roleDropdownExpanded }
    fun dismissRoleDropdown() { roleDropdownExpanded = false }

    private fun validate(): Boolean {
        var valid = true
        if (fullName.isBlank()) {
            fullNameError = "Full name is required"; valid = false
        } else if (fullName.length < 3) {
            fullNameError = "Name must be at least 3 characters"; valid = false
        }
        if (email.isBlank()) {
            emailError = "Email is required"; valid = false
        } else if (!android.util.Patterns.EMAIL_ADDRESS
                .matcher(email).matches()) {
            emailError = "Enter a valid email"; valid = false
        }
        if (department.isBlank()) {
            departmentError = "Department is required"; valid = false
        }
        if (password.isBlank()) {
            passwordError = "Password is required"; valid = false
        } else if (password.length < 6) {
            passwordError = "At least 6 characters"; valid = false
        }
        if (confirmPassword != password) {
            confirmPasswordError = "Passwords do not match"; valid = false
        }
        return valid
    }

    fun register() {
        if (!validate()) return
        viewModelScope.launch {
            _registerState.value = UiState.Loading
            // Real Firebase registration call
            val result = authRepository.register(
                fullName = fullName,
                email = email,
                password = password,
                department = department,
                role = selectedRole
            )
            result.fold(
                onSuccess = {
                    _registerState.value = UiState.Success(Unit)
                },
                onFailure = { error ->
                    _registerState.value = UiState.Error(
                        when {
                            error.message?.contains("email") == true ->
                                "This email is already registered."
                            error.message?.contains("network") == true ->
                                "No internet connection."
                            else -> "Registration failed. Please try again."
                        }
                    )
                }
            )
        }
    }

    fun resetState() { _registerState.value = UiState.Idle }
}