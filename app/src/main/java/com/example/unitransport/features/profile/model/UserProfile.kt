package com.example.unitransport.features.profile.model

import com.example.unitransport.features.auth.model.UserRole

data class UserProfile(
    val id: String = "U001",
    val fullName: String = "Alex Mwangi",
    val email: String = "alex.mwangi@university.ac.ke",
    val phone: String = "+254 712 345 678",
    val role: UserRole = UserRole.STUDENT,
    val department: String = "Computer Science",
    val studentId: String = "CS/2022/001",
    val profileInitials: String = "AM",
    val totalBookings: Int = 12,
    val completedTrips: Int = 9,
    val pendingBookings: Int = 2,
    val cancelledBookings: Int = 1
)