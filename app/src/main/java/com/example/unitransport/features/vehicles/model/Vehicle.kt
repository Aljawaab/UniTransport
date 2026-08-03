package com.example.unitransport.features.vehicles.model
import com.example.unitransport.features.bookings.model.Booking
import com.example.unitransport.features.bookings.model.BookingRequestStatus

enum class VehicleDisplayStatus(val displayName: String) {
    AVAILABLE("Available"),
    BOOKED("Booked"),
    ON_TRIP("On Trip"),
    MAINTENANCE("Maintenance"),
    OUT_OF_SERVICE("Out of Service")
}

fun computeVehicleDisplayStatus(
    vehicle: Vehicle,
    allBookings: List<Booking>
): VehicleDisplayStatus {
    val onTrip = allBookings.any {
        it.vehicleAssigned == vehicle.registrationNumber &&
                it.status == BookingRequestStatus.ACTIVE
    }
    return when {
        onTrip -> VehicleDisplayStatus.ON_TRIP
        vehicle.status == VehicleStatus.RESERVED -> VehicleDisplayStatus.BOOKED
        vehicle.status == VehicleStatus.MAINTENANCE -> VehicleDisplayStatus.MAINTENANCE
        vehicle.status == VehicleStatus.OUT_OF_SERVICE -> VehicleDisplayStatus.OUT_OF_SERVICE
        else -> VehicleDisplayStatus.AVAILABLE
    }
}

data class Vehicle(
    val id: String,
    val registrationNumber: String,
    val make: String,
    val model: String,
    val type: VehicleType,
    val capacity: Int,
    val status: VehicleStatus,
    val imageUrl: String = "",
    val description: String = "",
    val yearOfManufacture: Int = 2020,
    val fuelType: String = "Diesel",
    val features: List<String> = emptyList()
)

enum class VehicleType(val displayName: String) {
    BUS("Bus"),
    MINIBUS("Minibus"),
    VAN("Van"),
    SEDAN("Sedan"),
    SUV("SUV"),
    TRUCK("Truck")
}

enum class VehicleStatus(val displayName: String) {
    AVAILABLE("Available"),
    RESERVED("Reserved"),
    MAINTENANCE("Maintenance"),
    OUT_OF_SERVICE("Out of Service")
}