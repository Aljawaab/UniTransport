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
class RegisterViewModel @Inject constructor() : ViewModel() {

    // Form fields
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

    // Validation errors
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

    // Register state
    private val _registerState =
        MutableStateFlow<UiState<Unit>>(UiState.Idle)
    val registerState: StateFlow<UiState<Unit>> =
        _registerState.asStateFlow()

    fun onFullNameChange(value: String) {
        fullName = value
        fullNameError = null
    }

    fun onEmailChange(value: String) {
        email = value
        emailError = null
    }

    fun onDepartmentChange(value: String) {
        department = value
        departmentError = null
    }

    fun onPasswordChange(value: String) {
        password = value
        passwordError = null
    }

    fun onConfirmPasswordChange(value: String) {
        confirmPassword = value
        confirmPasswordError = null
    }

    fun onRoleSelected(role: UserRole) {
        selectedRole = role
        roleDropdownExpanded = false
    }

    fun togglePasswordVisibility() {
        passwordVisible = !passwordVisible
    }

    fun toggleConfirmPasswordVisibility() {
        confirmPasswordVisible = !confirmPasswordVisible
    }

    fun toggleRoleDropdown() {
        roleDropdownExpanded = !roleDropdownExpanded
    }

    fun dismissRoleDropdown() {
        roleDropdownExpanded = false
    }

    private fun validate(): Boolean {
        var isValid = true

        if (fullName.isBlank()) {
            fullNameError = "Full name is required"
            isValid = false
        } else if (fullName.length < 3) {
            fullNameError = "Name must be at least 3 characters"
            isValid = false
        }

        if (email.isBlank()) {
            emailError = "Email is required"
            isValid = false
        } else if (!android.util.Patterns.EMAIL_ADDRESS
                .matcher(email).matches()) {
            emailError = "Enter a valid email address"
            isValid = false
        }

        if (department.isBlank()) {
            departmentError = "Department is required"
            isValid = false
        }

        if (password.isBlank()) {
            passwordError = "Password is required"
            isValid = false
        } else if (password.length < 6) {
            passwordError = "Password must be at least 6 characters"
            isValid = false
        }

        if (confirmPassword != password) {
            confirmPasswordError = "Passwords do not match"
            isValid = false
        }

        return isValid
    }

    fun register() {
        if (!validate()) return

        viewModelScope.launch {
            _registerState.value = UiState.Loading
            // Simulate registration API call
            // In Step 16 this becomes FirebaseAuth.createUserWithEmailAndPassword()
            // + Firestore user document creation with role
            delay(2000)
            _registerState.value = UiState.Success(Unit)
        }
    }

    fun resetState() {
        _registerState.value = UiState.Idle
    }
}