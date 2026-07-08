package com.example.unitransport.features.bookings.presentation

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.unitransport.core.base.UiState
import com.example.unitransport.core.ui.components.StepperIndicator
import com.example.unitransport.features.bookings.model.Booking

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateBookingScreen(
    onNavigateBack: () -> Unit = {},
    onBookingSuccess: (Booking) -> Unit = {},
    viewModel: CreateBookingViewModel = hiltViewModel()
) {
    val submitState by viewModel.submitState.collectAsState()
    val steps = listOf("Details", "Schedule", "Vehicle", "Review")

    LaunchedEffect(submitState) {
        if (submitState is UiState.Success) {
            onBookingSuccess((submitState as UiState.Success<Booking>).data)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "New Booking",
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

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Stepper
            StepperIndicator(
                steps = steps,
                currentStep = viewModel.currentStep,
                modifier = Modifier.padding(
                    horizontal = 16.dp,
                    vertical = 20.dp
                )
            )

            // Animated step content
            AnimatedContent(
                targetState = viewModel.currentStep,
                transitionSpec = {
                    if (targetState > initialState) {
                        slideInHorizontally { it } + fadeIn() togetherWith
                                slideOutHorizontally { -it } + fadeOut()
                    } else {
                        slideInHorizontally { -it } + fadeIn() togetherWith
                                slideOutHorizontally { it } + fadeOut()
                    }
                },
                modifier = Modifier.weight(1f),
                label = "step_animation"
            ) { step ->
                when (step) {
                    0 -> Step1TripDetails(viewModel = viewModel)
                    1 -> Step2Schedule(viewModel = viewModel)
                    2 -> Step3VehiclePreference(viewModel = viewModel)
                    3 -> Step4Review(
                        viewModel = viewModel,
                        isLoading = submitState is UiState.Loading
                    )
                }
            }

            // Navigation buttons
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                if (viewModel.currentStep > 0) {
                    OutlinedButton(
                        onClick = { viewModel.previousStep() },
                        modifier = Modifier
                            .weight(1f)
                            .height(52.dp),
                        shape = MaterialTheme.shapes.medium
                    ) {
                        Text(
                            text = "Back",
                            style = MaterialTheme.typography.labelLarge
                        )
                    }
                }

                Button(
                    onClick = {
                        if (viewModel.currentStep < 3) {
                            viewModel.nextStep()
                        } else {
                            viewModel.submitBooking(onBookingSuccess)
                        }
                    },
                    modifier = Modifier
                        .weight(1f)
                        .height(52.dp),
                    enabled = submitState !is UiState.Loading,
                    shape = MaterialTheme.shapes.medium,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary
                    )
                ) {
                    if (submitState is UiState.Loading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(20.dp),
                            color = MaterialTheme.colorScheme.onPrimary,
                            strokeWidth = 2.dp
                        )
                    } else {
                        Text(
                            text = if (viewModel.currentStep < 3)
                                "Next" else "Submit Booking",
                            style = MaterialTheme.typography.labelLarge
                        )
                    }
                }
            }
        }
    }
}

// Step 1 — Trip Details
@Composable
private fun Step1TripDetails(viewModel: CreateBookingViewModel) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 16.dp)
    ) {
        Text(
            text = "Trip Details",
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.onBackground
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = "Tell us about your trip",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.height(24.dp))

        OutlinedTextField(
            value = viewModel.destination,
            onValueChange = viewModel::onDestinationChange,
            label = { Text("Destination") },
            placeholder = { Text("e.g. Engineering Block, Town Campus") },
            isError = viewModel.destinationError != null,
            supportingText = {
                viewModel.destinationError?.let {
                    Text(text = it, color = MaterialTheme.colorScheme.error)
                }
            },
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
            shape = MaterialTheme.shapes.medium
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = viewModel.purpose,
            onValueChange = viewModel::onPurposeChange,
            label = { Text("Purpose of Trip") },
            placeholder = { Text("e.g. Field Trip, Research Visit, Games") },
            isError = viewModel.purposeError != null,
            supportingText = {
                viewModel.purposeError?.let {
                    Text(text = it, color = MaterialTheme.colorScheme.error)
                }
            },
            minLines = 2,
            maxLines = 3,
            modifier = Modifier.fillMaxWidth(),
            shape = MaterialTheme.shapes.medium
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Passenger counter
        Text(
            text = "Number of Passengers",
            style = MaterialTheme.typography.titleSmall,
            color = MaterialTheme.colorScheme.onBackground
        )
        Spacer(modifier = Modifier.height(8.dp))

        Card(
            shape = MaterialTheme.shapes.large,
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = {
                        viewModel.onPassengerCountChange(
                            viewModel.passengerCount - 1
                        )
                    },
                    enabled = viewModel.passengerCount > 1
                ) {
                    Icon(
                        imageVector = Icons.Filled.Remove,
                        contentDescription = "Decrease",
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
                Text(
                    text = "${viewModel.passengerCount}",
                    style = MaterialTheme.typography.headlineMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                    textAlign = TextAlign.Center
                )
                IconButton(
                    onClick = {
                        viewModel.onPassengerCountChange(
                            viewModel.passengerCount + 1
                        )
                    },
                    enabled = viewModel.passengerCount < 100
                ) {
                    Icon(
                        imageVector = Icons.Filled.Add,
                        contentDescription = "Increase",
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = viewModel.additionalNotes,
            onValueChange = viewModel::onAdditionalNotesChange,
            label = { Text("Additional Notes (Optional)") },
            placeholder = { Text("Any special requirements?") },
            minLines = 2,
            maxLines = 4,
            modifier = Modifier.fillMaxWidth(),
            shape = MaterialTheme.shapes.medium
        )

        Spacer(modifier = Modifier.height(16.dp))
    }
}

// Step 2 — Schedule
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun Step2Schedule(viewModel: CreateBookingViewModel) {
    var departureDateExpanded by remember { mutableStateOf(false) }
    var departureTimeExpanded by remember { mutableStateOf(false) }
    var returnDateExpanded by remember { mutableStateOf(false) }
    var returnTimeExpanded by remember { mutableStateOf(false) }

    val dates = listOf(
        "2026-06-27", "2026-06-28", "2026-06-29",
        "2026-06-30", "2026-07-01", "2026-07-02",
        "2026-07-03", "2026-07-04", "2026-07-05"
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 16.dp)
    ) {
        Text(
            text = "Schedule",
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.onBackground
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = "When do you need the vehicle?",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "Departure",
            style = MaterialTheme.typography.titleSmall,
            color = MaterialTheme.colorScheme.primary
        )
        Spacer(modifier = Modifier.height(8.dp))

        // Departure date
        ExposedDropdownMenuBox(
            expanded = departureDateExpanded,
            onExpandedChange = { departureDateExpanded = it }
        ) {
            OutlinedTextField(
                value = viewModel.departureDate,
                onValueChange = {},
                readOnly = true,
                label = { Text("Departure Date") },
                trailingIcon = {
                    ExposedDropdownMenuDefaults.TrailingIcon(
                        expanded = departureDateExpanded
                    )
                },
                isError = viewModel.departureDateError != null,
                supportingText = {
                    viewModel.departureDateError?.let {
                        Text(text = it, color = MaterialTheme.colorScheme.error)
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .menuAnchor(),
                shape = MaterialTheme.shapes.medium
            )
            ExposedDropdownMenu(
                expanded = departureDateExpanded,
                onDismissRequest = { departureDateExpanded = false }
            ) {
                dates.forEach { date ->
                    DropdownMenuItem(
                        text = { Text(date) },
                        onClick = {
                            viewModel.onDepartureDateChange(date)
                            departureDateExpanded = false
                        }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Departure time
        ExposedDropdownMenuBox(
            expanded = departureTimeExpanded,
            onExpandedChange = { departureTimeExpanded = it }
        ) {
            OutlinedTextField(
                value = viewModel.departureTime,
                onValueChange = {},
                readOnly = true,
                label = { Text("Departure Time") },
                trailingIcon = {
                    ExposedDropdownMenuDefaults.TrailingIcon(
                        expanded = departureTimeExpanded
                    )
                },
                isError = viewModel.departureTimeError != null,
                supportingText = {
                    viewModel.departureTimeError?.let {
                        Text(text = it, color = MaterialTheme.colorScheme.error)
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .menuAnchor(),
                shape = MaterialTheme.shapes.medium
            )
            ExposedDropdownMenu(
                expanded = departureTimeExpanded,
                onDismissRequest = { departureTimeExpanded = false }
            ) {
                viewModel.timeSlots.forEach { time ->
                    DropdownMenuItem(
                        text = { Text(time) },
                        onClick = {
                            viewModel.onDepartureTimeChange(time)
                            departureTimeExpanded = false
                        }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        Text(
            text = "Return (Optional)",
            style = MaterialTheme.typography.titleSmall,
            color = MaterialTheme.colorScheme.primary
        )
        Spacer(modifier = Modifier.height(8.dp))

        // Return date
        ExposedDropdownMenuBox(
            expanded = returnDateExpanded,
            onExpandedChange = { returnDateExpanded = it }
        ) {
            OutlinedTextField(
                value = viewModel.returnDate,
                onValueChange = {},
                readOnly = true,
                label = { Text("Return Date (Optional)") },
                trailingIcon = {
                    ExposedDropdownMenuDefaults.TrailingIcon(
                        expanded = returnDateExpanded
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .menuAnchor(),
                shape = MaterialTheme.shapes.medium
            )
            ExposedDropdownMenu(
                expanded = returnDateExpanded,
                onDismissRequest = { returnDateExpanded = false }
            ) {
                dates.forEach { date ->
                    DropdownMenuItem(
                        text = { Text(date) },
                        onClick = {
                            viewModel.onReturnDateChange(date)
                            returnDateExpanded = false
                        }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Return time
        ExposedDropdownMenuBox(
            expanded = returnTimeExpanded,
            onExpandedChange = { returnTimeExpanded = it }
        ) {
            OutlinedTextField(
                value = viewModel.returnTime,
                onValueChange = {},
                readOnly = true,
                label = { Text("Return Time (Optional)") },
                trailingIcon = {
                    ExposedDropdownMenuDefaults.TrailingIcon(
                        expanded = returnTimeExpanded
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .menuAnchor(),
                shape = MaterialTheme.shapes.medium
            )
            ExposedDropdownMenu(
                expanded = returnTimeExpanded,
                onDismissRequest = { returnTimeExpanded = false }
            ) {
                viewModel.timeSlots.forEach { time ->
                    DropdownMenuItem(
                        text = { Text(time) },
                        onClick = {
                            viewModel.onReturnTimeChange(time)
                            returnTimeExpanded = false
                        }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
    }
}

// Step 3 — Vehicle Preference
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun Step3VehiclePreference(viewModel: CreateBookingViewModel) {
    var expanded by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 16.dp)
    ) {
        Text(
            text = "Vehicle Preference",
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.onBackground
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = "Select a preferred vehicle type (optional)",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.height(24.dp))

        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = { expanded = it }
        ) {
            OutlinedTextField(
                value = viewModel.vehiclePreference,
                onValueChange = {},
                readOnly = true,
                label = { Text("Vehicle Type Preference") },
                trailingIcon = {
                    ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .menuAnchor(),
                shape = MaterialTheme.shapes.medium
            )
            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                viewModel.vehiclePreferences.forEach { pref ->
                    DropdownMenuItem(
                        text = { Text(pref) },
                        onClick = {
                            viewModel.onVehiclePreferenceChange(pref)
                            expanded = false
                        }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Card(
            shape = MaterialTheme.shapes.large,
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "ℹ️ Note",
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Vehicle preference is a request only. " +
                            "The Transport Officer will assign the most " +
                            "suitable available vehicle based on your " +
                            "requirements and availability.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
    }
}

// Step 4 — Review
@Composable
private fun Step4Review(
    viewModel: CreateBookingViewModel,
    isLoading: Boolean
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 16.dp)
    ) {
        Text(
            text = "Review Booking",
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.onBackground
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = "Please confirm your booking details",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.height(24.dp))

        Card(
            shape = MaterialTheme.shapes.large,
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                ReviewRow(label = "Destination", value = viewModel.destination)
                ReviewRow(label = "Purpose", value = viewModel.purpose)
                ReviewRow(
                    label = "Passengers",
                    value = "${viewModel.passengerCount}"
                )
                ReviewRow(
                    label = "Departure Date",
                    value = viewModel.departureDate
                )
                ReviewRow(
                    label = "Departure Time",
                    value = viewModel.departureTime
                )
                if (viewModel.returnDate.isNotBlank()) {
                    ReviewRow(
                        label = "Return Date",
                        value = viewModel.returnDate
                    )
                }
                if (viewModel.returnTime.isNotBlank()) {
                    ReviewRow(
                        label = "Return Time",
                        value = viewModel.returnTime
                    )
                }
                ReviewRow(
                    label = "Vehicle Preference",
                    value = viewModel.vehiclePreference,
                    isLast = viewModel.additionalNotes.isBlank()
                )
                if (viewModel.additionalNotes.isNotBlank()) {
                    ReviewRow(
                        label = "Notes",
                        value = viewModel.additionalNotes,
                        isLast = true
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Card(
            shape = MaterialTheme.shapes.large,
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.secondaryContainer
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
        ) {
            Row(
                modifier = Modifier.padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Filled.CheckCircle,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.secondary,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.size(12.dp))
                Text(
                    text = "Your booking will be reviewed by the " +
                            "Transport Officer within 24 hours.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSecondaryContainer
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
    }
}

@Composable
private fun ReviewRow(
    label: String,
    value: String,
    isLast: Boolean = false
) {
    Column {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.weight(0.4f)
            )
            Text(
                text = value,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.weight(0.6f),
                textAlign = TextAlign.End
            )
        }
        if (!isLast) {
            androidx.compose.material3.Divider(
                color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)
            )
        }
    }
}