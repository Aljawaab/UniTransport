package com.example.unitransport.features.officer.presentation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.AssignmentTurnedIn
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.DirectionsBus
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.unitransport.core.base.UiState
import com.example.unitransport.core.ui.theme.StatusApproved
import com.example.unitransport.features.bookings.model.BookingRequestStatus

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RequestDetailScreen(
    bookingId: String,
    onNavigateBack: () -> Unit = {},
    onNavigateToAssignment: (String) -> Unit = {},
    viewModel: OfficerViewModel = hiltViewModel()
) {
    val requestState by viewModel.selectedRequest.collectAsState()
    val approveState by viewModel.approveState.collectAsState()
    val rejectState by viewModel.rejectState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(bookingId) {
        viewModel.loadRequestById(bookingId)
    }

    LaunchedEffect(approveState) {
        if (approveState is UiState.Success) {
            snackbarHostState.showSnackbar("Booking approved!")
            viewModel.resetApproveState()
        }
    }

    LaunchedEffect(rejectState) {
        if (rejectState is UiState.Success) {
            snackbarHostState.showSnackbar("Booking rejected.")
            viewModel.resetRejectState()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Request Details",
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = MaterialTheme.colorScheme.onPrimary
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary
                )
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
        containerColor = MaterialTheme.colorScheme.background
    ) { paddingValues ->

        when (val state = requestState) {
            is UiState.Loading -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }

            is UiState.Success -> {
                val request = state.data
                val booking = request.booking

                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentPadding = PaddingValues(bottom = 32.dp)
                ) {
                    // Status banner
                    item {
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            shape = MaterialTheme.shapes.large,
                            colors = CardDefaults.cardColors(
                                containerColor = when (booking.status) {
                                    BookingRequestStatus.APPROVED ->
                                        StatusApproved.copy(alpha = 0.1f)
                                    BookingRequestStatus.REJECTED ->
                                        MaterialTheme.colorScheme.errorContainer
                                    else ->
                                        MaterialTheme.colorScheme.primaryContainer
                                }
                            ),
                            elevation = CardDefaults.cardElevation(0.dp)
                        ) {
                            Column(
                                modifier = Modifier.padding(16.dp)
                            ) {
                                Text(
                                    text = booking.destination,
                                    style = MaterialTheme.typography.titleLarge,
                                    color = MaterialTheme.colorScheme.onBackground
                                )
                                Text(
                                    text = booking.purpose,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme
                                        .onSurfaceVariant
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = "Reference: ${booking.id}",
                                    style = MaterialTheme.typography.labelMedium,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }
                        }
                    }

                    // Booking details
                    item {
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp),
                            shape = MaterialTheme.shapes.large,
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.surface
                            ),
                            elevation = CardDefaults.cardElevation(2.dp)
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text(
                                    text = "Trip Details",
                                    style = MaterialTheme.typography.titleSmall,
                                    color = MaterialTheme.colorScheme.primary
                                )
                                Spacer(modifier = Modifier.height(12.dp))
                                DetailRow(
                                    icon = Icons.Filled.CalendarMonth,
                                    label = "Date",
                                    value = booking.departureDate
                                )
                                Divider(modifier = Modifier.padding(vertical = 8.dp))
                                DetailRow(
                                    icon = Icons.Filled.Schedule,
                                    label = "Departure",
                                    value = booking.departureTime
                                )
                                if (booking.returnTime.isNotBlank()) {
                                    Divider(modifier = Modifier.padding(vertical = 8.dp))
                                    DetailRow(
                                        icon = Icons.Filled.Schedule,
                                        label = "Return",
                                        value = booking.returnTime
                                    )
                                }
                                Divider(modifier = Modifier.padding(vertical = 8.dp))
                                DetailRow(
                                    icon = Icons.Filled.People,
                                    label = "Passengers",
                                    value = "${booking.passengerCount}"
                                )
                                Divider(modifier = Modifier.padding(vertical = 8.dp))
                                DetailRow(
                                    icon = Icons.Filled.DirectionsBus,
                                    label = "Preference",
                                    value = booking.vehiclePreference
                                )
                            }
                        }
                    }

                    // Requester info
                    item {
                        Spacer(modifier = Modifier.height(12.dp))
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp),
                            shape = MaterialTheme.shapes.large,
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.surface
                            ),
                            elevation = CardDefaults.cardElevation(2.dp)
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text(
                                    text = "Requested By",
                                    style = MaterialTheme.typography.titleSmall,
                                    color = MaterialTheme.colorScheme.primary
                                )
                                Spacer(modifier = Modifier.height(12.dp))
                                DetailRow(
                                    icon = Icons.Filled.Person,
                                    label = "Name",
                                    value = request.requesterName
                                )
                                Divider(modifier = Modifier.padding(vertical = 8.dp))
                                DetailRow(
                                    icon = Icons.Filled.Person,
                                    label = "Department",
                                    value = request.requesterDepartment
                                )
                                Divider(modifier = Modifier.padding(vertical = 8.dp))
                                DetailRow(
                                    icon = Icons.Filled.Person,
                                    label = "Phone",
                                    value = request.requesterPhone
                                )
                            }
                        }
                    }

                    // Assignment info if approved
                    if (booking.vehicleAssigned != null) {
                        item {
                            Spacer(modifier = Modifier.height(12.dp))
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 16.dp),
                                shape = MaterialTheme.shapes.large,
                                colors = CardDefaults.cardColors(
                                    containerColor =
                                        StatusApproved.copy(alpha = 0.08f)
                                ),
                                elevation = CardDefaults.cardElevation(0.dp)
                            ) {
                                Column(modifier = Modifier.padding(16.dp)) {
                                    Text(
                                        text = "Assignment",
                                        style = MaterialTheme.typography.titleSmall,
                                        color = StatusApproved
                                    )
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Text(
                                        text = "Vehicle: ${booking.vehicleAssigned}",
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                    if (booking.driverAssigned != null) {
                                        Text(
                                            text = "Driver: ${booking.driverAssigned}",
                                            style = MaterialTheme.typography.bodyMedium,
                                            color = MaterialTheme.colorScheme.onSurface
                                        )
                                    }
                                }
                            }
                        }
                    }

                    // Action buttons
                    item {
                        Spacer(modifier = Modifier.height(24.dp))
                        Column(
                            modifier = Modifier.padding(horizontal = 16.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            if (booking.status == BookingRequestStatus.PENDING) {
                                Button(
                                    onClick = {
                                        viewModel.approveRequest(booking.id) {}
                                    },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(52.dp),
                                    enabled = approveState !is UiState.Loading,
                                    shape = MaterialTheme.shapes.medium,
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = StatusApproved
                                    )
                                ) {
                                    if (approveState is UiState.Loading) {
                                        CircularProgressIndicator(
                                            modifier = Modifier
                                                .height(20.dp)
                                                .width(20.dp),
                                            color = MaterialTheme.colorScheme
                                                .onPrimary,
                                            strokeWidth = 2.dp
                                        )
                                    } else {
                                        Text(
                                            text = "Approve Request",
                                            style = MaterialTheme.typography
                                                .labelLarge,
                                            color = MaterialTheme.colorScheme
                                                .onPrimary
                                        )
                                    }
                                }

                                OutlinedButton(
                                    onClick = {
                                        viewModel.showRejectDialog(booking.id)
                                    },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(52.dp),
                                    enabled = rejectState !is UiState.Loading,
                                    shape = MaterialTheme.shapes.medium,
                                    colors = ButtonDefaults.outlinedButtonColors(
                                        contentColor =
                                            MaterialTheme.colorScheme.error
                                    ),
                                    border = androidx.compose.foundation.BorderStroke(
                                        1.dp,
                                        MaterialTheme.colorScheme.error
                                            .copy(alpha = 0.5f)
                                    )
                                ) {
                                    Text(
                                        text = "Reject Request",
                                        style = MaterialTheme.typography.labelLarge
                                    )
                                }
                            }

                            if (booking.status == BookingRequestStatus.APPROVED &&
                                booking.vehicleAssigned == null) {
                                Button(
                                    onClick = {
                                        onNavigateToAssignment(booking.id)
                                    },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(52.dp),
                                    shape = MaterialTheme.shapes.medium,
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor =
                                            MaterialTheme.colorScheme.primary
                                    )
                                ) {
                                    Icon(
                                        imageVector =
                                            Icons.Filled.AssignmentTurnedIn,
                                        contentDescription = null
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = "Assign Vehicle & Driver",
                                        style = MaterialTheme.typography.labelLarge
                                    )
                                }
                            }
                        }
                        Spacer(modifier = Modifier.height(16.dp))
                    }
                }
            }

            is UiState.Error -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Request not found",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.error,
                        textAlign = TextAlign.Center
                    )
                }
            }

            else -> Unit
        }
    }
}

@Composable
private fun DetailRow(
    icon: ImageVector,
    label: String,
    value: String
) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.height(18.dp).width(18.dp)
        )
        Spacer(modifier = Modifier.width(12.dp))
        Column {
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = value,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}