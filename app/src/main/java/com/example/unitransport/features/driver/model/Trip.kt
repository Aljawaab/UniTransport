package com.example.unitransport.features.driver.model

data class Trip(
    val id: String,
    val bookingId: String,
    val destination: String,
    val purpose: String,
    val passengerCount: Int,
    val departureTime: String,
    val returnTime: String,
    val date: String,
    val vehicleRegistration: String,
    val vehicleMake: String,
    val vehicleModel: String,
    val requesterName: String,
    val requesterPhone: String,
    val status: TripStatus,
    val pickupPoint: String = "Main Gate",
    val notes: String = ""
)

enum class TripStatus(val displayName: String) {
    UPCOMING("Upcoming"),
    IN_PROGRESS("In Progress"),
    COMPLETED("Completed"),
    CANCELLED("Cancelled")
}

data class LiveLocation(
    val latitude: Double,
    val longitude: Double,
    val speed: Double = 0.0,
    val timestamp: String = "",
    val isSharing: Boolean = false
)

// Simulated route coordinates (Nairobi area)
val simulatedRouteCoordinates = listOf(
    LiveLocation(-1.2921, 36.8219, 0.0, "Start"),
    LiveLocation(-1.2931, 36.8229, 40.0, "Moving"),
    LiveLocation(-1.2945, 36.8242, 55.0, "Moving"),
    LiveLocation(-1.2960, 36.8255, 60.0, "Moving"),
    LiveLocation(-1.2978, 36.8270, 58.0, "Moving"),
    LiveLocation(-1.2995, 36.8285, 62.0, "Moving"),
    LiveLocation(-1.3010, 36.8300, 50.0, "Moving"),
    LiveLocation(-1.3025, 36.8315, 45.0, "Moving"),
    LiveLocation(-1.3040, 36.8330, 30.0, "Slowing"),
    LiveLocation(-1.3055, 36.8345, 0.0, "Arrived")
)

val mockTrips = listOf(
    Trip(
        id = "T001",
        bookingId = "BK001",
        destination = "Engineering Block",
        purpose = "Department Field Trip",
        passengerCount = 25,
        departureTime = "08:00 AM",
        returnTime = "05:00 PM",
        date = "2026-06-27",
        vehicleRegistration = "KDA 123A",
        vehicleMake = "Toyota",
        vehicleModel = "Coaster",
        requesterName = "Dr. James Otieno",
        requesterPhone = "+254 722 111 222",
        status = TripStatus.IN_PROGRESS,
        pickupPoint = "Main Gate",
        notes = "Students require special access passes"
    ),
    Trip(
        id = "T002",
        bookingId = "BK003",
        destination = "Sports Complex",
        purpose = "Inter-university Games",
        passengerCount = 40,
        departureTime = "03:30 PM",
        returnTime = "08:00 PM",
        date = "2026-06-27",
        vehicleRegistration = "KDA 123A",
        vehicleMake = "Toyota",
        vehicleModel = "Coaster",
        requesterName = "Coach Peter Waweru",
        requesterPhone = "+254 733 222 333",
        status = TripStatus.UPCOMING,
        pickupPoint = "Sports Ground Entrance",
        notes = "Sports equipment will be loaded"
    ),
    Trip(
        id = "T003",
        bookingId = "BK005",
        destination = "Administration Block",
        purpose = "Staff Meeting",
        passengerCount = 3,
        departureTime = "10:00 AM",
        returnTime = "12:00 PM",
        date = "2026-06-26",
        vehicleRegistration = "KDA 123A",
        vehicleMake = "Toyota",
        vehicleModel = "Coaster",
        requesterName = "Prof. Mary Kamau",
        requesterPhone = "+254 711 333 444",
        status = TripStatus.COMPLETED,
        pickupPoint = "Staff Quarters",
        notes = ""
    )
)

data class VehicleIssue(
    val id: String = "",
    val tripId: String = "",
    val category: IssueCategory = IssueCategory.MECHANICAL,
    val description: String = "",
    val severity: IssueSeverity = IssueSeverity.LOW,
    val reportedAt: String = ""
)

enum class IssueCategory(val displayName: String) {
    MECHANICAL("Mechanical"),
    ELECTRICAL("Electrical"),
    TYRE("Tyre/Wheel"),
    BODY_DAMAGE("Body Damage"),
    FUEL("Fuel"),
    OTHER("Other")
}

enum class IssueSeverity(val displayName: String) {
    LOW("Low — Can continue trip"),
    MEDIUM("Medium — Needs attention soon"),
    HIGH("High — Needs immediate attention")
}