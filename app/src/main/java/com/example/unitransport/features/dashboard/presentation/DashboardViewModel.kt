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
                // Get current user profile from Firestore
                val profile = authRepository.getCurrentUserProfile()
                val userName = profile["fullName"] as? String
                    ?: "User"

                // Listen to real bookings from Firestore
                bookingRepository.getUserBookings().collect { bookings ->
                    val pending = bookings.filter {
                        it.status.name == "PENDING"
                    }
                    val approved = bookings.filter {
                        it.status.name == "APPROVED"
                    }

                    // Build upcoming bookings (pending + approved)
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

                    // Build recent activities from latest bookings
                    val activities = bookings
                        .take(4)
                        .map { booking ->
                            RecentActivity(
                                id = booking.id,
                                title = when (booking.status.name) {
                                    "APPROVED" -> "Booking Approved"
                                    "REJECTED" -> "Booking Rejected"
                                    "COMPLETED" -> "Trip Completed"
                                    else -> "Booking Created"
                                },
                                description = "Trip to ${booking.destination}",
                                time = booking.createdAt,
                                type = when (booking.status.name) {
                                    "APPROVED" ->
                                        ActivityType.BOOKING_APPROVED
                                    "REJECTED" ->
                                        ActivityType.BOOKING_REJECTED
                                    "COMPLETED" ->
                                        ActivityType.TRIP_COMPLETED
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
                                activeTrips = bookings.count {
                                    it.status.name == "ACTIVE"
                                }
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