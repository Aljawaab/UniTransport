package com.example.unitransport.features.profile.presentation

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.unitransport.core.base.UiState
import com.example.unitransport.features.profile.model.UserProfile
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor() : ViewModel() {

    private val _profileState =
        MutableStateFlow<UiState<UserProfile>>(UiState.Loading)
    val profileState: StateFlow<UiState<UserProfile>> =
        _profileState.asStateFlow()

    // Edit profile fields
    var editFullName by mutableStateOf("")
        private set
    var editPhone by mutableStateOf("")
        private set
    var editDepartment by mutableStateOf("")
        private set

    // Change password fields
    var currentPassword by mutableStateOf("")
        private set
    var newPassword by mutableStateOf("")
        private set
    var confirmPassword by mutableStateOf("")
        private set
    var currentPasswordVisible by mutableStateOf(false)
        private set
    var newPasswordVisible by mutableStateOf(false)
        private set
    var confirmPasswordVisible by mutableStateOf(false)
        private set

    // Password errors
    var currentPasswordError by mutableStateOf<String?>(null)
        private set
    var newPasswordError by mutableStateOf<String?>(null)
        private set
    var confirmPasswordError by mutableStateOf<String?>(null)
        private set

    // Update states
    private val _updateState =
        MutableStateFlow<UiState<Unit>>(UiState.Idle)
    val updateState: StateFlow<UiState<Unit>> =
        _updateState.asStateFlow()

    private val _passwordState =
        MutableStateFlow<UiState<Unit>>(UiState.Idle)
    val passwordState: StateFlow<UiState<Unit>> =
        _passwordState.asStateFlow()

    // Settings
    var isDarkMode by mutableStateOf(false)
        private set
    var notificationsEnabled by mutableStateOf(true)
        private set
    var emailNotifications by mutableStateOf(true)
        private set

    fun loadProfile() {
        viewModelScope.launch {
            _profileState.value = UiState.Loading
            delay(600)
            val profile = UserProfile()
            editFullName = profile.fullName
            editPhone = profile.phone
            editDepartment = profile.department
            _profileState.value = UiState.Success(profile)
        }
    }

    fun onFullNameChange(value: String) { editFullName = value }
    fun onPhoneChange(value: String) { editPhone = value }
    fun onDepartmentChange(value: String) { editDepartment = value }
    fun onCurrentPasswordChange(v: String) {
        currentPassword = v; currentPasswordError = null
    }
    fun onNewPasswordChange(v: String) {
        newPassword = v; newPasswordError = null
    }
    fun onConfirmPasswordChange(v: String) {
        confirmPassword = v; confirmPasswordError = null
    }
    fun toggleCurrentPasswordVisibility() {
        currentPasswordVisible = !currentPasswordVisible
    }
    fun toggleNewPasswordVisibility() {
        newPasswordVisible = !newPasswordVisible
    }
    fun toggleConfirmPasswordVisibility() {
        confirmPasswordVisible = !confirmPasswordVisible
    }
    fun toggleDarkMode() { isDarkMode = !isDarkMode }
    fun toggleNotifications() {
        notificationsEnabled = !notificationsEnabled
    }
    fun toggleEmailNotifications() {
        emailNotifications = !emailNotifications
    }

    fun saveProfile(onSuccess: () -> Unit) {
        viewModelScope.launch {
            _updateState.value = UiState.Loading
            delay(1500)
            _updateState.value = UiState.Success(Unit)
            onSuccess()
        }
    }

    fun changePassword(onSuccess: () -> Unit) {
        var valid = true
        if (currentPassword.isBlank()) {
            currentPasswordError = "Current password is required"
            valid = false
        }
        if (newPassword.length < 6) {
            newPasswordError = "Password must be at least 6 characters"
            valid = false
        }
        if (confirmPassword != newPassword) {
            confirmPasswordError = "Passwords do not match"
            valid = false
        }
        if (!valid) return

        viewModelScope.launch {
            _passwordState.value = UiState.Loading
            delay(1500)
            _passwordState.value = UiState.Success(Unit)
            onSuccess()
        }
    }

    fun resetUpdateState() { _updateState.value = UiState.Idle }
    fun resetPasswordState() { _passwordState.value = UiState.Idle }
}