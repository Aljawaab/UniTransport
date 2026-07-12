package com.example.unitransport.features.driver.model

data class Rating(
    val id: String,
    val tripId: String,
    val bookingId: String,
    val raterName: String,
    val raterRole: String,
    val targetName: String,
    val targetId: String,
    val stars: Int,
    val comment: String,
    val timestamp: String,
    val type: RatingType
)

enum class RatingType {
    BOOKER_RATES_DRIVER,
    DRIVER_RATES_PASSENGERS
}

data class DriverRatingSummary(
    val driverId: String,
    val driverName: String,
    val averageRating: Float,
    val totalRatings: Int,
    val ratings: List<Rating>
)

val mockRatings = listOf(
    Rating(
        id = "R001",
        tripId = "T003",
        bookingId = "BK005",
        raterName = "Prof. Mary Kamau",
        raterRole = "Staff Member",
        targetName = "John Kamau",
        targetId = "D001",
        stars = 5,
        comment = "Very punctual and professional driver. " +
                "Arrived 10 minutes early and was courteous throughout.",
        timestamp = "2026-06-26 12:30 PM",
        type = RatingType.BOOKER_RATES_DRIVER
    ),
    Rating(
        id = "R002",
        tripId = "T001",
        bookingId = "BK001",
        raterName = "Dr. James Otieno",
        raterRole = "Staff Member",
        targetName = "John Kamau",
        targetId = "D001",
        stars = 4,
        comment = "Good driver, safe and smooth ride. " +
                "Slightly late but communicated well.",
        timestamp = "2026-06-27 05:15 PM",
        type = RatingType.BOOKER_RATES_DRIVER
    ),
    Rating(
        id = "R003",
        tripId = "T003",
        bookingId = "BK005",
        raterName = "John Kamau",
        raterRole = "Driver",
        targetName = "Staff Group",
        targetId = "BK005",
        stars = 5,
        comment = "Very cooperative passengers. " +
                "Ready on time and followed all instructions.",
        timestamp = "2026-06-26 12:35 PM",
        type = RatingType.DRIVER_RATES_PASSENGERS
    ),
    Rating(
        id = "R004",
        tripId = "T004",
        bookingId = "BK003",
        raterName = "Coach Peter Waweru",
        raterRole = "Club Representative",
        targetName = "Peter Mwenda",
        targetId = "D002",
        stars = 3,
        comment = "Driver was okay but the vehicle had some issues " +
                "with air conditioning during the trip.",
        timestamp = "2026-06-27 08:30 PM",
        type = RatingType.BOOKER_RATES_DRIVER
    )
)

// Pre-computed driver rating summaries
val mockDriverRatings = listOf(
    DriverRatingSummary(
        driverId = "D001",
        driverName = "John Kamau",
        averageRating = 4.5f,
        totalRatings = 2,
        ratings = mockRatings.filter { it.targetId == "D001" }
    ),
    DriverRatingSummary(
        driverId = "D002",
        driverName = "Peter Mwenda",
        averageRating = 3.0f,
        totalRatings = 1,
        ratings = mockRatings.filter { it.targetId == "D002" }
    ),
    DriverRatingSummary(
        driverId = "D003",
        driverName = "James Odhiambo",
        averageRating = 0f,
        totalRatings = 0,
        ratings = emptyList()
    )
)