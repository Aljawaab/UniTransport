package com.example.unitransport.features.vehicles.model

object VehicleMockData {

    val vehicles = listOf(
        Vehicle(
            id = "V001",
            registrationNumber = "KDA 123A",
            make = "Toyota",
            model = "Coaster",
            type = VehicleType.MINIBUS,
            capacity = 30,
            status = VehicleStatus.AVAILABLE,
            description = "Comfortable minibus suitable for medium group trips.",
            yearOfManufacture = 2021,
            fuelType = "Diesel",
            features = listOf("Air Conditioning", "USB Charging", "First Aid Kit")
        ),
        Vehicle(
            id = "V002",
            registrationNumber = "KDB 456B",
            make = "Isuzu",
            model = "NQR",
            type = VehicleType.BUS,
            capacity = 60,
            status = VehicleStatus.AVAILABLE,
            description = "Large bus ideal for full department outings.",
            yearOfManufacture = 2020,
            fuelType = "Diesel",
            features = listOf("Air Conditioning", "Sound System", "Safety Belts")
        ),
        Vehicle(
            id = "V003",
            registrationNumber = "KDC 789C",
            make = "Toyota",
            model = "HiAce",
            type = VehicleType.VAN,
            capacity = 14,
            status = VehicleStatus.RESERVED,
            description = "Compact van for small group transport needs.",
            yearOfManufacture = 2022,
            fuelType = "Petrol",
            features = listOf("Air Conditioning", "Luggage Space")
        ),
        Vehicle(
            id = "V004",
            registrationNumber = "KDD 012D",
            make = "Toyota",
            model = "Land Cruiser",
            type = VehicleType.SUV,
            capacity = 8,
            status = VehicleStatus.AVAILABLE,
            description = "4WD SUV for off-road and field trips.",
            yearOfManufacture = 2023,
            fuelType = "Diesel",
            features = listOf("4WD", "Air Conditioning", "GPS Navigation")
        ),
        Vehicle(
            id = "V005",
            registrationNumber = "KDE 345E",
            make = "Isuzu",
            model = "FRR",
            type = VehicleType.BUS,
            capacity = 48,
            status = VehicleStatus.MAINTENANCE,
            description = "Currently undergoing scheduled maintenance.",
            yearOfManufacture = 2019,
            fuelType = "Diesel",
            features = listOf("Sound System", "Safety Belts")
        ),
        Vehicle(
            id = "V006",
            registrationNumber = "KDF 678F",
            make = "Nissan",
            model = "Urvan",
            type = VehicleType.VAN,
            capacity = 15,
            status = VehicleStatus.AVAILABLE,
            description = "Reliable van for staff and student transport.",
            yearOfManufacture = 2021,
            fuelType = "Diesel",
            features = listOf("Air Conditioning", "USB Charging")
        ),
        Vehicle(
            id = "V007",
            registrationNumber = "KDG 901G",
            make = "Toyota",
            model = "Prado",
            type = VehicleType.SUV,
            capacity = 7,
            status = VehicleStatus.RESERVED,
            description = "Premium SUV for VIP and administrative trips.",
            yearOfManufacture = 2022,
            fuelType = "Diesel",
            features = listOf(
                "Leather Seats", "4WD",
                "GPS Navigation", "Air Conditioning"
            )
        ),
        Vehicle(
            id = "V008",
            registrationNumber = "KDH 234H",
            make = "Mercedes",
            model = "Sprinter",
            type = VehicleType.MINIBUS,
            capacity = 22,
            status = VehicleStatus.AVAILABLE,
            description = "Premium minibus for executive travel.",
            yearOfManufacture = 2023,
            fuelType = "Diesel",
            features = listOf(
                "Leather Seats", "Air Conditioning",
                "WiFi", "USB Charging"
            )
        )
    )
}