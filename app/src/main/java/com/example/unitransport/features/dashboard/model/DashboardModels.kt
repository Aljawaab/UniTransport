package com.example.unitransport.features.dashboard.model

import com.example.unitransport.features.auth.model.UserRole
import androidx.compose.runtime.Stable


@Stable
data class DashboardStats(
    val totalBookings: Int = 0,
    val pendingBookings: Int = 0,
    val approvedBookings: Int = 0,
    val activeTrips: Int = 0
)

@Stable
data class QuickBooking(
    val id: String,
    val destination: String,
    val date: String,
    val time: String,
    val status: BookingStatus,
    val vehicleType: String
)

@Stable
data class RecentActivity(
    val id: String,
    val title: String,
    val description: String,
    val time: String,
    val type: ActivityType
)

enum class BookingStatus(val displayName: String) {
    PENDING("Pending"),
    APPROVED("Approved"),
    REJECTED("Rejected"),
    ACTIVE("Active"),
    COMPLETED("Completed"),
    CANCELLED("Cancelled")
}

enum class ActivityType {
    BOOKING_CREATED,
    BOOKING_APPROVED,
    BOOKING_REJECTED,
    TRIP_STARTED,
    TRIP_COMPLETED
}

@Stable
data class DashboardUiData(
    val userName: String = "",
    val role: UserRole = UserRole.STUDENT,
    val stats: DashboardStats = DashboardStats(),
    val upcomingBookings: List<QuickBooking> = emptyList(),
    val recentActivities: List<RecentActivity> = emptyList()
)

