package com.example.unitransport.features.driver.presentation

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.unitransport.core.base.UiState
import com.example.unitransport.data.repository.AuthRepository
import com.example.unitransport.data.repository.BookingRepository
import com.example.unitransport.data.repository.IssueReportRepository
import com.example.unitransport.data.repository.LocationRepository
import com.example.unitransport.data.repository.NotificationRepository
import com.example.unitransport.data.repository.RatingRepository
import com.example.unitransport.data.repository.UserRepository
import com.example.unitransport.data.repository.VehicleRepository
import com.example.unitransport.features.bookings.model.Booking
import com.example.unitransport.features.bookings.model.BookingRequestStatus
import com.example.unitransport.features.driver.model.IssueCategory
import com.example.unitransport.features.driver.model.IssueSeverity
import com.example.unitransport.features.driver.model.LiveLocation
import com.example.unitransport.features.driver.model.RatingType
import com.example.unitransport.features.driver.model.Trip
import com.example.unitransport.features.driver.model.TripStatus
import com.example.unitransport.features.driver.model.simulatedRouteCoordinates
import com.example.unitransport.features.notifications.model.NotificationType
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlinx.coroutines.flow.firstOrNull

@HiltViewModel
class DriverViewModel @Inject constructor(
    private val locationRepository: LocationRepository,
    private val bookingRepository: BookingRepository,
    private val vehicleRepository: VehicleRepository,
    private val userRepository: UserRepository,
    private val issueReportRepository: IssueReportRepository,
    private val authRepository: AuthRepository,
    private val ratingRepository: RatingRepository,
    private val notificationRepository: NotificationRepository
) : ViewModel() {

    private val _tripsState =
        MutableStateFlow<UiState<List<Trip>>>(UiState.Loading)
    val tripsState: StateFlow<UiState<List<Trip>>> = _tripsState.asStateFlow()

    private val _selectedTrip =
        MutableStateFlow<UiState<Trip>>(UiState.Idle)
    val selectedTrip: StateFlow<UiState<Trip>> = _selectedTrip.asStateFlow()

    private val _liveLocation = MutableStateFlow<LiveLocation?>(null)
    val liveLocation: StateFlow<LiveLocation?> = _liveLocation.asStateFlow()

    var isLocationSharing by mutableStateOf(false)
        private set

    private var routeIndex = 0
    private var locationJob: Job? = null

    private val _tripUpdateState =
        MutableStateFlow<UiState<Unit>>(UiState.Idle)
    val tripUpdateState: StateFlow<UiState<Unit>> = _tripUpdateState.asStateFlow()

    var issueCategory by mutableStateOf(IssueCategory.MECHANICAL)
        private set
    var issueDescription by mutableStateOf("")
        private set
    var issueSeverity by mutableStateOf(IssueSeverity.LOW)
        private set
    var issueDescriptionError by mutableStateOf<String?>(null)
        private set

    private val _issueState =
        MutableStateFlow<UiState<Unit>>(UiState.Idle)
    val issueState: StateFlow<UiState<Unit>> = _issueState.asStateFlow()

    // Real driver ID from Firebase Auth (was hardcoded "D001")
    private val driverId: String
        get() = authRepository.currentUserId ?: ""

    private var cachedTrips: List<Trip> = emptyList()

    private suspend fun bookingToTrip(booking: Booking): Trip {
        val vehicle = booking.vehicleAssigned?.let {
            vehicleRepository.getVehicleByRegistration(it)
        }
        val requester = if (booking.userId.isNotBlank()) {
            userRepository.getUserById(booking.userId)
        } else null

        val status = when {
            booking.status == BookingRequestStatus.COMPLETED -> TripStatus.COMPLETED
            booking.status == BookingRequestStatus.CANCELLED -> TripStatus.CANCELLED
            booking.tripStatus == "IN_PROGRESS" -> TripStatus.IN_PROGRESS
            else -> TripStatus.UPCOMING
        }

        return Trip(
            id = booking.id,
            bookingId = booking.id,
            destination = booking.destination,
            purpose = booking.purpose,
            passengerCount = booking.passengerCount,
            departureTime = booking.departureTime,
            returnTime = booking.returnTime,
            date = booking.departureDate,
            vehicleRegistration = booking.vehicleAssigned ?: "",
            vehicleMake = vehicle?.make ?: "",
            vehicleModel = vehicle?.model ?: "",
            requesterName = requester?.fullName ?: "Unknown",
            requesterPhone = requester?.phone ?: "—",
            status = status
        )
    }

    fun loadTrips() {
        viewModelScope.launch {
            _tripsState.value = UiState.Loading
            try {
                bookingRepository.getBookingsForDriver(driverId).collect { bookings ->
                    val trips = bookings.map { bookingToTrip(it) }
                    cachedTrips = trips
                    _tripsState.value = UiState.Success(trips)
                }
            } catch (e: Exception) {
                _tripsState.value = UiState.Error(e.message ?: "Failed to load trips")
            }
        }
    }

    fun loadTripById(tripId: String) {
        viewModelScope.launch {
            _selectedTrip.value = UiState.Loading
            try {
                bookingRepository.getBookingById(tripId).collect { booking ->
                    if (booking != null) {
                        val trip = bookingToTrip(booking)
                        _selectedTrip.value = UiState.Success(trip)
                    } else {
                        _selectedTrip.value = UiState.Error("Trip not found")
                    }
                }
            } catch (e: Exception) {
                _selectedTrip.value = UiState.Error(
                    e.message ?: "Failed to load trip"
                )
            }
        }
    }

    fun toggleLocationSharing() {
        isLocationSharing = !isLocationSharing
        if (isLocationSharing) startLocationUpdates() else stopLocationUpdates()
    }

    private fun startLocationUpdates() {
        routeIndex = 0
        locationJob?.cancel()
        locationJob = viewModelScope.launch {
            try {
                while (isLocationSharing) {
                    if (routeIndex >= simulatedRouteCoordinates.size) routeIndex = 0
                    val coordinate = simulatedRouteCoordinates[routeIndex]
                    val location = coordinate.copy(
                        isSharing = true,
                        timestamp = getCurrentTime(routeIndex)
                    )
                    _liveLocation.value = location
                    locationRepository.updateDriverLocation(driverId, location)
                    routeIndex++
                    kotlinx.coroutines.delay(3000)
                }
            } catch (e: Exception) {
                // cancelled cleanly
            }
        }
    }

    private fun stopLocationUpdates() {
        locationJob?.cancel()
        viewModelScope.launch {
            locationRepository.stopSharing(driverId)
            _liveLocation.value = _liveLocation.value?.copy(isSharing = false)
        }
    }

    private fun getCurrentTime(index: Int): String {
        val times = listOf(
            "08:00 AM", "08:03 AM", "08:06 AM", "08:09 AM", "08:12 AM",
            "08:15 AM", "08:18 AM", "08:21 AM", "08:24 AM", "08:27 AM"
        )
        return times.getOrElse(index) { "08:30 AM" }
    }

    fun startTrip(tripId: String) {
        viewModelScope.launch {
            _tripUpdateState.value = UiState.Loading
            val result = bookingRepository.updateTripStatus(tripId, "IN_PROGRESS")
            result.fold(
                onSuccess = {
                    _tripUpdateState.value = UiState.Success(Unit)
                    notifyTripEvent(tripId, started = true)
                },
                onFailure = {
                    _tripUpdateState.value = UiState.Error(it.message ?: "Failed to start trip")
                }
            )
        }
    }

    fun completeTrip(tripId: String) {
        viewModelScope.launch {
            _tripUpdateState.value = UiState.Loading
            stopLocationUpdates()
            isLocationSharing = false
            val result = bookingRepository.updateBookingStatusOnly(
                bookingId = tripId,
                status = BookingRequestStatus.COMPLETED
            )
            result.fold(
                onSuccess = {
                    _tripUpdateState.value = UiState.Success(Unit)
                    notifyTripEvent(tripId, started = false)
                },
                onFailure = {
                    _tripUpdateState.value = UiState.Error(it.message ?: "Failed to complete trip")
                }
            )
        }
    }

    private val _hasRatedPassengers = MutableStateFlow(false)
    val hasRatedPassengers: StateFlow<Boolean> = _hasRatedPassengers.asStateFlow()

    fun checkIfRatedPassengers(bookingId: String) {
        viewModelScope.launch {
            _hasRatedPassengers.value = ratingRepository.hasRated(
                bookingId, RatingType.DRIVER_RATES_PASSENGERS
            )
        }
    }

    fun onIssueCategoryChange(category: IssueCategory) { issueCategory = category }
    fun onIssueDescriptionChange(value: String) {
        issueDescription = value
        issueDescriptionError = null
    }
    fun onIssueSeverityChange(severity: IssueSeverity) { issueSeverity = severity }

    fun submitIssue(tripId: String, onSuccess: () -> Unit) {
        if (issueDescription.isBlank()) {
            issueDescriptionError = "Please describe the issue"
            return
        }
        viewModelScope.launch {
            _issueState.value = UiState.Loading
            val result = issueReportRepository.submitIssue(
                tripId = tripId,
                driverId = driverId,
                category = issueCategory,
                description = issueDescription,
                severity = issueSeverity
            )
            result.fold(
                onSuccess = {
                    _issueState.value = UiState.Success(Unit)
                    onSuccess()
                },
                onFailure = {
                    _issueState.value = UiState.Error(it.message ?: "Failed to submit issue")
                }
            )
        }
    }

    private fun notifyTripEvent(bookingId: String, started: Boolean) {
        viewModelScope.launch {
            val booking = bookingRepository.getBookingById(bookingId).first()
            if (booking != null && booking.userId.isNotBlank()) {
                notificationRepository.addNotification(
                    userId = booking.userId,
                    title = if (started) "Trip Started" else "Trip Completed",
                    message = if (started)
                        "Your driver has started the trip to ${booking.destination}."
                    else
                        "Your trip to ${booking.destination} has been completed. " +
                                "Thank you for using UniTransport.",
                    type = if (started) NotificationType.TRIP_STARTED
                    else NotificationType.TRIP_COMPLETED,
                    relatedId = booking.id
                )
            }
        }
    }

    fun resetIssueState() { _issueState.value = UiState.Idle }
    fun resetTripUpdateState() { _tripUpdateState.value = UiState.Idle }

    override fun onCleared() {
        super.onCleared()
        locationJob?.cancel()
    }
}