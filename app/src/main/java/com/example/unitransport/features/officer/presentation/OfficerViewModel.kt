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
import com.example.unitransport.features.officer.model.mockBookingRequests
import com.example.unitransport.features.vehicles.model.Vehicle
import com.example.unitransport.features.vehicles.model.VehicleMockData
import com.example.unitransport.features.vehicles.model.VehicleStatus
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import com.example.unitransport.data.repository.BookingRepository
import com.example.unitransport.data.repository.VehicleRepository

@HiltViewModel
class OfficerViewModel @Inject constructor(
    private val bookingRepository: BookingRepository,
    private val vehicleRepository: VehicleRepository
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
    private val allRequests = mockBookingRequests.toMutableList()

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
                        BookingRequest(
                            booking = booking,
                            requesterName = "University Member",
                            requesterDepartment = "Department",
                            requesterPhone = "+254 700 000 000"
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
            delay(400)
            val request = allRequests.find { it.booking.id == bookingId }
            if (request != null) {
                _selectedRequest.value = UiState.Success(request)
            } else {
                _selectedRequest.value = UiState.Error("Request not found")
            }
        }
    }

    fun loadActiveDrivers() {
        viewModelScope.launch {
            _activeDrivers.value = UiState.Loading
            delay(600)
            _activeDrivers.value = UiState.Success(mockActiveDrivers)
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
            delay(500)
            _availableDrivers.value = UiState.Success(availableDrivers)
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
        onSuccess: () -> Unit
    ) {
        viewModelScope.launch {
            _assignState.value = UiState.Loading
            delay(1500)
            val index = allRequests.indexOfFirst {
                it.booking.id == bookingId
            }
            if (index != -1) {
                val updated = allRequests[index].copy(
                    booking = allRequests[index].booking.copy(
                        vehicleAssigned = vehicleReg,
                        driverAssigned = driverName
                    )
                )
                allRequests[index] = updated
                _selectedRequest.value = UiState.Success(updated)
            }
            _assignState.value = UiState.Success(Unit)
            onSuccess()
        }
    }

    fun resetApproveState() { _approveState.value = UiState.Idle }
    fun resetRejectState() { _rejectState.value = UiState.Idle }
    fun resetAssignState() { _assignState.value = UiState.Idle }
}