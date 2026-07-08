package com.example.unitransport.features.officer.model

import com.example.unitransport.features.bookings.model.Booking
import com.example.unitransport.features.bookings.model.BookingRequestStatus
import com.example.unitransport.features.bookings.model.mockBookings
import com.example.unitransport.features.driver.model.LiveLocation
import com.example.unitransport.features.driver.model.TripStatus
import com.example.unitransport.features.vehicles.model.VehicleMockData

data class BookingRequest(
    val booking: Booking,
    val requesterName: String,
    val requesterDepartment: String,
    val requesterPhone: String,
    val rejectionReason: String = ""
)

data class ActiveDriver(
    val id: String,
    val name: String,
    val vehicleRegistration: String,
    val vehicleMake: String,
    val vehicleModel: String,
    val tripDestination: String,
    val tripId: String,
    val status: TripStatus,
    val liveLocation: LiveLocation?,
    val passengerCount: Int
)

data class OfficerStats(
    val pendingRequests: Int,
    val approvedToday: Int,
    val activeTrips: Int,
    val availableVehicles: Int
)

data class DriverOption(
    val id: String,
    val name: String,
    val licenseNumber: String,
    val isAvailable: Boolean,
    val currentAssignment: String? = null
)

// Mock booking requests
val mockBookingRequests = listOf(
    BookingRequest(
        booking = mockBookings[1], // Pending
        requesterName = "Alex Mwangi",
        requesterDepartment = "Computer Science",
        requesterPhone = "+254 712 345 678"
    ),
    BookingRequest(
        booking = Booking(
            id = "BK006",
            destination = "Nairobi CBD",
            purpose = "Club Annual Dinner",
            passengerCount = 35,
            departureDate = "2026-06-29",
            departureTime = "06:00 PM",
            returnDate = "2026-06-29",
            returnTime = "10:00 PM",
            vehiclePreference = "Bus",
            status = BookingRequestStatus.PENDING,
            createdAt = "2026-06-27"
        ),
        requesterName = "Sarah Njeri",
        requesterDepartment = "Drama Club",
        requesterPhone = "+254 723 456 789"
    ),
    BookingRequest(
        booking = Booking(
            id = "BK007",
            destination = "Kenyatta University",
            purpose = "Inter-University Debate",
            passengerCount = 12,
            departureDate = "2026-06-30",
            departureTime = "07:00 AM",
            returnDate = "2026-06-30",
            returnTime = "05:00 PM",
            vehiclePreference = "Minibus",
            status = BookingRequestStatus.PENDING,
            createdAt = "2026-06-27"
        ),
        requesterName = "Brian Ochieng",
        requesterDepartment = "Debate Society",
        requesterPhone = "+254 734 567 890"
    )
)

// Mock active drivers
val mockActiveDrivers = listOf(
    ActiveDriver(
        id = "D001",
        name = "John Kamau",
        vehicleRegistration = "KDA 123A",
        vehicleMake = "Toyota",
        vehicleModel = "Coaster",
        tripDestination = "Engineering Block",
        tripId = "T001",
        status = TripStatus.IN_PROGRESS,
        liveLocation = LiveLocation(
            latitude = -1.2960,
            longitude = 36.8255,
            speed = 45.0,
            timestamp = "08:09 AM",
            isSharing = true
        ),
        passengerCount = 25
    ),
    ActiveDriver(
        id = "D002",
        name = "Peter Mwenda",
        vehicleRegistration = "KDB 456B",
        vehicleMake = "Isuzu",
        vehicleModel = "NQR",
        tripDestination = "Sports Complex",
        tripId = "T004",
        status = TripStatus.UPCOMING,
        liveLocation = null,
        passengerCount = 40
    ),
    ActiveDriver(
        id = "D003",
        name = "James Odhiambo",
        vehicleRegistration = "KDF 678F",
        vehicleMake = "Nissan",
        vehicleModel = "Urvan",
        tripDestination = "Town Campus",
        tripId = "T005",
        status = TripStatus.IN_PROGRESS,
        liveLocation = LiveLocation(
            latitude = -1.3010,
            longitude = 36.8300,
            speed = 30.0,
            timestamp = "08:18 AM",
            isSharing = true
        ),
        passengerCount = 10
    )
)

// Available drivers for assignment
val availableDrivers = listOf(
    DriverOption(
        id = "D001",
        name = "John Kamau",
        licenseNumber = "DL/2020/00123",
        isAvailable = false,
        currentAssignment = "Engineering Block Trip"
    ),
    DriverOption(
        id = "D002",
        name = "Peter Mwenda",
        licenseNumber = "DL/2019/00456",
        isAvailable = true
    ),
    DriverOption(
        id = "D003",
        name = "James Odhiambo",
        licenseNumber = "DL/2021/00789",
        isAvailable = false,
        currentAssignment = "Town Campus Trip"
    ),
    DriverOption(
        id = "D004",
        name = "Samuel Kipchoge",
        licenseNumber = "DL/2022/01012",
        isAvailable = true
    ),
    DriverOption(
        id = "D005",
        name = "David Mutua",
        licenseNumber = "DL/2020/01345",
        isAvailable = true
    )
)