package com.example.unitransport.features.auth.model

enum class UserRole(val displayName: String) {
    STUDENT("Student"),
    CLUB_REPRESENTATIVE("Club Representative"),
    STAFF("Staff Member"),
    DRIVER("Driver"),
    TRANSPORT_OFFICER("Transport Officer"),
    ADMINISTRATOR("Administrator")
}