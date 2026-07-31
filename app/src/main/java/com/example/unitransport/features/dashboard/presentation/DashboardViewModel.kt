package com.example.unitransport.features.dashboard.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.unitransport.core.base.UiState
import com.example.unitransport.data.repository.AuthRepository
import com.example.unitransport.data.repository.BookingRepository
import com.example.unitransport.features.auth.model.UserRole
import com.example.unitransport.features.dashboard.model.ActivityType
import com.example.unitransport.features.dashboard.model.BookingStatus
import com.example.unitransport.features.dashboard.model.DashboardStats
import com.example.unitransport.features.dashboard.model.DashboardUiData
import com.example.unitransport.features.dashboard.model.QuickBooking
import com.example.unitransport.features.dashboard.model.RecentActivity
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DashboardViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val bookingRepository: BookingRepository
) : ViewModel() {

    private val _dashboardState =
        MutableStateFlow<UiState<DashboardUiData>>(UiState.Loading)
    val dashboardState: StateFlow<UiState<DashboardUiData>> =
        _dashboardState.asStateFlow()

    fun loadDashboard(role: UserRole = UserRole.STUDENT) {
        viewModelScope.launch {
            _dashboardState.value = UiState.Loading
            try {
                // Step 1: Get real user profile from Firestore
                val profile = authRepository.getCurrentUserProfile()
                val userName = profile["fullName"] as? String
                    ?: "User"

                // Step 2: Listen to real bookings
                bookingRepository.getUserBookings().collect { bookings ->

                    val pending = bookings.filter {
                        it.status.name == "PENDING"
                    }
                    val approved = bookings.filter {
                        it.status.name == "APPROVED"
                    }
                    val active = bookings.filter {
                        it.status.name == "ACTIVE"
                    }

                    // Build upcoming from real bookings
                    val upcoming = (pending + approved)
                        .take(5)
                        .map { booking ->
                            QuickBooking(
                                id = booking.id,
                                destination = booking.destination,
                                date = booking.departureDate,
                                time = booking.departureTime,
                                status = try {
                                    BookingStatus.valueOf(
                                        booking.status.name
                                    )
                                } catch (e: Exception) {
                                    BookingStatus.PENDING
                                },
                                vehicleType = booking.vehiclePreference
                            )
                        }

                    // Build activity from real bookings
                    val activities = bookings
                        .take(4)
                        .map { booking ->
                            RecentActivity(
                                id = booking.id,
                                title = when (booking.status.name) {
                                    "APPROVED" -> "Booking Approved"
                                    "REJECTED" -> "Booking Rejected"
                                    "COMPLETED" -> "Trip Completed"
                                    "ACTIVE" -> "Trip Started"
                                    else -> "Booking Created"
                                },
                                description = "Trip to " +
                                        booking.destination,
                                time = booking.createdAt
                                    .take(10),
                                type = when (booking.status.name) {
                                    "APPROVED" ->
                                        ActivityType.BOOKING_APPROVED
                                    "REJECTED" ->
                                        ActivityType.BOOKING_REJECTED
                                    "COMPLETED" ->
                                        ActivityType.TRIP_COMPLETED
                                    "ACTIVE" ->
                                        ActivityType.TRIP_STARTED
                                    else ->
                                        ActivityType.BOOKING_CREATED
                                }
                            )
                        }

                    _dashboardState.value = UiState.Success(
                        DashboardUiData(
                            userName = userName,
                            role = role,
                            stats = DashboardStats(
                                totalBookings = bookings.size,
                                pendingBookings = pending.size,
                                approvedBookings = approved.size,
                                activeTrips = active.size
                            ),
                            upcomingBookings = upcoming,
                            recentActivities = activities
                        )
                    )
                }
            } catch (e: Exception) {
                _dashboardState.value = UiState.Error(
                    e.message ?: "Failed to load dashboard"
                )
            }
        }
    }
}