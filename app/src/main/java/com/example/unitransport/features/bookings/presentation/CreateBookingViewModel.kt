package com.example.unitransport.features.bookings.presentation

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.unitransport.core.base.UiState
import com.example.unitransport.features.bookings.model.Booking
import com.example.unitransport.features.bookings.model.BookingRequestStatus
import com.example.unitransport.data.repository.BookingRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CreateBookingViewModel @Inject constructor(
    private val bookingRepository: BookingRepository
) : ViewModel() {

    // Step tracker
    var currentStep by mutableIntStateOf(0)
        private set

    // Step 1 fields
    var destination by mutableStateOf("")
        private set
    var purpose by mutableStateOf("")
        private set
    var passengerCount by mutableIntStateOf(1)
        private set
    var additionalNotes by mutableStateOf("")
        private set

    // Step 2 fields
    var departureDate by mutableStateOf("")
        private set
    var departureTime by mutableStateOf("")
        private set
    var returnDate by mutableStateOf("")
        private set
    var returnTime by mutableStateOf("")
        private set

    // Step 3 fields
    var vehiclePreference by mutableStateOf("No Preference")
        private set

    // Validation errors
    var destinationError by mutableStateOf<String?>(null)
        private set
    var purposeError by mutableStateOf<String?>(null)
        private set
    var departureDateError by mutableStateOf<String?>(null)
        private set
    var departureTimeError by mutableStateOf<String?>(null)
        private set

    // Submission state
    private val _submitState = MutableStateFlow<UiState<Booking>>(UiState.Idle)
    val submitState: StateFlow<UiState<Booking>> = _submitState.asStateFlow()

    val vehiclePreferences = listOf(
        "No Preference", "Bus", "Minibus", "Van", "SUV", "Sedan"
    )

    val timeSlots = listOf(
        "06:00 AM", "07:00 AM", "08:00 AM", "09:00 AM",
        "10:00 AM", "11:00 AM", "12:00 PM", "01:00 PM",
        "02:00 PM", "03:00 PM", "04:00 PM", "05:00 PM",
        "06:00 PM", "07:00 PM", "08:00 PM"
    )

    // Field update functions
    fun onDestinationChange(value: String) {
        destination = value
        destinationError = null
    }

    fun onPurposeChange(value: String) {
        purpose = value
        purposeError = null
    }

    fun onPassengerCountChange(value: Int) {
        if (value in 1..100) passengerCount = value
    }

    fun onAdditionalNotesChange(value: String) {
        additionalNotes = value
    }

    fun onDepartureDateChange(value: String) {
        departureDate = value
        departureDateError = null
    }

    fun onDepartureTimeChange(value: String) {
        departureTime = value
        departureTimeError = null
    }

    fun onReturnDateChange(value: String) { returnDate = value }
    fun onReturnTimeChange(value: String) { returnTime = value }
    fun onVehiclePreferenceChange(value: String) { vehiclePreference = value }

    // Step validation
    private fun validateStep1(): Boolean {
        var valid = true
        if (destination.isBlank()) {
            destinationError = "Destination is required"
            valid = false
        }
        if (purpose.isBlank()) {
            purposeError = "Purpose of trip is required"
            valid = false
        }
        return valid
    }

    private fun validateStep2(): Boolean {
        var valid = true
        if (departureDate.isBlank()) {
            departureDateError = "Departure date is required"
            valid = false
        }
        if (departureTime.isBlank()) {
            departureTimeError = "Departure time is required"
            valid = false
        }
        return valid
    }

    fun nextStep(): Boolean {
        return when (currentStep) {
            0 -> if (validateStep1()) { currentStep++; true } else false
            1 -> if (validateStep2()) { currentStep++; true } else false
            2 -> { currentStep++; true }
            else -> false
        }
    }

    fun previousStep() {
        if (currentStep > 0) currentStep--
    }

    fun submitBooking(onSuccess: (Booking) -> Unit) {
        viewModelScope.launch {
            _submitState.value = UiState.Loading
            val booking = Booking(
                id = "",
                destination = destination,
                purpose = purpose,
                passengerCount = passengerCount,
                departureDate = departureDate,
                departureTime = departureTime,
                returnDate = returnDate,
                returnTime = returnTime,
                vehiclePreference = vehiclePreference,
                additionalNotes = additionalNotes,
                status = BookingRequestStatus.PENDING,
                createdAt = System.currentTimeMillis().toString()
            )
            val result = bookingRepository.createBooking(booking)
            result.fold(
                onSuccess = { bookingId ->
                    val created = booking.copy(id = bookingId)
                    _submitState.value = UiState.Success(created)
                    onSuccess(created)
                },
                onFailure = { error ->
                    _submitState.value = UiState.Error(
                        error.message ?: "Failed to create booking"
                    )
                }
            )
        }
    }

    fun resetForm() {
        currentStep = 0
        destination = ""
        purpose = ""
        passengerCount = 1
        additionalNotes = ""
        departureDate = ""
        departureTime = ""
        returnDate = ""
        returnTime = ""
        vehiclePreference = "No Preference"
        _submitState.value = UiState.Idle
    }
}