package com.example.unitransport.features.admin.presentation

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.unitransport.core.base.UiState
import com.example.unitransport.data.repository.BookingRepository
import com.example.unitransport.data.repository.UserRepository
import com.example.unitransport.data.repository.VehicleRepository
import com.example.unitransport.features.admin.model.AdminReport
import com.example.unitransport.features.admin.model.SystemLog
import com.example.unitransport.features.admin.model.SystemUser
import com.example.unitransport.features.auth.model.UserRole
import com.example.unitransport.features.bookings.model.BookingRequestStatus
import com.example.unitransport.features.vehicles.model.VehicleStatus
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject

@HiltViewModel
class AdminViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val bookingRepository: BookingRepository,
    private val vehicleRepository: VehicleRepository
) : ViewModel() {

    private val _reportState =
        MutableStateFlow<UiState<AdminReport>>(UiState.Loading)
    val reportState: StateFlow<UiState<AdminReport>> = _reportState.asStateFlow()

    private val _usersState =
        MutableStateFlow<UiState<List<SystemUser>>>(UiState.Loading)
    val usersState: StateFlow<UiState<List<SystemUser>>> = _usersState.asStateFlow()

    private val _logsState =
        MutableStateFlow<UiState<List<SystemLog>>>(UiState.Loading)
    val logsState: StateFlow<UiState<List<SystemLog>>> = _logsState.asStateFlow()

    private val _toggleState =
        MutableStateFlow<UiState<Unit>>(UiState.Idle)
    val toggleState: StateFlow<UiState<Unit>> = _toggleState.asStateFlow()

    var searchQuery by mutableStateOf("")
        private set
    var selectedRoleFilter by mutableStateOf<UserRole?>(null)
        private set

    private var allUsers: List<SystemUser> = emptyList()

    private val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

    fun loadUsers() {
        viewModelScope.launch {
            _usersState.value = UiState.Loading
            try {
                val profiles = userRepository.getAllUsers()
                val bookings = bookingRepository.getAllBookings().first()
                val bookingCounts = bookings.groupBy { it.userId }
                    .mapValues { it.value.size }

                allUsers = profiles.map { profile ->
                    val role = try {
                        UserRole.valueOf(profile.role)
                    } catch (e: Exception) {
                        UserRole.STUDENT
                    }
                    SystemUser(
                        id = profile.uid,
                        fullName = profile.fullName,
                        email = profile.email,
                        role = role,
                        department = profile.department,
                        isActive = profile.isActive,
                        joinDate = if (profile.createdAt > 0)
                            dateFormat.format(Date(profile.createdAt))
                        else "—",
                        totalBookings = bookingCounts[profile.uid] ?: 0
                    )
                }
                applyUserFilters()
            } catch (e: Exception) {
                _usersState.value = UiState.Error(
                    e.message ?: "Failed to load users"
                )
            }
        }
    }

    fun onSearchQueryChange(query: String) {
        searchQuery = query
        applyUserFilters()
    }

    fun onRoleFilterChange(role: UserRole?) {
        selectedRoleFilter = role
        applyUserFilters()
    }

    private fun applyUserFilters() {
        val filtered = allUsers.filter { user ->
            val matchesSearch = searchQuery.isBlank() ||
                    user.fullName.contains(searchQuery, ignoreCase = true) ||
                    user.email.contains(searchQuery, ignoreCase = true) ||
                    user.department.contains(searchQuery, ignoreCase = true)
            val matchesRole = selectedRoleFilter == null ||
                    user.role == selectedRoleFilter
            matchesSearch && matchesRole
        }
        _usersState.value = UiState.Success(filtered)
    }

    fun toggleUserStatus(userId: String) {
        viewModelScope.launch {
            _toggleState.value = UiState.Loading
            val current = allUsers.find { it.id == userId }
            if (current == null) {
                _toggleState.value = UiState.Error("User not found")
                return@launch
            }
            val result = userRepository.toggleUserActive(userId, !current.isActive)
            result.fold(
                onSuccess = {
                    loadUsers()
                    _toggleState.value = UiState.Success(Unit)
                },
                onFailure = { error ->
                    _toggleState.value = UiState.Error(
                        error.message ?: "Failed to update user"
                    )
                }
            )
        }
    }

    fun loadReport() {
        viewModelScope.launch {
            _reportState.value = UiState.Loading
            try {
                val users = userRepository.getAllUsers()
                val bookings = bookingRepository.getAllBookings().first()
                val vehicles = vehicleRepository.getVehicles().first()

                val activeTrips = bookings.count {
                    it.status == BookingRequestStatus.ACTIVE
                }
                val pendingRequests = bookings.count {
                    it.status == BookingRequestStatus.PENDING
                }
                val completedThisMonth = bookings.count {
                    it.status == BookingRequestStatus.COMPLETED
                }
                val availableVehicles = vehicles.count {
                    it.status == VehicleStatus.AVAILABLE
                }
                val utilization = if (vehicles.isNotEmpty()) {
                    ((vehicles.size - availableVehicles) * 100) / vehicles.size
                } else 0
                val mostBooked = bookings
                    .groupingBy { it.destination }
                    .eachCount()
                    .maxByOrNull { it.value }?.key ?: "—"

                _reportState.value = UiState.Success(
                    AdminReport(
                        totalUsers = users.size,
                        totalVehicles = vehicles.size,
                        totalBookings = bookings.size,
                        activeTrips = activeTrips,
                        pendingRequests = pendingRequests,
                        completedTripsThisMonth = completedThisMonth,
                        vehicleUtilizationPercent = utilization,
                        mostBookedDestination = mostBooked
                    )
                )
            } catch (e: Exception) {
                _reportState.value = UiState.Error(
                    e.message ?: "Failed to load report"
                )
            }
        }
    }

    fun loadLogs() {
        viewModelScope.launch {
            _logsState.value = UiState.Loading
            _logsState.value = UiState.Success(
                com.example.unitransport.features.admin.model.mockSystemLogs
            )
        }
    }
}