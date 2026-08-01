package com.example.unitransport.features.bookings.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.unitransport.core.base.UiState
import com.example.unitransport.data.repository.BookingRepository
import com.example.unitransport.features.bookings.model.Booking
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class BookingDetailViewModel @Inject constructor(
    private val bookingRepository: BookingRepository
) : ViewModel() {

    private val _bookingState =
        MutableStateFlow<UiState<Booking>>(UiState.Loading)
    val bookingState: StateFlow<UiState<Booking>> =
        _bookingState.asStateFlow()

    fun loadBooking(bookingId: String) {
        viewModelScope.launch {
            _bookingState.value = UiState.Loading
            try {
                bookingRepository.getBookingById(bookingId).collect { booking ->
                    _bookingState.value = if (booking != null) {
                        UiState.Success(booking)
                    } else {
                        UiState.Error("Booking not found")
                    }
                }
            } catch (e: Exception) {
                _bookingState.value = UiState.Error(
                    e.message ?: "Failed to load booking"
                )
            }
        }
    }
}