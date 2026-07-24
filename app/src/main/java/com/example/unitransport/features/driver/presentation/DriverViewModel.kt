package com.example.unitransport.features.driver.presentation

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.unitransport.core.base.UiState
import com.example.unitransport.features.driver.model.IssueCategory
import com.example.unitransport.features.driver.model.IssueSeverity
import com.example.unitransport.features.driver.model.LiveLocation
import com.example.unitransport.features.driver.model.Trip
import com.example.unitransport.features.driver.model.TripStatus
import com.example.unitransport.features.driver.model.VehicleIssue
import com.example.unitransport.features.driver.model.mockTrips
import com.example.unitransport.features.driver.model.simulatedRouteCoordinates
import com.example.unitransport.data.repository.LocationRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DriverViewModel @Inject constructor(
    private val locationRepository: LocationRepository
) : ViewModel() {

    // Trips state
    private val _tripsState =
        MutableStateFlow<UiState<List<Trip>>>(UiState.Loading)
    val tripsState: StateFlow<UiState<List<Trip>>> =
        _tripsState.asStateFlow()

    // Selected trip
    private val _selectedTrip =
        MutableStateFlow<UiState<Trip>>(UiState.Idle)
    val selectedTrip: StateFlow<UiState<Trip>> =
        _selectedTrip.asStateFlow()

    // Live location simulation
    private val _liveLocation =
        MutableStateFlow<LiveLocation?>(null)
    val liveLocation: StateFlow<LiveLocation?> =
        _liveLocation.asStateFlow()

    // Location sharing toggle
    var isLocationSharing by mutableStateOf(false)
        private set

    // Current route index for simulation
    private var routeIndex = 0
    private var locationJob: Job? = null

    // Trip update state
    private val _tripUpdateState =
        MutableStateFlow<UiState<Unit>>(UiState.Idle)
    val tripUpdateState: StateFlow<UiState<Unit>> =
        _tripUpdateState.asStateFlow()

    // Issue report fields
    var issueCategory by mutableStateOf(IssueCategory.MECHANICAL)
        private set
    var issueDescription by mutableStateOf("")
        private set
    var issueSeverity by mutableStateOf(IssueSeverity.LOW)
        private set
    var issueDescriptionError by mutableStateOf<String?>(null)
        private set

    // Issue submit state
    private val _issueState =
        MutableStateFlow<UiState<Unit>>(UiState.Idle)
    val issueState: StateFlow<UiState<Unit>> =
        _issueState.asStateFlow()

    private val allTrips = mockTrips.toMutableList()

    fun loadTrips() {
        viewModelScope.launch {
            _tripsState.value = UiState.Loading
            delay(800)
            _tripsState.value = UiState.Success(allTrips)
        }
    }

    fun loadTripById(tripId: String) {
        viewModelScope.launch {
            _selectedTrip.value = UiState.Loading
            delay(400)
            val trip = allTrips.find { it.id == tripId }
            if (trip != null) {
                _selectedTrip.value = UiState.Success(trip)
            } else {
                _selectedTrip.value = UiState.Error("Trip not found")
            }
        }
    }

    // Driver ID — in production comes from Firebase Auth
    private val driverId = "D001"

    fun toggleLocationSharing() {
        isLocationSharing = !isLocationSharing
        if (isLocationSharing) {
            startLocationUpdates()
        } else {
            stopLocationUpdates()
        }
    }

    private fun startLocationUpdates() {
        routeIndex = 0
        locationJob?.cancel()
        locationJob = viewModelScope.launch {
            try {
                while (isLocationSharing) {
                    if (routeIndex >= simulatedRouteCoordinates.size) {
                        routeIndex = 0
                    }
                    val coordinate = simulatedRouteCoordinates[routeIndex]
                    val location = coordinate.copy(
                        isSharing = true,
                        timestamp = getCurrentTime(routeIndex)
                    )
                    _liveLocation.value = location

                    // Push to Firebase Realtime Database
                    locationRepository.updateDriverLocation(
                        driverId = driverId,
                        location = location
                    )

                    routeIndex++
                    kotlinx.coroutines.delay(3000)
                }
            } catch (e: Exception) {
                // Coroutine cancelled cleanly
            }
        }
    }

    private fun stopLocationUpdates() {
        locationJob?.cancel()
        viewModelScope.launch {
            locationRepository.stopSharing(driverId)
            _liveLocation.value = _liveLocation.value?.copy(
                isSharing = false
            )
        }
    }

    private fun getCurrentTime(index: Int): String {
        val times = listOf(
            "08:00 AM", "08:03 AM", "08:06 AM",
            "08:09 AM", "08:12 AM", "08:15 AM",
            "08:18 AM", "08:21 AM", "08:24 AM",
            "08:27 AM"
        )
        return times.getOrElse(index) { "08:30 AM" }
    }

    fun startTrip(tripId: String) {
        viewModelScope.launch {
            _tripUpdateState.value = UiState.Loading
            delay(1000)
            val index = allTrips.indexOfFirst { it.id == tripId }
            if (index != -1) {
                allTrips[index] = allTrips[index].copy(
                    status = TripStatus.IN_PROGRESS
                )
                _selectedTrip.value = UiState.Success(allTrips[index])
                _tripsState.value = UiState.Success(allTrips.toList())
            }
            _tripUpdateState.value = UiState.Success(Unit)
        }
    }

    fun completeTrip(tripId: String) {
        viewModelScope.launch {
            _tripUpdateState.value = UiState.Loading
            delay(1000)
            stopLocationUpdates()
            isLocationSharing = false
            val index = allTrips.indexOfFirst { it.id == tripId }
            if (index != -1) {
                allTrips[index] = allTrips[index].copy(
                    status = TripStatus.COMPLETED
                )
                _selectedTrip.value = UiState.Success(allTrips[index])
                _tripsState.value = UiState.Success(allTrips.toList())
            }
            _tripUpdateState.value = UiState.Success(Unit)
        }
    }

    fun onIssueCategoryChange(category: IssueCategory) {
        issueCategory = category
    }

    fun onIssueDescriptionChange(value: String) {
        issueDescription = value
        issueDescriptionError = null
    }

    fun onIssueSeverityChange(severity: IssueSeverity) {
        issueSeverity = severity
    }

    fun submitIssue(tripId: String, onSuccess: () -> Unit) {
        if (issueDescription.isBlank()) {
            issueDescriptionError = "Please describe the issue"
            return
        }
        viewModelScope.launch {
            _issueState.value = UiState.Loading
            delay(1500)
            _issueState.value = UiState.Success(Unit)
            onSuccess()
        }
    }

    fun resetIssueState() { _issueState.value = UiState.Idle }
    fun resetTripUpdateState() {
        _tripUpdateState.value = UiState.Idle
    }

    override fun onCleared() {
        super.onCleared()
        locationJob?.cancel()
    }
}