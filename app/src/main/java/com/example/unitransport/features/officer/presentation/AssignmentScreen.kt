package com.example.unitransport.features.officer.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.DirectionsBus
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.unitransport.core.base.UiState
import com.example.unitransport.core.ui.theme.StatusApproved
import com.example.unitransport.core.ui.theme.StatusRejected
import com.example.unitransport.features.officer.model.DriverOption
import com.example.unitransport.features.vehicles.model.Vehicle
import com.example.unitransport.core.ui.components.StarRatingDisplay
import com.example.unitransport.features.driver.model.mockDriverRatings

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AssignmentScreen(
    bookingId: String,
    onNavigateBack: () -> Unit = {},
    viewModel: OfficerViewModel = hiltViewModel()
) {
    val vehiclesState by viewModel.availableVehicles.collectAsState()
    val driversState by viewModel.availableDriversList.collectAsState()
    val assignState by viewModel.assignState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    var selectedVehicle by remember { mutableStateOf<Vehicle?>(null) }
    var selectedDriver by remember { mutableStateOf<DriverOption?>(null) }

    LaunchedEffect(key1=Unit) {
        viewModel.loadAvailableVehicles()
        viewModel.loadAvailableDrivers()
    }

    LaunchedEffect(key1=assignState) {
        if (assignState is UiState.Success) {
            snackbarHostState.showSnackbar(
                "Vehicle and driver assigned successfully!"
            )
            viewModel.resetAssignState()
            onNavigateBack()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Assign Vehicle & Driver",
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

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentPadding = PaddingValues(bottom = 100.dp)
        ) {
            // Select Vehicle section
            item {
                Text(
                    text = "Select Vehicle",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onBackground,
                    modifier = Modifier.padding(16.dp)
                )
            }

            when (val state = vehiclesState) {
                is UiState.Loading -> {
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(80.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator(
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                }
                is UiState.Success -> {
                    items(state.data) { vehicle ->
                        val isSelected = selectedVehicle?.id == vehicle.id
                        AssignVehicleCard(
                            vehicle = vehicle,
                            isSelected = isSelected,
                            onClick = { selectedVehicle = vehicle }
                        )
                    }
                }
                else -> Unit
            }

            // Select Driver section
            item {
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "Select Driver",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onBackground,
                    modifier = Modifier.padding(16.dp)
                )
            }

            when (val state = driversState) {
                is UiState.Loading -> {
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(80.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator(
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                }
                is UiState.Success -> {
                    items(state.data) { driver ->
                        val isSelected = selectedDriver?.id == driver.id
                        AssignDriverCard(
                            driver = driver,
                            isSelected = isSelected,
                            onClick = {
                                if (driver.isAvailable) {
                                    selectedDriver = driver
                                }
                            }
                        )
                    }
                }
                else -> Unit
            }

            // Confirm button
            item {
                Spacer(modifier = Modifier.height(24.dp))
                Button(
                    onClick = {
                        val vehicle = selectedVehicle
                        val driver = selectedDriver
                        if (vehicle != null && driver != null) {
                            viewModel.assignVehicleAndDriver(
                                bookingId = bookingId,
                                vehicleReg = vehicle.registrationNumber,
                                driverName = driver.name,
                                onSuccess = {}
                            )
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp)
                        .padding(horizontal = 16.dp),
                    enabled = selectedVehicle != null &&
                            selectedDriver != null &&
                            assignState !is UiState.Loading,
                    shape = MaterialTheme.shapes.medium,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary
                    )
                ) {
                    if (assignState is UiState.Loading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(20.dp),
                            color = MaterialTheme.colorScheme.onPrimary,
                            strokeWidth = 2.dp
                        )
                    } else {
                        Text(
                            text = "Confirm Assignment",
                            style = MaterialTheme.typography.labelLarge
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun AssignVehicleCard(
    vehicle: Vehicle,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp)
            .clip(MaterialTheme.shapes.large)
            .border(
                width = if (isSelected) 2.dp else 0.dp,
                color = if (isSelected)
                    MaterialTheme.colorScheme.primary
                else
                    MaterialTheme.colorScheme.surface,
                shape = MaterialTheme.shapes.large
            )
            .background(
                if (isSelected)
                    MaterialTheme.colorScheme.primaryContainer
                else
                    MaterialTheme.colorScheme.surface
            )
            .clickable { onClick() }
            .padding(16.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Filled.DirectionsBus,
                contentDescription = null,
                tint = if (isSelected)
                    MaterialTheme.colorScheme.primary
                else
                    MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(32.dp)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = vehicle.registrationNumber,
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = "${vehicle.make} ${vehicle.model}" +
                            " • ${vehicle.capacity} seats",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            if (isSelected) {
                Icon(
                    imageVector = Icons.Filled.CheckCircle,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(22.dp)
                )
            }
        }
    }
}

@Composable
private fun AssignDriverCard(
    driver: DriverOption,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    // Find this driver's rating summary
    val ratingSummary = mockDriverRatings.find {
        it.driverId == driver.id
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp)
            .clip(MaterialTheme.shapes.large)
            .border(
                width = if (isSelected) 2.dp else 0.dp,
                color = if (isSelected)
                    MaterialTheme.colorScheme.primary
                else
                    MaterialTheme.colorScheme.surface,
                shape = MaterialTheme.shapes.large
            )
            .background(
                when {
                    isSelected ->
                        MaterialTheme.colorScheme.primaryContainer
                    !driver.isAvailable ->
                        MaterialTheme.colorScheme.surface
                            .copy(alpha = 0.5f)
                    else -> MaterialTheme.colorScheme.surface
                }
            )
            .clickable(enabled = driver.isAvailable) { onClick() }
            .padding(16.dp)
    ) {
        Column {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Filled.Person,
                    contentDescription = null,
                    tint = if (!driver.isAvailable)
                        MaterialTheme.colorScheme.onSurfaceVariant
                            .copy(alpha = 0.5f)
                    else if (isSelected)
                        MaterialTheme.colorScheme.primary
                    else
                        MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(32.dp)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = driver.name,
                        style = MaterialTheme.typography.titleSmall,
                        color = if (!driver.isAvailable)
                            MaterialTheme.colorScheme.onSurface
                                .copy(alpha = 0.5f)
                        else
                            MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = driver.licenseNumber,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme
                            .onSurfaceVariant
                            .copy(
                                alpha = if (!driver.isAvailable)
                                    0.5f else 1f
                            )
                    )

                    // Show rating stars
                    if (ratingSummary != null &&
                        ratingSummary.totalRatings > 0) {
                        Spacer(modifier = Modifier.height(4.dp))
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            StarRatingDisplay(
                                rating = ratingSummary.averageRating,
                                starSize = 12.dp
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "${"%.1f".format(
                                    ratingSummary.averageRating
                                )} (${ratingSummary.totalRatings})",
                                style = MaterialTheme.typography
                                    .labelSmall,
                                color = MaterialTheme.colorScheme
                                    .onSurfaceVariant
                            )
                        }
                    } else {
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "No ratings yet",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme
                                .onSurfaceVariant.copy(alpha = 0.6f)
                        )
                    }

                    if (!driver.isAvailable &&
                        driver.currentAssignment != null) {
                        Text(
                            text = "Busy: ${driver.currentAssignment}",
                            style = MaterialTheme.typography.labelSmall,
                            color = StatusRejected
                        )
                    }
                }

                if (isSelected) {
                    Icon(
                        imageVector = Icons.Filled.CheckCircle,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(22.dp)
                    )
                } else {
                    Box(
                        modifier = Modifier
                            .clip(MaterialTheme.shapes.extraSmall)
                            .background(
                                if (driver.isAvailable)
                                    StatusApproved.copy(alpha = 0.12f)
                                else
                                    StatusRejected.copy(alpha = 0.12f)
                            )
                            .padding(
                                horizontal = 8.dp,
                                vertical = 3.dp
                            )
                    ) {
                        Text(
                            text = if (driver.isAvailable)
                                "Available"
                            else "Busy",
                            style = MaterialTheme.typography.labelSmall,
                            color = if (driver.isAvailable)
                                StatusApproved
                            else
                                StatusRejected
                        )
                    }
                }
            }
        }
    }
}