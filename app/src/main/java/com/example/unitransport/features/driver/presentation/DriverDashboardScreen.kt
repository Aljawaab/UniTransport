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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.DirectionsBus
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.unitransport.core.base.UiState
import com.example.unitransport.core.ui.theme.StatusApproved
import com.example.unitransport.core.ui.theme.StatusPending
import com.example.unitransport.core.ui.theme.StatusRejected
import com.example.unitransport.features.driver.model.Trip
import com.example.unitransport.features.driver.model.TripStatus

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DriverDashboardScreen(
    onNavigateToTripDetail: (String) -> Unit = {},
    onNavigateToNotifications: () -> Unit = {},
    onNavigateToProfile: () -> Unit = {},
    viewModel: DriverViewModel = hiltViewModel()
) {
    val tripsState by viewModel.tripsState.collectAsState()
    val liveLocation by viewModel.liveLocation.collectAsState()

    LaunchedEffect(key1=Unit) {
        viewModel.loadTrips()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "UniTransport",
                            style = MaterialTheme.typography.titleLarge,
                            color = MaterialTheme.colorScheme.onPrimary
                        )
                        Text(
                            text = "Driver Portal",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onPrimary
                                .copy(alpha = 0.8f)
                        )
                    }
                },
                actions = {
                    BadgedBox(
                        badge = { Badge { Text("1") } }
                    ) {
                        IconButton(onClick = onNavigateToNotifications) {
                            Icon(
                                imageVector = Icons.Filled.Notifications,
                                contentDescription = "Notifications",
                                tint = MaterialTheme.colorScheme.onPrimary
                            )
                        }
                    }
                    IconButton(onClick = onNavigateToProfile) {
                        Icon(
                            imageVector = Icons.Filled.Person,
                            contentDescription = "Profile",
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

        when (val state = tripsState) {
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
                val trips = state.data
                val activeTrip = trips.find {
                    it.status == TripStatus.IN_PROGRESS
                }
                val upcomingTrips = trips.filter {
                    it.status == TripStatus.UPCOMING
                }
                val completedCount = trips.count {
                    it.status == TripStatus.COMPLETED
                }

                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentPadding = PaddingValues(bottom = 32.dp)
                ) {
                    // Driver welcome card
                    item {
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            shape = MaterialTheme.shapes.large,
                            colors = CardDefaults.cardColors(
                                containerColor =
                                    MaterialTheme.colorScheme.primaryContainer
                            )
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(20.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(56.dp)
                                        .clip(CircleShape)
                                        .background(
                                            MaterialTheme.colorScheme.primary
                                                .copy(alpha = 0.15f)
                                        ),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = "JK",
                                        style = MaterialTheme.typography
                                            .titleLarge,
                                        color = MaterialTheme.colorScheme
                                            .primary
                                    )
                                }
                                Spacer(modifier = Modifier.width(16.dp))
                                Column {
                                    Text(
                                        text = "Jamil Daulini",
                                        style = MaterialTheme.typography
                                            .titleMedium,
                                        color = MaterialTheme.colorScheme
                                            .onPrimaryContainer
                                    )
                                    Text(
                                        text = "KDA 123A — Toyota Coaster",
                                        style = MaterialTheme.typography
                                            .bodySmall,
                                        color = MaterialTheme.colorScheme
                                            .onPrimaryContainer
                                            .copy(alpha = 0.7f)
                                    )
                                    Text(
                                        text = "License: DL/2020/00123",
                                        style = MaterialTheme.typography
                                            .labelSmall,
                                        color = MaterialTheme.colorScheme
                                            .primary
                                    )
                                }
                            }
                        }
                    }

                    // Stats row
                    item {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            DriverStatCard(
                                icon = Icons.Filled.PlayArrow,
                                value = "${upcomingTrips.size}",
                                label = "Upcoming",
                                color = StatusPending,
                                modifier = Modifier.weight(1f)
                            )
                            DriverStatCard(
                                icon = Icons.Filled.DirectionsBus,
                                value = if (activeTrip != null) "1" else "0",
                                label = "Active",
                                color = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.weight(1f)
                            )
                            DriverStatCard(
                                icon = Icons.Filled.CheckCircle,
                                value = "$completedCount",
                                label = "Completed",
                                color = StatusApproved,
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }

                    // Active trip section
                    if (activeTrip != null) {
                        item {
                            Spacer(modifier = Modifier.height(24.dp))
                            Text(
                                text = "Active Trip",
                                style = MaterialTheme.typography.titleMedium,
                                color = MaterialTheme.colorScheme.onBackground,
                                modifier = Modifier.padding(horizontal = 16.dp)
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                        }

                        item {
                            ActiveTripCard(
                                trip = activeTrip,
                                isSharing = viewModel.isLocationSharing,
                                liveLocation = liveLocation,
                                onTripClick = {
                                    onNavigateToTripDetail(activeTrip.id)
                                },
                                onToggleLocation = {
                                    viewModel.toggleLocationSharing()
                                }
                            )
                        }
                    }

                    // Upcoming trips
                    if (upcomingTrips.isNotEmpty()) {
                        item {
                            Spacer(modifier = Modifier.height(24.dp))
                            Text(
                                text = "Upcoming Trips",
                                style = MaterialTheme.typography.titleMedium,
                                color = MaterialTheme.colorScheme.onBackground,
                                modifier = Modifier.padding(horizontal = 16.dp)
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                        }

                        items(upcomingTrips) { trip ->
                            TripListCard(
                                trip = trip,
                                onClick = { onNavigateToTripDetail(trip.id) }
                            )
                        }
                    }

                    // Today's summary
                    item {
                        Spacer(modifier = Modifier.height(24.dp))
                        Text(
                            text = "All Trips Today",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onBackground,
                            modifier = Modifier.padding(horizontal = 16.dp)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                    }

                    items(trips) { trip ->
                        TripListCard(
                            trip = trip,
                            onClick = { onNavigateToTripDetail(trip.id) }
                        )
                    }
                }
            }

            else -> Unit
        }
    }
}

@Composable
private fun DriverStatCard(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    value: String,
    label: String,
    color: androidx.compose.ui.graphics.Color,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = MaterialTheme.shapes.large,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = color,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = value,
                style = MaterialTheme.typography.titleLarge,
                color = color
            )
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ActiveTripCard(
    trip: Trip,
    isSharing: Boolean,
    liveLocation: com.example.unitransport.features.driver.model.LiveLocation?,
    onTripClick: () -> Unit,
    onToggleLocation: () -> Unit
) {
    Card(
        onClick = onTripClick,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        shape = MaterialTheme.shapes.large,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primary
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "IN PROGRESS",
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.onPrimary
                        .copy(alpha = 0.8f)
                )
                // Live indicator
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .clip(CircleShape)
                            .background(
                                if (isSharing) StatusApproved
                                else StatusRejected
                            )
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = if (isSharing) "LIVE" else "OFF",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = trip.destination,
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onPrimary
            )

            Text(
                text = trip.purpose,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.8f)
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Live location display
            if (isSharing && liveLocation != null) {
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.onPrimary
                            .copy(alpha = 0.15f)
                    ),
                    shape = MaterialTheme.shapes.medium
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Filled.LocationOn,
                                contentDescription = null,
                                tint = StatusApproved,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Live Location",
                                style = MaterialTheme.typography.labelLarge,
                                color = MaterialTheme.colorScheme.onPrimary
                            )
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "Lat: ${"%.4f".format(liveLocation.latitude)}" +
                                    "  Lng: ${"%.4f".format(liveLocation.longitude)}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onPrimary
                                .copy(alpha = 0.9f)
                        )
                        Text(
                            text = "Speed: ${"%.0f".format(liveLocation.speed)} km/h" +
                                    "  •  ${liveLocation.timestamp}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onPrimary
                                .copy(alpha = 0.7f)
                        )
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
            }

            // Share location button
            androidx.compose.material3.OutlinedButton(
                onClick = onToggleLocation,
                modifier = Modifier.fillMaxWidth(),
                colors = androidx.compose.material3.ButtonDefaults
                    .outlinedButtonColors(
                        contentColor = MaterialTheme.colorScheme.onPrimary
                    ),
                border = androidx.compose.foundation.BorderStroke(
                    1.dp,
                    MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.5f)
                )
            ) {
                Icon(
                    imageVector = Icons.Filled.LocationOn,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = if (isSharing)
                        "Stop Sharing Location"
                    else
                        "Share My Location",
                    style = MaterialTheme.typography.labelLarge
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TripListCard(
    trip: Trip,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val statusColor = when (trip.status) {
        TripStatus.UPCOMING -> StatusPending
        TripStatus.IN_PROGRESS -> MaterialTheme.colorScheme.primary
        TripStatus.COMPLETED -> StatusApproved
        TripStatus.CANCELLED -> StatusRejected
    }

    Card(
        onClick = onClick,
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp),
        shape = MaterialTheme.shapes.large,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(MaterialTheme.shapes.medium)
                    .background(statusColor.copy(alpha = 0.12f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Filled.DirectionsBus,
                    contentDescription = null,
                    tint = statusColor,
                    modifier = Modifier.size(24.dp)
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = trip.destination,
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = "${trip.date} • ${trip.departureTime}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = "${trip.passengerCount} passengers",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Box(
                modifier = Modifier
                    .clip(MaterialTheme.shapes.extraSmall)
                    .background(statusColor.copy(alpha = 0.12f))
                    .padding(horizontal = 8.dp, vertical = 4.dp)
            ) {
                Text(
                    text = trip.status.displayName,
                    style = MaterialTheme.typography.labelSmall,
                    color = statusColor
                )
            }
        }
    }
}