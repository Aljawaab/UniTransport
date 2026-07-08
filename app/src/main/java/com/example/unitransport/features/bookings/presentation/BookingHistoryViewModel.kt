package com.example.unitransport.features.bookings.presentation

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.unitransport.core.base.UiState
import com.example.unitransport.features.bookings.model.Booking
import com.example.unitransport.features.bookings.model.BookingRequestStatus
import com.example.unitransport.features.bookings.model.mockBookings
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class BookingHistoryViewModel @Inject constructor() : ViewModel() {

    private val _bookingsState =
        MutableStateFlow<UiState<List<Booking>>>(UiState.Loading)
    val bookingsState: StateFlow<UiState<List<Booking>>> =
        _bookingsState.asStateFlow()

    var selectedTabIndex by mutableIntStateOf(0)
        private set

    private val allBookings = mockBookings

    val tabs = listOf("All", "Pending", "Approved", "Completed", "Rejected")

    fun loadBookings() {
        viewModelScope.launch {
            _bookingsState.value = UiState.Loading
            delay(800)
            _bookingsState.value = UiState.Success(allBookings)
        }
    }

    fun onTabSelected(index: Int) {
        selectedTabIndex = index
    }

    fun getFilteredBookings(bookings: List<Booking>): List<Booking> {
        return when (selectedTabIndex) {
            0 -> bookings
            1 -> bookings.filter {
                it.status == BookingRequestStatus.PENDING
            }
            2 -> bookings.filter {
                it.status == BookingRequestStatus.APPROVED
            }
            3 -> bookings.filter {
                it.status == BookingRequestStatus.COMPLETED
            }
            4 -> bookings.filter {
                it.status == BookingRequestStatus.REJECTED
            }
            else -> bookings
        }
    }
}