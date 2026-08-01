package com.example.unitransport.features.bookings.presentation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.unitransport.core.base.UiState
import com.example.unitransport.features.bookings.model.Booking
import com.example.unitransport.features.bookings.model.BookingRequestStatus

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BookingDetailScreen(
    bookingId: String,
    onNavigateBack: () -> Unit,
    viewModel: BookingDetailViewModel = hiltViewModel()
) {
    val bookingState by viewModel.bookingState.collectAsState()

    LaunchedEffect(bookingId) {
        viewModel.loadBooking(bookingId)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Booking Details") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentAlignment = Alignment.Center
        ) {
            when (val state = bookingState) {
                is UiState.Loading, UiState.Idle -> {
                    CircularProgressIndicator()
                }
                is UiState.Error -> {
                    Text(
                        text = state.message,
                        color = MaterialTheme.colorScheme.error
                    )
                }
                is UiState.Success -> {
                    BookingDetailContent(booking = state.data)
                }
            }
        }
    }
}

@Composable
private fun BookingDetailContent(booking: Booking) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item { StatusBadge(status = booking.status) }

        item {
            DetailCard(title = "Trip Info") {
                DetailRow("Destination", booking.destination)
                DetailRow("Purpose", booking.purpose)
                DetailRow("Passengers", booking.passengerCount.toString())
                DetailRow("Vehicle Preference", booking.vehiclePreference)
            }
        }

        item {
            DetailCard(title = "Schedule") {
                DetailRow("Departure Date", booking.departureDate)
                DetailRow("Departure Time", booking.departureTime)
                DetailRow("Return Date", booking.returnDate)
                DetailRow("Return Time", booking.returnTime)
            }
        }

        if (booking.vehicleAssigned != null || booking.driverAssigned != null) {
            item {
                DetailCard(title = "Assignment") {
                    DetailRow(
                        "Vehicle",
                        booking.vehicleAssigned ?: "Not yet assigned"
                    )
                    DetailRow(
                        "Driver",
                        booking.driverAssigned ?: "Not yet assigned"
                    )
                }
            }
        }

        if (booking.additionalNotes.isNotBlank()) {
            item {
                DetailCard(title = "Additional Notes") {
                    Text(
                        text = booking.additionalNotes,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        }
    }
}

@Composable
private fun StatusBadge(status: BookingRequestStatus) {
    val (bg, fg) = when (status) {
        BookingRequestStatus.PENDING -> Color(0xFFFFF3CD) to Color(0xFF856404)
        BookingRequestStatus.APPROVED -> Color(0xFFD4EDDA) to Color(0xFF155724)
        BookingRequestStatus.REJECTED -> Color(0xFFF8D7DA) to Color(0xFF721C24)
        BookingRequestStatus.COMPLETED -> Color(0xFFD1ECF1) to Color(0xFF0C5460)
        BookingRequestStatus.ACTIVE -> Color(0xFFCCE5FF) to Color(0xFF004085)
        BookingRequestStatus.CANCELLED -> Color(0xFFE2E3E5) to Color(0xFF383D41)
    }
    Surface(
        color = bg,
        shape = MaterialTheme.shapes.small
    ) {
        Text(
            text = status.name,
            color = fg,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
        )
    }
}

@Composable
private fun DetailCard(
    title: String,
    content: @Composable ColumnScopeHolder.() -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Column(modifier = Modifier.padding(top = 8.dp)) {
                ColumnScopeHolder.content()
            }
        }
    }
}

// Small helper object so DetailCard's content lambda can call DetailRow directly
private object ColumnScopeHolder

@Composable
private fun DetailRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Medium
        )
    }
}