package com.example.unitransport.features.driver.presentation

import androidx.compose.foundation.background
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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.DirectionsBus
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.ReportProblem
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
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.unitransport.core.base.UiState
import com.example.unitransport.core.ui.theme.StatusApproved
import com.example.unitransport.core.ui.theme.StatusPending
import com.example.unitransport.core.ui.theme.StatusRejected
import com.example.unitransport.features.driver.model.LiveLocation
import com.example.unitransport.features.driver.model.Trip
import com.example.unitransport.features.driver.model.TripStatus

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TripDetailScreen(
    tripId: String,
    onNavigateBack: () -> Unit = {},
    onNavigateToIssueReport: (String) -> Unit = {},
    onNavigateToRatePassengers: () -> Unit = {},
    viewModel: DriverViewModel = hiltViewModel()
) {
    val tripState by viewModel.selectedTrip.collectAsState()
    val liveLocation by viewModel.liveLocation.collectAsState()
    val tripUpdateState by viewModel.tripUpdateState.collectAsState()

    LaunchedEffect(key1=tripId) {
        viewModel.loadTripById(tripId)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Trip Details",
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
        containerColor = MaterialTheme.colorScheme.background
    ) { paddingValues ->

        when (val state = tripState) {
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
                TripDetailContent(
                    trip = state.data,
                    liveLocation = liveLocation,
                    isLocationSharing = viewModel.isLocationSharing,
                    isUpdating = tripUpdateState is UiState.Loading,
                    paddingValues = paddingValues,
                    onToggleLocation = { viewModel.toggleLocationSharing() },
                    onStartTrip = { viewModel.startTrip(state.data.id) },
                    onCompleteTrip = { viewModel.completeTrip(state.data.id) },
                    onReportIssue = {
                        onNavigateToIssueReport(state.data.id)
                    },
                    onNavigateToRatePassengers = {
                        onNavigateToRatePassengers()
                    }
                )
            }

            is UiState.Error -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Trip not found",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.error
                    )
                }
            }

            else -> Unit
        }
    }
}

@Composable
private fun TripDetailContent(
    trip: Trip,
    liveLocation: LiveLocation?,
    isLocationSharing: Boolean,
    isUpdating: Boolean,
    paddingValues: PaddingValues,
    onToggleLocation: () -> Unit,
    onStartTrip: () -> Unit,
    onCompleteTrip: () -> Unit,
    onReportIssue: () -> Unit,
    onNavigateToRatePassengers: () -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues),
        contentPadding = PaddingValues(bottom = 32.dp)
    ) {
        // Status header
        item {
            val (statusColor, statusText) = when (trip.status) {
                TripStatus.UPCOMING -> StatusPending to "Upcoming"
                TripStatus.IN_PROGRESS ->
                    MaterialTheme.colorScheme.primary to "In Progress"
                TripStatus.COMPLETED -> StatusApproved to "Completed"
                TripStatus.CANCELLED -> StatusRejected to "Cancelled"
            }

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                shape = MaterialTheme.shapes.large,
                colors = CardDefaults.cardColors(
                    containerColor = statusColor.copy(alpha = 0.12f)
                ),
                elevation = CardDefaults.cardElevation(0.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = trip.destination,
                            style = MaterialTheme.typography.titleLarge,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                        Text(
                            text = trip.purpose,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    Box(
                        modifier = Modifier
                            .clip(MaterialTheme.shapes.small)
                            .background(statusColor.copy(alpha = 0.15f))
                            .padding(horizontal = 10.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = statusText,
                            style = MaterialTheme.typography.labelLarge,
                            color = statusColor
                        )
                    }
                }
            }
        }

        // Trip info card
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
                        text = "Trip Information",
                        style = MaterialTheme.typography.titleSmall,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    TripInfoRow(
                        icon = Icons.Filled.Schedule,
                        label = "Departure",
                        value = "${trip.date} at ${trip.departureTime}"
                    )
                    Divider(
                        modifier = Modifier.padding(vertical = 8.dp),
                        color = MaterialTheme.colorScheme.outlineVariant
                            .copy(alpha = 0.5f)
                    )
                    TripInfoRow(
                        icon = Icons.Filled.Schedule,
                        label = "Return",
                        value = trip.returnTime
                    )
                    Divider(
                        modifier = Modifier.padding(vertical = 8.dp),
                        color = MaterialTheme.colorScheme.outlineVariant
                            .copy(alpha = 0.5f)
                    )
                    TripInfoRow(
                        icon = Icons.Filled.People,
                        label = "Passengers",
                        value = "${trip.passengerCount} passengers"
                    )
                    Divider(
                        modifier = Modifier.padding(vertical = 8.dp),
                        color = MaterialTheme.colorScheme.outlineVariant
                            .copy(alpha = 0.5f)
                    )
                    TripInfoRow(
                        icon = Icons.Filled.Place,
                        label = "Pickup Point",
                        value = trip.pickupPoint
                    )
                    Divider(
                        modifier = Modifier.padding(vertical = 8.dp),
                        color = MaterialTheme.colorScheme.outlineVariant
                            .copy(alpha = 0.5f)
                    )
                    TripInfoRow(
                        icon = Icons.Filled.DirectionsBus,
                        label = "Vehicle",
                        value = "${trip.vehicleRegistration} — " +
                                "${trip.vehicleMake} ${trip.vehicleModel}"
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
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(44.dp)
                                .clip(CircleShape)
                                .background(
                                    MaterialTheme.colorScheme.primaryContainer
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Person,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = trip.requesterName,
                                style = MaterialTheme.typography.titleSmall,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = trip.requesterPhone,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        IconButton(onClick = {}) {
                            Icon(
                                imageVector = Icons.Filled.Call,
                                contentDescription = "Call requester",
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                }
            }
        }

        // Live Location Card
        if (trip.status == TripStatus.IN_PROGRESS) {
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
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.LocationOn,
                                    contentDescription = null,
                                    tint = if (isLocationSharing)
                                        StatusApproved
                                    else
                                        MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.size(22.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "Share My Location",
                                    style = MaterialTheme.typography.titleSmall,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            }
                            Switch(
                                checked = isLocationSharing,
                                onCheckedChange = { onToggleLocation() },
                                colors = SwitchDefaults.colors(
                                    checkedThumbColor =
                                        MaterialTheme.colorScheme.onPrimary,
                                    checkedTrackColor =
                                        StatusApproved
                                )
                            )
                        }

                        if (isLocationSharing && liveLocation != null) {
                            Spacer(modifier = Modifier.height(12.dp))
                            Divider(
                                color = MaterialTheme.colorScheme.outlineVariant
                                    .copy(alpha = 0.5f)
                            )
                            Spacer(modifier = Modifier.height(12.dp))

                            // Simulated map placeholder
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(180.dp)
                                    .clip(MaterialTheme.shapes.large)
                                    .background(
                                        MaterialTheme.colorScheme.primaryContainer
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Icon(
                                        imageVector = Icons.Filled.LocationOn,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.primary,
                                        modifier = Modifier.size(48.dp)
                                    )
                                    Text(
                                        text = "Live Map View",
                                        style = MaterialTheme.typography
                                            .titleSmall,
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                    Text(
                                        text = "(Google Maps integrates here in Step 16)",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme
                                            .onSurfaceVariant,
                                        textAlign = TextAlign.Center
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(12.dp))

                            // Live coordinates
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                LocationDataItem(
                                    label = "Latitude",
                                    value = "%.4f".format(
                                        liveLocation.latitude
                                    )
                                )
                                LocationDataItem(
                                    label = "Longitude",
                                    value = "%.4f".format(
                                        liveLocation.longitude
                                    )
                                )
                                LocationDataItem(
                                    label = "Speed",
                                    value = "${"%.0f".format(
                                        liveLocation.speed
                                    )} km/h"
                                )
                                LocationDataItem(
                                    label = "Time",
                                    value = liveLocation.timestamp
                                )
                            }
                        } else if (!isLocationSharing) {
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "Toggle on to share your live location " +
                                        "with the transport office.",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
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
                when (trip.status) {
                    TripStatus.UPCOMING -> {
                        Button(
                            onClick = onStartTrip,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(52.dp),
                            enabled = !isUpdating,
                            shape = MaterialTheme.shapes.medium,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.primary
                            )
                        ) {
                            if (isUpdating) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(20.dp),
                                    color = MaterialTheme.colorScheme.onPrimary,
                                    strokeWidth = 2.dp
                                )
                            } else {
                                Icon(
                                    imageVector = Icons.Filled.PlayArrow,
                                    contentDescription = null
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "Start Trip",
                                    style = MaterialTheme.typography.labelLarge
                                )
                            }
                        }
                    }

                    TripStatus.IN_PROGRESS -> {
                        Button(
                            onClick = onCompleteTrip,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(52.dp),
                            enabled = !isUpdating,
                            shape = MaterialTheme.shapes.medium,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = StatusApproved
                            )
                        ) {
                            if (isUpdating) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(20.dp),
                                    color = MaterialTheme.colorScheme.onPrimary,
                                    strokeWidth = 2.dp
                                )
                            } else {
                                Icon(
                                    imageVector = Icons.Filled.CheckCircle,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.onPrimary
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "Mark as Completed",
                                    style = MaterialTheme.typography.labelLarge,
                                    color = MaterialTheme.colorScheme.onPrimary
                                )
                            }
                        }
                    }

                    else -> Unit
                }

                if (trip.status != TripStatus.COMPLETED) {
                    OutlinedButton(
                        onClick = onReportIssue,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp),
                        shape = MaterialTheme.shapes.medium,
                        colors = androidx.compose.material3.ButtonDefaults
                            .outlinedButtonColors(
                                contentColor = MaterialTheme.colorScheme.error
                            ),
                        border = androidx.compose.foundation.BorderStroke(
                            1.dp,
                            MaterialTheme.colorScheme.error.copy(alpha = 0.5f)
                        )
                    ) {
                        Icon(
                            imageVector = Icons.Filled.ReportProblem,
                            contentDescription = null
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Report Vehicle Issue",
                            style = MaterialTheme.typography.labelLarge
                        )
                    }
                }

                // Add inside the action buttons section
// after TripStatus.COMPLETED case

                if (trip.status == TripStatus.COMPLETED) {
                    Button(
                        onClick = onNavigateToRatePassengers,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp),
                        shape = MaterialTheme.shapes.medium,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = StatusPending
                        )
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Star,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onPrimary
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Rate Passengers",
                            style = MaterialTheme.typography.labelLarge,
                            color = MaterialTheme.colorScheme.onPrimary
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
private fun TripInfoRow(
    icon: ImageVector,
    label: String,
    value: String
) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(18.dp)
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

@Composable
private fun LocationDataItem(label: String, value: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = value,
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.primary
        )
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}