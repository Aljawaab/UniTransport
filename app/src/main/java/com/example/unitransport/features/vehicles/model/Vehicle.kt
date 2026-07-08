package com.example.unitransport.features.vehicles.model

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