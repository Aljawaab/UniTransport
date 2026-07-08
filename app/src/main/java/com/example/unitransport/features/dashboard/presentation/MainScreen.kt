package com.example.unitransport.features.dashboard.presentation

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.unitransport.core.navigation.AppDestinations
import com.example.unitransport.core.navigation.BottomNavItem
import com.example.unitransport.core.ui.components.AppBottomNav
import com.example.unitransport.features.bookings.presentation.BookingHistoryScreen
import com.example.unitransport.features.calendar.presentation.CalendarScreen
import com.example.unitransport.features.notifications.presentation.NotificationsScreen
import com.example.unitransport.features.vehicles.presentation.VehicleListScreen

@Composable
fun MainScreen(
    onNavigateToCreateBooking: () -> Unit = {},
    onNavigateToVehicleDetail: (String) -> Unit = {},
    onNavigateToBookingDetail: (String) -> Unit = {},
    onNavigateToProfile: () -> Unit = {}
) {
    val bottomNavController: NavHostController = rememberNavController()

    Scaffold(
        bottomBar = {
            AppBottomNav(navController = bottomNavController)
        },
        modifier = Modifier.fillMaxSize()
    ) { paddingValues ->
        NavHost(
            navController = bottomNavController,
            startDestination = BottomNavItem.Home.route,
            modifier = Modifier.padding(paddingValues)
        ) {
            composable(AppDestinations.DASHBOARD) {
                StudentDashboardScreen(
                    onNavigateToCreateBooking = onNavigateToCreateBooking,
                    onNavigateToBookingHistory = {
                        bottomNavController.navigate(
                            AppDestinations.BOOKING_HISTORY
                        ) {
                            launchSingleTop = true
                        }
                    },
                    onNavigateToBookingDetail = onNavigateToBookingDetail,
                    onNavigateToNotifications = {
                        bottomNavController.navigate(
                            AppDestinations.NOTIFICATIONS
                        ) {
                            launchSingleTop = true
                        }
                    },
                    onNavigateToProfile = onNavigateToProfile
                )
            }

            composable(AppDestinations.VEHICLE_LIST) {
                VehicleListScreen(
                    onNavigateToDetail = { vehicleId ->
                        onNavigateToVehicleDetail(vehicleId)
                    }
                )
            }

            composable(AppDestinations.BOOKING_HISTORY) {
                BookingHistoryScreen(
                    onNavigateToDetail = {}
                )
            }

            composable(AppDestinations.CALENDAR) {
                CalendarScreen()
            }

            composable(AppDestinations.NOTIFICATIONS) {
                NotificationsScreen(
                    onNavigateToBooking = {}
                )
            }
        }
    }
}