package com.example.unitransport.features.dashboard.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.unitransport.core.base.UiState
import com.example.unitransport.features.auth.model.UserRole
import com.example.unitransport.features.dashboard.model.ActivityType
import com.example.unitransport.features.dashboard.model.BookingStatus
import com.example.unitransport.features.dashboard.model.DashboardStats
import com.example.unitransport.features.dashboard.model.DashboardUiData
import com.example.unitransport.features.dashboard.model.QuickBooking
import com.example.unitransport.features.dashboard.model.RecentActivity
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DashboardViewModel @Inject constructor() : ViewModel() {

    private val _dashboardState = MutableStateFlow<UiState<DashboardUiData>>(UiState.Loading)
    val dashboardState: StateFlow<UiState<DashboardUiData>> = _dashboardState.asStateFlow()

    fun loadDashboard(role: UserRole = UserRole.STUDENT) {
        viewModelScope.launch {
            _dashboardState.value = UiState.Loading
            delay(1000) // Simulate API call
            _dashboardState.value = UiState.Success(getMockData(role))
        }
    }

    private fun getMockData(role: UserRole) = DashboardUiData(
        userName = "Alex Mwangi",
        role = role,
        stats = DashboardStats(
            totalBookings = 12,
            pendingBookings = 2,
            approvedBookings = 8,
            activeTrips = 1
        ),
        upcomingBookings = listOf(
            QuickBooking(
                id = "BK001",
                destination = "Engineering Block",
                date = "Today",
                time = "2:00 PM",
                status = BookingStatus.APPROVED,
                vehicleType = "Bus"
            ),
            QuickBooking(
                id = "BK002",
                destination = "Main Campus Library",
                date = "Tomorrow",
                time = "9:00 AM",
                status = BookingStatus.PENDING,
                vehicleType = "Van"
            ),
            QuickBooking(
                id = "BK003",
                destination = "Sports Complex",
                date = "Jun 28",
                time = "3:30 PM",
                status = BookingStatus.APPROVED,
                vehicleType = "Bus"
            )
        ),
        recentActivities = listOf(
            RecentActivity(
                id = "A001",
                title = "Booking Approved",
                description = "Your trip to Engineering Block was approved",
                time = "2 hrs ago",
                type = ActivityType.BOOKING_APPROVED
            ),
            RecentActivity(
                id = "A002",
                title = "Booking Created",
                description = "New booking to Main Campus Library",
                time = "5 hrs ago",
                type = ActivityType.BOOKING_CREATED
            ),
            RecentActivity(
                id = "A003",
                title = "Trip Completed",
                description = "Trip to Administration Block completed",
                time = "Yesterday",
                type = ActivityType.TRIP_COMPLETED
            ),
            RecentActivity(
                id = "A004",
                title = "Booking Rejected",
                description = "Trip to Off-campus venue was rejected",
                time = "2 days ago",
                type = ActivityType.BOOKING_REJECTED
            )
        )
    )
}