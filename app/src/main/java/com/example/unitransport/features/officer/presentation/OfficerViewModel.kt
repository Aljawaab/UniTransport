package com.example.unitransport.features.officer.presentation

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.unitransport.core.base.UiState
import com.example.unitransport.features.bookings.model.Booking
import com.example.unitransport.features.bookings.model.BookingRequestStatus
import com.example.unitransport.features.officer.model.ActiveDriver
import com.example.unitransport.features.officer.model.BookingRequest
import com.example.unitransport.features.officer.model.DriverOption
import com.example.unitransport.features.officer.model.OfficerStats
import com.example.unitransport.features.officer.model.availableDrivers
import com.example.unitransport.features.officer.model.mockActiveDrivers
import com.example.unitransport.features.vehicles.model.Vehicle
import com.example.unitransport.features.vehicles.model.VehicleMockData
import com.example.unitransport.features.vehicles.model.VehicleStatus
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull
import javax.inject.Inject
import com.example.unitransport.data.repository.BookingRepository
import com.example.unitransport.data.repository.VehicleRepository
import com.example.unitransport.data.repository.UserRepository
import com.example.unitransport.data.repository.LocationRepository
import com.example.unitransport.data.repository.NotificationRepository
import com.example.unitransport.features.driver.model.TripStatus
import com.example.unitransport.features.notifications.model.NotificationType
import kotlinx.coroutines.flow.combine

@HiltViewModel
class OfficerViewModel @Inject constructor(
    private val bookingRepository: BookingRepository,
    private val vehicleRepository: VehicleRepository,
    private val userRepository: UserRepository,
    private val locationRepository: LocationRepository,
    private val notificationRepository: NotificationRepository
) : ViewModel() {

    // Dashboard state
    private val _statsState =
        MutableStateFlow<UiState<OfficerStats>>(UiState.Loading)
    val statsState: StateFlow<UiState<OfficerStats>> =
        _statsState.asStateFlow()

    // Pending requests
    private val _requestsState =
        MutableStateFlow<UiState<List<BookingRequest>>>(UiState.Loading)
    val requestsState: StateFlow<UiState<List<BookingRequest>>> =
        _requestsState.asStateFlow()

    // Selected request for detail
    private val _selectedRequest =
        MutableStateFlow<UiState<BookingRequest>>(UiState.Idle)
    val selectedRequest: StateFlow<UiState<BookingRequest>> =
        _selectedRequest.asStateFlow()

    // Active drivers / live tracking
    private val _activeDrivers =
        MutableStateFlow<UiState<List<ActiveDriver>>>(UiState.Loading)
    val activeDrivers: StateFlow<UiState<List<ActiveDriver>>> =
        _activeDrivers.asStateFlow()

    // Available vehicles for assignment
    private val _availableVehicles =
        MutableStateFlow<UiState<List<Vehicle>>>(UiState.Loading)
    val availableVehicles: StateFlow<UiState<List<Vehicle>>> =
        _availableVehicles.asStateFlow()

    // Available drivers for assignment
    private val _availableDrivers =
        MutableStateFlow<UiState<List<DriverOption>>>(UiState.Loading)
    val availableDriversList: StateFlow<UiState<List<DriverOption>>> =
        _availableDrivers.asStateFlow()

    // Action states
    private val _approveState =
        MutableStateFlow<UiState<Unit>>(UiState.Idle)
    val approveState: StateFlow<UiState<Unit>> =
        _approveState.asStateFlow()

    private val _rejectState =
        MutableStateFlow<UiState<Unit>>(UiState.Idle)
    val rejectState: StateFlow<UiState<Unit>> =
        _rejectState.asStateFlow()

    private val _assignState =
        MutableStateFlow<UiState<Unit>>(UiState.Idle)
    val assignState: StateFlow<UiState<Unit>> =
        _assignState.asStateFlow()

    // Rejection reason input
    var rejectionReason by mutableStateOf("")
        private set
    var rejectionReasonError by mutableStateOf<String?>(null)
        private set
    var showRejectionDialog by mutableStateOf(false)
        private set
    var currentRejectionBookingId by mutableStateOf("")
        private set

    // In-memory mutable list
    private val allRequests = mutableListOf<BookingRequest>()

    fun loadDashboard() {
        viewModelScope.launch {
            _statsState.value = UiState.Loading
            delay(800)
            val pending = allRequests.count {
                it.booking.status == BookingRequestStatus.PENDING
            }
            _statsState.value = UiState.Success(
                OfficerStats(
                    pendingRequests = pending,
                    approvedToday = 2,
                    activeTrips = mockActiveDrivers.count {
                        it.status ==
                                com.example.unitransport.features.driver
                                    .model.TripStatus.IN_PROGRESS
                    },
                    availableVehicles = VehicleMockData.vehicles.count {
                        it.status == VehicleStatus.AVAILABLE
                    }
                )
            )
        }
    }

    private fun notifyBookingStatusChange(
        bookingId: String,
        approved: Boolean,
        reason: String? = null
    ) {
        viewModelScope.launch {
            val booking = bookingRepository.getBookingById(bookingId).first()
            if (booking != null && booking.userId.isNotBlank()) {
                notificationRepository.addNotification(
                    userId = booking.userId,
                    title = if (approved) "Booking Approved ✓" else "Booking Rejected",
                    message = if (approved)
                        "Your booking to ${booking.destination} has been approved."
                    else
                        "Your booking to ${booking.destination} was rejected." +
                                (reason?.let { " Reason: $it" } ?: ""),
                    type = if (approved) NotificationType.BOOKING_APPROVED
                    else NotificationType.BOOKING_REJECTED,
                    relatedId = booking.id
                )
            }
        }
    }

    fun loadPendingRequests() {
        viewModelScope.launch {
            _requestsState.value = UiState.Loading
            delay(600)
            _requestsState.value = UiState.Success(
                allRequests.filter {
                    it.booking.status == BookingRequestStatus.PENDING
                }
            )
        }
    }

    fun loadAllRequests() {
        viewModelScope.launch {
            _requestsState.value = UiState.Loading
            try {
                bookingRepository.getAllBookings().collect { bookings ->
                    val requests = bookings.map { booking ->
                        val requester = if (booking.userId.isNotBlank()) {
                            userRepository.getUserById(booking.userId)
                        } else null
                        BookingRequest(
                            booking = booking,
                            requesterName = requester?.fullName ?: "Unknown",
                            requesterDepartment = requester?.department ?: "—",
                            requesterPhone = requester?.phone ?: "—"
                        )
                    }
                    _requestsState.value = UiState.Success(requests)
                    allRequests.clear()
                    allRequests.addAll(requests)
                }
            } catch (e: Exception) {
                _requestsState.value = UiState.Error(
                    e.message ?: "Failed to load requests"
                )
            }
        }
    }

    fun loadRequestById(bookingId: String) {
        viewModelScope.launch {
            _selectedRequest.value = UiState.Loading
            try {
                bookingRepository.getBookingById(bookingId).collect { booking ->
                    if (booking != null) {
                        val requester = if (booking.userId.isNotBlank()) {
                            userRepository.getUserById(booking.userId)
                        } else null
                        val request = BookingRequest(
                            booking = booking,
                            requesterName = requester?.fullName ?: "Unknown",
                            requesterDepartment = requester?.department ?: "—",
                            requesterPhone = requester?.phone ?: "—"
                        )
                        _selectedRequest.value = UiState.Success(request)
                    } else {
                        _selectedRequest.value = UiState.Error("Request not found")
                    }
                }
            } catch (e: Exception) {
                _selectedRequest.value = UiState.Error(
                    e.message ?: "Failed to load request"
                )
            }
        }
    }

    fun loadActiveDrivers() {
        viewModelScope.launch {
            _activeDrivers.value = UiState.Loading
            try {
                val bookings = bookingRepository.getAllBookings().first()
                val activeBookings = bookings.filter {
                    it.status == BookingRequestStatus.ACTIVE && it.driverId != null
                }

                if (activeBookings.isEmpty()) {
                    _activeDrivers.value = UiState.Success(emptyList())
                    return@launch
                }

                // Pre-fetch driver + vehicle info once (doesn't change per GPS tick)
                val driverInfo = activeBookings.map { booking ->
                    val profile = userRepository.getUserById(booking.driverId!!)
                    val vehicle = booking.vehicleAssigned?.let {
                        vehicleRepository.getVehicleByRegistration(it)
                    }
                    Triple(booking, profile, vehicle)
                }

                val locationFlows = activeBookings.map { booking ->
                    locationRepository.getDriverLocation(booking.driverId!!)
                }

                combine(locationFlows) { locations ->
                    driverInfo.mapIndexed { index, (booking, profile, vehicle) ->
                        ActiveDriver(
                            id = booking.driverId!!,
                            name = profile?.fullName ?: booking.driverAssigned ?: "Unknown",
                            vehicleRegistration = booking.vehicleAssigned ?: "",
                            vehicleMake = vehicle?.make ?: "",
                            vehicleModel = vehicle?.model ?: "",
                            tripDestination = booking.destination,
                            tripId = booking.id,
                            status = if (booking.tripStatus == "IN_PROGRESS")
                                TripStatus.IN_PROGRESS
                            else
                                TripStatus.UPCOMING,
                            liveLocation = locations[index],
                            passengerCount = booking.passengerCount
                        )
                    }
                }.collect { drivers ->
                    _activeDrivers.value = UiState.Success(drivers)
                }
            } catch (e: Exception) {
                _activeDrivers.value = UiState.Error(
                    e.message ?: "Failed to load active drivers"
                )
            }
        }
    }

    fun loadAvailableVehicles() {
        viewModelScope.launch {
            _availableVehicles.value = UiState.Loading
            try {
                vehicleRepository.getVehicles().collect { vehicles ->
                    _availableVehicles.value = UiState.Success(
                        vehicles.filter {
                            it.status == VehicleStatus.AVAILABLE
                        }
                    )
                }
            } catch (e: Exception) {
                _availableVehicles.value = UiState.Error(
                    e.message ?: "Failed to load vehicles"
                )
            }
        }
    }

    fun loadAvailableDrivers() {
        viewModelScope.launch {
            _availableDrivers.value = UiState.Loading
            try {
                val drivers = userRepository.getUsersByRole("DRIVER")
                val allBookings = bookingRepository.getAllBookings().first()
                val activeAssignments = allBookings
                    .filter { it.status == BookingRequestStatus.ACTIVE }
                val options = drivers.map { driver ->
                    val assignment = activeAssignments.find {
                        it.driverId == driver.uid
                    }
                    DriverOption(
                        id = driver.uid,
                        name = driver.fullName,
                        licenseNumber = "N/A",
                        isAvailable = assignment == null,
                        currentAssignment = assignment?.destination
                    )
                }
                _availableDrivers.value = UiState.Success(options)
            } catch (e: Exception) {
                _availableDrivers.value = UiState.Error(
                    e.message ?: "Failed to load drivers"
                )
            }
        }
    }

    fun approveRequest(bookingId: String, onSuccess: () -> Unit) {
        viewModelScope.launch {
            _approveState.value = UiState.Loading
            val result = bookingRepository.updateBookingStatus(
                bookingId = bookingId,
                status = BookingRequestStatus.APPROVED
            )
            result.fold(
                onSuccess = {
                    _approveState.value = UiState.Success(Unit)
                    loadAllRequests()
                    onSuccess()
                    notifyBookingStatusChange(bookingId, approved = true)
                },
                onFailure = { error ->
                    _approveState.value = UiState.Error(
                        error.message ?: "Failed to approve"
                    )
                }
            )
        }
    }

    fun showRejectDialog(bookingId: String) {
        currentRejectionBookingId = bookingId
        rejectionReason = ""
        rejectionReasonError = null
        showRejectionDialog = true
    }

    fun dismissRejectDialog() {
        showRejectionDialog = false
        rejectionReason = ""
    }

    fun onRejectionReasonChange(value: String) {
        rejectionReason = value
        rejectionReasonError = null
    }

    fun confirmReject(onSuccess: () -> Unit) {
        if (rejectionReason.isBlank()) {
            rejectionReasonError = "Please provide a rejection reason"
            return
        }
        viewModelScope.launch {
            showRejectionDialog = false
            _rejectState.value = UiState.Loading
            val result = bookingRepository.updateBookingStatus(
                bookingId = currentRejectionBookingId,
                status = BookingRequestStatus.REJECTED
            )
            result.fold(
                onSuccess = {
                    _rejectState.value = UiState.Success(Unit)
                    loadAllRequests()
                    onSuccess()
                    notifyBookingStatusChange(currentRejectionBookingId, approved = false, reason = rejectionReason)
                },
                onFailure = { error ->
                    _rejectState.value = UiState.Error(
                        error.message ?: "Failed to reject"
                    )
                }
            )
        }
    }

    fun assignVehicleAndDriver(
        bookingId: String,
        vehicleReg: String,
        driverName: String,
        driverId: String,
        onSuccess: () -> Unit
    ) {
        viewModelScope.launch {
            _assignState.value = UiState.Loading
            val result = bookingRepository.updateBookingStatus(
                bookingId = bookingId,
                status = BookingRequestStatus.ACTIVE,
                vehicleAssigned = vehicleReg,
                driverAssigned = driverName,
                driverId = driverId
            )
            result.fold(
                onSuccess = {
                    _assignState.value = UiState.Success(Unit)
                    loadAllRequests()
                    onSuccess()
                    viewModelScope.launch {
                        val booking = bookingRepository.getBookingById(bookingId).first()
                        if (booking != null && booking.userId.isNotBlank()) {
                            notificationRepository.addNotification(
                                userId = booking.userId,
                                title = "Vehicle Assigned",
                                message = "Vehicle $vehicleReg with driver $driverName has been " +
                                        "assigned to your trip to ${booking.destination}.",
                                type = NotificationType.BOOKING_APPROVED,
                                relatedId = booking.id
                            )
                        }
                    }
                },
                onFailure = { error ->
                    _assignState.value = UiState.Error(
                        error.message ?: "Failed to assign"
                    )
                }
            )
        }
    }

    fun resetApproveState() { _approveState.value = UiState.Idle }
    fun resetRejectState() { _rejectState.value = UiState.Idle }
    fun resetAssignState() { _assignState.value = UiState.Idle }
}