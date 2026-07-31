package com.example.unitransport.core.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.unitransport.features.admin.presentation.AdminDashboardScreen
import com.example.unitransport.features.admin.presentation.ReportsScreen
import com.example.unitransport.features.admin.presentation.SystemLogsScreen
import com.example.unitransport.features.admin.presentation.UserManagementScreen
import com.example.unitransport.features.auth.model.UserRole
import com.example.unitransport.features.auth.presentation.LoginScreen
import com.example.unitransport.features.auth.presentation.RegisterScreen
import com.example.unitransport.features.auth.presentation.SplashScreen
import com.example.unitransport.features.bookings.model.Booking
import com.example.unitransport.features.bookings.presentation.BookingConfirmationScreen
import com.example.unitransport.features.bookings.presentation.BookingHistoryScreen
import com.example.unitransport.features.bookings.presentation.CreateBookingScreen
import com.example.unitransport.features.dashboard.presentation.MainScreen
import com.example.unitransport.features.driver.model.RatingType
import com.example.unitransport.features.driver.presentation.DriverDashboardScreen
import com.example.unitransport.features.driver.presentation.IssueReportScreen
import com.example.unitransport.features.driver.presentation.RatingScreen
import com.example.unitransport.features.driver.presentation.TripDetailScreen
import com.example.unitransport.features.notifications.presentation.NotificationsScreen
import com.example.unitransport.features.officer.presentation.AssignmentScreen
import com.example.unitransport.features.officer.presentation.DriverRatingsScreen
import com.example.unitransport.features.officer.presentation.LiveTrackingScreen
import com.example.unitransport.features.officer.presentation.OfficerDashboardScreen
import com.example.unitransport.features.officer.presentation.PendingRequestsScreen
import com.example.unitransport.features.officer.presentation.RequestDetailScreen
import com.example.unitransport.features.profile.presentation.ChangePasswordScreen
import com.example.unitransport.features.profile.presentation.EditProfileScreen
import com.example.unitransport.features.profile.presentation.ProfileScreen
import com.example.unitransport.features.vehicles.presentation.VehicleDetailScreen
import com.example.unitransport.features.vehicles.presentation.VehicleListScreen
import com.example.unitransport.features.profile.presentation.SettingsScreen
import com.example.unitransport.features.bookings.presentation.BookingDetailScreen

@Composable
fun AppNavHost(
    navController: NavHostController = rememberNavController()
) {
    var confirmedBooking by remember { mutableStateOf(Booking()) }

    NavHost(
        navController = navController,
        startDestination = AppDestinations.SPLASH
    ) {

        // ── Splash ───────────────────────────────────────────────
        composable(AppDestinations.SPLASH) {
            SplashScreen(
                onNavigateToLogin = {
                    navController.navigate(AppDestinations.LOGIN) {
                        popUpTo(AppDestinations.SPLASH) { inclusive = true }
                    }
                },
                onNavigateToDashboard = { role ->
                    val destination = when (role) {
                        UserRole.DRIVER -> "driver_dashboard"
                        UserRole.TRANSPORT_OFFICER -> "officer_dashboard"
                        UserRole.ADMINISTRATOR -> "admin_dashboard"
                        else -> AppDestinations.DASHBOARD
                    }
                    navController.navigate(destination) {
                        popUpTo(AppDestinations.SPLASH) { inclusive = true }
                    }
                }
            )
        }

        // ── Login ────────────────────────────────────────────────
        composable(AppDestinations.LOGIN) {
            LoginScreen(
                onNavigateToDashboard = { role ->
                    val destination = when (role) {
                        UserRole.DRIVER -> "driver_dashboard"
                        UserRole.TRANSPORT_OFFICER -> "officer_dashboard"
                        UserRole.ADMINISTRATOR -> "admin_dashboard"
                        else -> AppDestinations.DASHBOARD
                    }
                    navController.navigate(destination) {
                        popUpTo(AppDestinations.LOGIN) {
                            inclusive = true
                        }
                    }
                },
                onNavigateToForgotPassword = {},
                onNavigateToRegister = {
                    navController.navigate(AppDestinations.REGISTER)
                }
            )
        }

        // ── Register ─────────────────────────────────────────────
        composable(AppDestinations.REGISTER) {
            RegisterScreen(
                onNavigateToLogin = {
                    navController.navigate(AppDestinations.LOGIN) {
                        popUpTo(AppDestinations.REGISTER) {
                            inclusive = true
                        }
                    }
                },
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }

        // ── Booker Dashboard (Student / Staff / Club) ─────────────
        // Role defaults to STUDENT here
        // In Step 16 Firebase will provide the real role
        composable(AppDestinations.DASHBOARD) {
            MainScreen(
                role = UserRole.STUDENT,
                onNavigateToCreateBooking = {
                    navController.navigate(AppDestinations.CREATE_BOOKING)
                },
                onNavigateToVehicleDetail = { vehicleId ->
                    navController.navigate(
                        AppDestinations.vehicleDetail(vehicleId)
                    )
                },
                onNavigateToBookingDetail = { bookingId ->
                    navController.navigate(
                        AppDestinations.bookingDetail(bookingId)
                    )
                },
                onNavigateToProfile = {
                    navController.navigate(AppDestinations.PROFILE)
                },
                onNavigateToRateDriver = { bookingId, driverName, driverId ->
                    navController.navigate(
                        "rate_driver/$bookingId/$bookingId" +
                                "/$driverName/$driverId"
                    )
                }
            )
        }

        // ── Vehicle Detail ────────────────────────────────────────
        composable(
            route = AppDestinations.VEHICLE_DETAIL,
            arguments = listOf(
                navArgument("vehicleId") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val vehicleId =
                backStackEntry.arguments?.getString("vehicleId") ?: ""
            VehicleDetailScreen(
                vehicleId = vehicleId,
                onNavigateBack = { navController.popBackStack() },
                onNavigateToBooking = {
                    navController.navigate(AppDestinations.CREATE_BOOKING)
                }
            )
        }

        // ── Create Booking ────────────────────────────────────────
        composable(AppDestinations.CREATE_BOOKING) {
            CreateBookingScreen(
                onNavigateBack = { navController.popBackStack() },
                onBookingSuccess = { booking ->
                    confirmedBooking = booking
                    navController.navigate("booking_confirmation") {
                        popUpTo(AppDestinations.CREATE_BOOKING) {
                            inclusive = true
                        }
                    }
                }
            )
        }

        // ── Booking Confirmation ──────────────────────────────────
        // No rate button here — rating happens in Booking History
        composable("booking_confirmation") {
            BookingConfirmationScreen(
                booking = confirmedBooking,
                onNavigateToDashboard = {
                    navController.navigate(AppDestinations.DASHBOARD) {
                        popUpTo(AppDestinations.DASHBOARD) {
                            inclusive = true
                        }
                    }
                },
                onNavigateToBookingHistory = {
                    navController.navigate(AppDestinations.DASHBOARD) {
                        popUpTo(AppDestinations.DASHBOARD) {
                            inclusive = true
                        }
                    }
                }
            )
        }

        // ── Booking History ───────────────────────────────────────
        // Rate button shows only on COMPLETED bookings
        composable(AppDestinations.BOOKING_HISTORY) {
            BookingHistoryScreen(
                onNavigateToDetail = {},
                onNavigateToRateDriver = { bookingId, driverName, driverId ->
                    navController.navigate(
                        "rate_driver/$bookingId/$bookingId" +
                                "/$driverName/$driverId"
                    )
                }
            )
        }

        // ── Booking Detail ────────────────────────────────────────
        composable(
            route = "booking_detail/{bookingId}",
            arguments = listOf(
                navArgument("bookingId") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val bookingId =
                backStackEntry.arguments?.getString("bookingId") ?: ""
            BookingDetailScreen(
                bookingId = bookingId,
                onNavigateBack = { navController.popBackStack() }
            )
        }

        // ── Profile ───────────────────────────────────────────────
        composable(AppDestinations.PROFILE) {
            ProfileScreen(
                onNavigateToEditProfile = {
                    navController.navigate("edit_profile")
                },
                onNavigateToSettings = {
                    navController.navigate(AppDestinations.SETTINGS)
                },
                onNavigateToChangePassword = {
                    navController.navigate("change_password")
                },
                onNavigateToBookingHistory = {
                    navController.popBackStack()
                },
                onLogout = {
                    navController.navigate(AppDestinations.LOGIN) {
                        popUpTo(0) { inclusive = true }
                    }
                }
            )
        }

        composable("edit_profile") {
            EditProfileScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable("change_password") {
            ChangePasswordScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable(AppDestinations.SETTINGS) {
            SettingsScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }

        // ── Notifications (shared) ────────────────────────────────
        composable(AppDestinations.NOTIFICATIONS) {
            NotificationsScreen(
                onNavigateToBooking = {}
            )
        }

        // ── Vehicle List (shared) ─────────────────────────────────
        composable(AppDestinations.VEHICLE_LIST) {
            VehicleListScreen(
                onNavigateToDetail = { vehicleId ->
                    navController.navigate(
                        AppDestinations.vehicleDetail(vehicleId)
                    )
                }
            )
        }

        // ── Driver ────────────────────────────────────────────────
        composable("driver_dashboard") {
            DriverDashboardScreen(
                onNavigateToTripDetail = { tripId ->
                    navController.navigate("trip_detail/$tripId")
                },
                onNavigateToNotifications = {
                    navController.navigate(AppDestinations.NOTIFICATIONS)
                },
                onNavigateToProfile = {
                    navController.navigate(AppDestinations.PROFILE)
                }
            )
        }

        composable(
            route = "trip_detail/{tripId}",
            arguments = listOf(
                navArgument("tripId") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val tripId =
                backStackEntry.arguments?.getString("tripId") ?: ""
            TripDetailScreen(
                tripId = tripId,
                onNavigateBack = { navController.popBackStack() },
                onNavigateToIssueReport = { id ->
                    navController.navigate("issue_report/$id")
                },
                onNavigateToRatePassengers = {
                    navController.navigate(
                        "rate_passengers/$tripId/$tripId"
                    )
                }
            )
        }

        composable(
            route = "issue_report/{tripId}",
            arguments = listOf(
                navArgument("tripId") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val tripId =
                backStackEntry.arguments?.getString("tripId") ?: ""
            IssueReportScreen(
                tripId = tripId,
                onNavigateBack = { navController.popBackStack() }
            )
        }

        // ── Transport Officer ─────────────────────────────────────
        composable("officer_dashboard") {
            OfficerDashboardScreen(
                onNavigateToPendingRequests = {
                    navController.navigate("pending_requests")
                },
                onNavigateToLiveTracking = {
                    navController.navigate("live_tracking")
                },
                onNavigateToDriverRatings = {
                    navController.navigate("driver_ratings")
                },
                onNavigateToNotifications = {
                    navController.navigate(AppDestinations.NOTIFICATIONS)
                },
                onNavigateToProfile = {
                    navController.navigate(AppDestinations.PROFILE)
                }
            )
        }

        composable("pending_requests") {
            PendingRequestsScreen(
                onNavigateBack = { navController.popBackStack() },
                onNavigateToDetail = { bookingId ->
                    navController.navigate("request_detail/$bookingId")
                }
            )
        }

        composable(
            route = "request_detail/{bookingId}",
            arguments = listOf(
                navArgument("bookingId") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val bookingId =
                backStackEntry.arguments?.getString("bookingId") ?: ""
            RequestDetailScreen(
                bookingId = bookingId,
                onNavigateBack = { navController.popBackStack() },
                onNavigateToAssignment = { id ->
                    navController.navigate("assignment/$id")
                }
            )
        }

        composable(
            route = "assignment/{bookingId}",
            arguments = listOf(
                navArgument("bookingId") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val bookingId =
                backStackEntry.arguments?.getString("bookingId") ?: ""
            AssignmentScreen(
                bookingId = bookingId,
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable("live_tracking") {
            LiveTrackingScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }

        // Driver Ratings — Officer sees all driver ratings
        composable("driver_ratings") {
            DriverRatingsScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }

        // ── Administrator ─────────────────────────────────────────
        composable("admin_dashboard") {
            AdminDashboardScreen(
                onNavigateToUsers = {
                    navController.navigate("user_management")
                },
                onNavigateToVehicles = {
                    navController.navigate(AppDestinations.VEHICLE_LIST)
                },
                onNavigateToReports = {
                    navController.navigate("reports")
                },
                onNavigateToLogs = {
                    navController.navigate("system_logs")
                },
                onNavigateToNotifications = {
                    navController.navigate(AppDestinations.NOTIFICATIONS)
                },
                onNavigateToProfile = {
                    navController.navigate(AppDestinations.PROFILE)
                }
            )
        }

        composable("user_management") {
            UserManagementScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable("reports") {
            ReportsScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable("system_logs") {
            SystemLogsScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }

        // ── Rating Screens ────────────────────────────────────────

        // Booker rates driver (from Booking History completed trips)
        composable(
            route = "rate_driver/{tripId}/{bookingId}" +
                    "/{targetName}/{targetId}",
            arguments = listOf(
                navArgument("tripId") { type = NavType.StringType },
                navArgument("bookingId") { type = NavType.StringType },
                navArgument("targetName") { type = NavType.StringType },
                navArgument("targetId") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val tripId = backStackEntry.arguments
                ?.getString("tripId") ?: ""
            val bookingId = backStackEntry.arguments
                ?.getString("bookingId") ?: ""
            val targetName = backStackEntry.arguments
                ?.getString("targetName") ?: ""
            val targetId = backStackEntry.arguments
                ?.getString("targetId") ?: ""
            RatingScreen(
                tripId = tripId,
                bookingId = bookingId,
                targetName = targetName,
                targetId = targetId,
                raterName = "Alex Mwangi",
                raterRole = "Student",
                ratingType = RatingType.BOOKER_RATES_DRIVER,
                onNavigateBack = { navController.popBackStack() }
            )
        }

        // Driver rates passengers (from completed Trip Detail)
        composable(
            route = "rate_passengers/{tripId}/{bookingId}",
            arguments = listOf(
                navArgument("tripId") { type = NavType.StringType },
                navArgument("bookingId") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val tripId = backStackEntry.arguments
                ?.getString("tripId") ?: ""
            val bookingId = backStackEntry.arguments
                ?.getString("bookingId") ?: ""
            RatingScreen(
                tripId = tripId,
                bookingId = bookingId,
                targetName = "Trip Passengers",
                targetId = bookingId,
                raterName = "John Kamau",
                raterRole = "Driver",
                ratingType = RatingType.DRIVER_RATES_PASSENGERS,
                onNavigateBack = { navController.popBackStack() }
            )
        }
    }
}