package com.example.unitransport.features.bookings.model

data class Booking(
    val id: String = "",
    val destination: String = "",
    val purpose: String = "",
    val passengerCount: Int = 1,
    val departureDate: String = "",
    val departureTime: String = "",
    val returnDate: String = "",
    val returnTime: String = "",
    val vehiclePreference: String = "No Preference",
    val additionalNotes: String = "",
    val status: BookingRequestStatus = BookingRequestStatus.PENDING,
    val createdAt: String = "",
    val vehicleAssigned: String? = null,
    val driverAssigned: String? = null
)

enum class BookingRequestStatus(val displayName: String) {
    PENDING("Pending"),
    APPROVED("Approved"),
    REJECTED("Rejected"),
    ACTIVE("Active"),
    COMPLETED("Completed"),
    CANCELLED("Cancelled")
}

val mockBookings = listOf(
    Booking(
        id = "BK001",
        destination = "Engineering Block",
        purpose = "Department Field Trip",
        passengerCount = 25,
        departureDate = "2026-06-27",
        departureTime = "08:00 AM",
        returnDate = "2026-06-27",
        returnTime = "05:00 PM",
        vehiclePreference = "Bus",
        status = BookingRequestStatus.APPROVED,
        createdAt = "2026-06-25",
        vehicleAssigned = "KDA 123A",
        driverAssigned = "John Kamau"
    ),
    Booking(
        id = "BK002",
        destination = "Main Campus Library",
        purpose = "Research Visit",
        passengerCount = 5,
        departureDate = "2026-06-28",
        departureTime = "09:00 AM",
        returnDate = "2026-06-28",
        returnTime = "01:00 PM",
        vehiclePreference = "Van",
        status = BookingRequestStatus.PENDING,
        createdAt = "2026-06-26"
    ),
    Booking(
        id = "BK003",
        destination = "Sports Complex",
        purpose = "Inter-university Games",
        passengerCount = 40,
        departureDate = "2026-06-28",
        departureTime = "03:30 PM",
        returnDate = "2026-06-28",
        returnTime = "08:00 PM",
        vehiclePreference = "Bus",
        status = BookingRequestStatus.APPROVED,
        createdAt = "2026-06-24",
        vehicleAssigned = "KDB 456B",
        driverAssigned = "Peter Mwenda"
    ),
    Booking(
        id = "BK004",
        destination = "Off-campus Conference Center",
        purpose = "Academic Conference",
        passengerCount = 8,
        departureDate = "2026-06-20",
        departureTime = "07:00 AM",
        returnDate = "2026-06-20",
        returnTime = "06:00 PM",
        vehiclePreference = "SUV",
        status = BookingRequestStatus.REJECTED,
        createdAt = "2026-06-18"
    ),
    Booking(
        id = "BK005",
        destination = "Administration Block",
        purpose = "Staff Meeting",
        passengerCount = 3,
        departureDate = "2026-06-19",
        departureTime = "10:00 AM",
        returnDate = "2026-06-19",
        returnTime = "12:00 PM",
        vehiclePreference = "No Preference",
        status = BookingRequestStatus.COMPLETED,
        createdAt = "2026-06-17",
        vehicleAssigned = "KDC 789C",
        driverAssigned = "James Odhiambo"
    )
)