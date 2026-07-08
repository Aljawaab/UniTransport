package com.example.unitransport.core.navigation

object AppDestinations {
    const val SPLASH = "splash"
    const val ONBOARDING = "onboarding"
    const val LOGIN = "login"
    const val DASHBOARD = "dashboard"
    const val VEHICLE_LIST = "vehicle_list"
    const val VEHICLE_DETAIL = "vehicle_detail/{vehicleId}"
    const val CREATE_BOOKING = "create_booking"
    const val BOOKING_HISTORY = "booking_history"
    const val BOOKING_DETAIL = "booking_detail/{bookingId}"
    const val CALENDAR = "calendar"
    const val NOTIFICATIONS = "notifications"
    const val PROFILE = "profile"
    const val SETTINGS = "settings"

    // Helper functions for routes with arguments
    fun vehicleDetail(vehicleId: String) = "vehicle_detail/$vehicleId"
    fun bookingDetail(bookingId: String) = "booking_detail/$bookingId"
}