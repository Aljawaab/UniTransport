package com.example.unitransport.features.admin.model

import com.example.unitransport.features.auth.model.UserRole

data class SystemUser(
    val id: String,
    val fullName: String,
    val email: String,
    val role: UserRole,
    val department: String,
    val isActive: Boolean,
    val joinDate: String,
    val totalBookings: Int
)

data class SystemLog(
    val id: String,
    val action: String,
    val performedBy: String,
    val target: String,
    val timestamp: String,
    val type: LogType
)

data class AdminReport(
    val totalUsers: Int,
    val totalVehicles: Int,
    val totalBookings: Int,
    val activeTrips: Int,
    val pendingRequests: Int,
    val completedTripsThisMonth: Int,
    val vehicleUtilizationPercent: Int,
    val mostBookedDestination: String
)

enum class LogType {
    BOOKING, USER, VEHICLE, SYSTEM, AUTH
}

val mockSystemUsers = listOf(
    SystemUser(
        id = "U001",
        fullName = "Alex Mwangi",
        email = "alex.mwangi@university.ac.ke",
        role = UserRole.STUDENT,
        department = "Computer Science",
        isActive = true,
        joinDate = "2024-09-01",
        totalBookings = 12
    ),
    SystemUser(
        id = "U002",
        fullName = "Dr. Zeidine Bukhari",
        email = "z.bukhari@university.ac.ke",
        role = UserRole.STAFF,
        department = "Engineering",
        isActive = true,
        joinDate = "2020-01-15",
        totalBookings = 34
    ),
    SystemUser(
        id = "U003",
        fullName = "John Kamau",
        email = "j.kamau@university.ac.ke",
        role = UserRole.DRIVER,
        department = "Transport",
        isActive = true,
        joinDate = "2019-03-10",
        totalBookings = 0
    ),
    SystemUser(
        id = "U004",
        fullName = "Sarah Njeri",
        email = "s.njeri@university.ac.ke",
        role = UserRole.CLUB_REPRESENTATIVE,
        department = "Drama Club",
        isActive = true,
        joinDate = "2023-09-01",
        totalBookings = 8
    ),
    SystemUser(
        id = "U005",
        fullName = "Peter Mwenda",
        email = "p.mwenda@university.ac.ke",
        role = UserRole.DRIVER,
        department = "Transport",
        isActive = true,
        joinDate = "2021-06-01",
        totalBookings = 0
    ),
    SystemUser(
        id = "U006",
        fullName = "Mary Wanjiku",
        email = "m.wanjiku@university.ac.ke",
        role = UserRole.TRANSPORT_OFFICER,
        department = "Transport Office",
        isActive = true,
        joinDate = "2018-05-20",
        totalBookings = 0
    ),
    SystemUser(
        id = "U007",
        fullName = "Brian Ochieng",
        email = "b.ochieng@university.ac.ke",
        role = UserRole.STUDENT,
        department = "Law",
        isActive = false,
        joinDate = "2022-09-01",
        totalBookings = 3
    )
)

val mockSystemLogs = listOf(
    SystemLog(
        id = "L001",
        action = "Booking Approved",
        performedBy = "Mary Wanjiku",
        target = "BK001 — Engineering Block",
        timestamp = "2026-06-27 08:15 AM",
        type = LogType.BOOKING
    ),
    SystemLog(
        id = "L002",
        action = "User Disabled",
        performedBy = "Admin",
        target = "Brian Ochieng (U007)",
        timestamp = "2026-06-27 07:45 AM",
        type = LogType.USER
    ),
    SystemLog(
        id = "L003",
        action = "Vehicle Added",
        performedBy = "Admin",
        target = "KDH 234H — Mercedes Sprinter",
        timestamp = "2026-06-26 04:30 PM",
        type = LogType.VEHICLE
    ),
    SystemLog(
        id = "L004",
        action = "Booking Rejected",
        performedBy = "Mary Wanjiku",
        target = "BK004 — Off-campus Conference",
        timestamp = "2026-06-26 02:10 PM",
        type = LogType.BOOKING
    ),
    SystemLog(
        id = "L005",
        action = "Driver Login",
        performedBy = "John Kamau",
        target = "System",
        timestamp = "2026-06-27 07:50 AM",
        type = LogType.AUTH
    ),
    SystemLog(
        id = "L006",
        action = "Vehicle Status Updated",
        performedBy = "Admin",
        target = "KDE 345E — Set to Maintenance",
        timestamp = "2026-06-25 10:00 AM",
        type = LogType.VEHICLE
    ),
    SystemLog(
        id = "L007",
        action = "New User Registered",
        performedBy = "System",
        target = "Sarah Njeri (Drama Club)",
        timestamp = "2026-06-24 09:00 AM",
        type = LogType.USER
    ),
    SystemLog(
        id = "L008",
        action = "Trip Completed",
        performedBy = "John Kamau",
        target = "T003 — Administration Block",
        timestamp = "2026-06-26 12:05 PM",
        type = LogType.BOOKING
    )
)

val mockAdminReport = AdminReport(
    totalUsers = 7,
    totalVehicles = 8,
    totalBookings = 57,
    activeTrips = 2,
    pendingRequests = 3,
    completedTripsThisMonth = 24,
    vehicleUtilizationPercent = 68,
    mostBookedDestination = "Engineering Block"
)