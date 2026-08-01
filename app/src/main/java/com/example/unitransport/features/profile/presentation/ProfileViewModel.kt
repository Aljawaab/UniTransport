package com.example.unitransport.features.profile.presentation

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.unitransport.core.base.UiState
import com.example.unitransport.core.ui.theme.ThemeState
import com.example.unitransport.data.repository.AuthRepository
import com.example.unitransport.data.repository.BookingRepository
import com.example.unitransport.data.repository.UserRepository
import com.example.unitransport.features.auth.model.UserRole
import com.example.unitransport.features.bookings.model.BookingRequestStatus
import com.example.unitransport.features.profile.model.UserProfile
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val userRepository: UserRepository,
    private val bookingRepository: BookingRepository
) : ViewModel() {

    private val _profileState =
        MutableStateFlow<UiState<UserProfile>>(UiState.Loading)
    val profileState: StateFlow<UiState<UserProfile>> = _profileState.asStateFlow()

    var editFullName by mutableStateOf("")
        private set
    var editPhone by mutableStateOf("")
        private set
    var editDepartment by mutableStateOf("")
        private set

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

    var currentPasswordError by mutableStateOf<String?>(null)
        private set
    var newPasswordError by mutableStateOf<String?>(null)
        private set
    var confirmPasswordError by mutableStateOf<String?>(null)
        private set

    private val _updateState =
        MutableStateFlow<UiState<Unit>>(UiState.Idle)
    val updateState: StateFlow<UiState<Unit>> = _updateState.asStateFlow()

    private val _passwordState =
        MutableStateFlow<UiState<Unit>>(UiState.Idle)
    val passwordState: StateFlow<UiState<Unit>> = _passwordState.asStateFlow()

    var notificationsEnabled by mutableStateOf(true)
        private set
    var emailNotifications by mutableStateOf(true)
        private set

    var isDarkMode by mutableStateOf(ThemeState.isDarkMode)
        private set

    fun loadProfile() {
        viewModelScope.launch {
            _profileState.value = UiState.Loading
            try {
                val uid = authRepository.currentUserId
                if (uid == null) {
                    _profileState.value = UiState.Error("Not logged in")
                    return@launch
                }
                val userDoc = userRepository.getUserById(uid)
                if (userDoc == null) {
                    _profileState.value = UiState.Error("Profile not found")
                    return@launch
                }

                val bookings = bookingRepository.getUserBookings().first()
                val totalBookings = bookings.size
                val completedTrips = bookings.count {
                    it.status == BookingRequestStatus.COMPLETED
                }
                val pendingBookings = bookings.count {
                    it.status == BookingRequestStatus.PENDING
                }
                val cancelledBookings = bookings.count {
                    it.status == BookingRequestStatus.CANCELLED ||
                            it.status == BookingRequestStatus.REJECTED
                }

                val role = try {
                    UserRole.valueOf(userDoc.role)
                } catch (e: Exception) {
                    UserRole.STUDENT
                }

                val initials = userDoc.fullName
                    .split(" ")
                    .filter { it.isNotBlank() }
                    .take(2)
                    .map { it.first().uppercaseChar() }
                    .joinToString("")

                val profile = UserProfile(
                    id = uid,
                    fullName = userDoc.fullName,
                    email = userDoc.email,
                    phone = userDoc.phone,
                    role = role,
                    department = userDoc.department,
                    studentId = uid.take(8), // no dedicated student ID field exists yet
                    profileInitials = initials.ifBlank { "?" },
                    totalBookings = totalBookings,
                    completedTrips = completedTrips,
                    pendingBookings = pendingBookings,
                    cancelledBookings = cancelledBookings
                )

                editFullName = profile.fullName
                editPhone = profile.phone
                editDepartment = profile.department
                _profileState.value = UiState.Success(profile)
            } catch (e: Exception) {
                _profileState.value = UiState.Error(
                    e.message ?: "Failed to load profile"
                )
            }
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

    fun toggleDarkMode() {
        ThemeState.isDarkMode = !ThemeState.isDarkMode
        isDarkMode = ThemeState.isDarkMode
    }
    fun toggleNotifications() { notificationsEnabled = !notificationsEnabled }
    fun toggleEmailNotifications() { emailNotifications = !emailNotifications }

    fun saveProfile(onSuccess: () -> Unit) {
        viewModelScope.launch {
            _updateState.value = UiState.Loading
            val result = authRepository.updateProfile(
                fullName = editFullName,
                phone = editPhone,
                department = editDepartment
            )
            result.fold(
                onSuccess = {
                    _updateState.value = UiState.Success(Unit)
                    onSuccess()
                },
                onFailure = { error ->
                    _updateState.value = UiState.Error(
                        error.message ?: "Failed to update profile"
                    )
                }
            )
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
            val result = authRepository.changePassword(
                currentPassword = currentPassword,
                newPassword = newPassword
            )
            result.fold(
                onSuccess = {
                    _passwordState.value = UiState.Success(Unit)
                    currentPassword = ""
                    newPassword = ""
                    confirmPassword = ""
                    onSuccess()
                },
                onFailure = { error ->
                    _passwordState.value = UiState.Error(
                        error.message?.let {
                            if (it.contains("password", ignoreCase = true) ||
                                it.contains("credential", ignoreCase = true)
                            ) "Current password is incorrect" else it
                        } ?: "Failed to change password"
                    )
                }
            )
        }
    }

    fun resetUpdateState() { _updateState.value = UiState.Idle }
    fun resetPasswordState() { _passwordState.value = UiState.Idle }
}