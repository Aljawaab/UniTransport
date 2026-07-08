package com.example.unitransport.features.officer.presentation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.unitransport.core.base.UiState
import com.example.unitransport.features.bookings.model.BookingRequestStatus
import com.example.unitransport.features.officer.model.BookingRequest

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PendingRequestsScreen(
    onNavigateBack: () -> Unit = {},
    onNavigateToDetail: (String) -> Unit = {},
    viewModel: OfficerViewModel = hiltViewModel()
) {
    val requestsState by viewModel.requestsState.collectAsState()
    val approveState by viewModel.approveState.collectAsState()
    val rejectState by viewModel.rejectState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    var selectedTab by remember { mutableIntStateOf(0) }
    val tabs = listOf("Pending", "Approved", "Rejected", "All")

    LaunchedEffect(key1=Unit) { viewModel.loadAllRequests() }

    LaunchedEffect(key1=approveState) {
        if (approveState is UiState.Success) {
            snackbarHostState.showSnackbar("Booking approved successfully!")
            viewModel.resetApproveState()
            viewModel.loadAllRequests()
        }
    }

    LaunchedEffect(key1=rejectState) {
        if (rejectState is UiState.Success) {
            snackbarHostState.showSnackbar("Booking rejected.")
            viewModel.resetRejectState()
            viewModel.loadAllRequests()
        }
    }

    // Rejection dialog
    if (viewModel.showRejectionDialog) {
        AlertDialog(
            onDismissRequest = { viewModel.dismissRejectDialog() },
            title = {
                Text(
                    text = "Reject Booking",
                    style = MaterialTheme.typography.titleMedium
                )
            },
            text = {
                Column {
                    Text(
                        text = "Please provide a reason for rejection:",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    OutlinedTextField(
                        value = viewModel.rejectionReason,
                        onValueChange = viewModel::onRejectionReasonChange,
                        placeholder = {
                            Text("Enter rejection reason...")
                        },
                        isError = viewModel.rejectionReasonError != null,
                        supportingText = {
                            viewModel.rejectionReasonError?.let {
                                Text(
                                    text = it,
                                    color = MaterialTheme.colorScheme.error
                                )
                            }
                        },
                        minLines = 3,
                        shape = MaterialTheme.shapes.medium
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = { viewModel.confirmReject {} },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.error
                    )
                ) {
                    Text("Reject")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { viewModel.dismissRejectDialog() }
                ) {
                    Text("Cancel")
                }
            },
            shape = MaterialTheme.shapes.large
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Booking Requests",
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

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            ScrollableTabRow(
                selectedTabIndex = selectedTab,
                containerColor = MaterialTheme.colorScheme.surface,
                contentColor = MaterialTheme.colorScheme.primary,
                edgePadding = 16.dp
            ) {
                tabs.forEachIndexed { index, tab ->
                    Tab(
                        selected = selectedTab == index,
                        onClick = { selectedTab = index },
                        text = {
                            Text(
                                text = tab,
                                style = MaterialTheme.typography.labelLarge
                            )
                        }
                    )
                }
            }

            when (val state = requestsState) {
                is UiState.Loading -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }

                is UiState.Success -> {
                    val filtered = when (selectedTab) {
                        0 -> state.data.filter {
                            it.booking.status ==
                                    BookingRequestStatus.PENDING
                        }
                        1 -> state.data.filter {
                            it.booking.status ==
                                    BookingRequestStatus.APPROVED
                        }
                        2 -> state.data.filter {
                            it.booking.status ==
                                    BookingRequestStatus.REJECTED
                        }
                        else -> state.data
                    }

                    if (filtered.isEmpty()) {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    text = "📋",
                                    style = MaterialTheme.typography
                                        .displayLarge
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = "No requests here",
                                    style = MaterialTheme.typography.titleMedium,
                                    color = MaterialTheme.colorScheme
                                        .onSurfaceVariant,
                                    textAlign = TextAlign.Center
                                )
                            }
                        }
                    } else {
                        LazyColumn(
                            contentPadding = PaddingValues(16.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            items(filtered) { request ->
                                RequestCard(
                                    request = request,
                                    onViewDetail = {
                                        onNavigateToDetail(
                                            request.booking.id
                                        )
                                    },
                                    onApprove = {
                                        viewModel.approveRequest(
                                            request.booking.id
                                        ) {}
                                    },
                                    onReject = {
                                        viewModel.showRejectDialog(
                                            request.booking.id
                                        )
                                    },
                                    isApproving = approveState is
                                            UiState.Loading,
                                    isRejecting = rejectState is
                                            UiState.Loading
                                )
                            }
                        }
                    }
                }

                else -> Unit
            }
        }
    }
}

@Composable
private fun RequestCard(
    request: BookingRequest,
    onViewDetail: () -> Unit,
    onApprove: () -> Unit,
    onReject: () -> Unit,
    isApproving: Boolean,
    isRejecting: Boolean
) {
    androidx.compose.material3.Card(
        modifier = Modifier.fillMaxSize(),
        shape = MaterialTheme.shapes.large,
        colors = androidx.compose.material3.CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = androidx.compose.material3.CardDefaults
            .cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Header
            androidx.compose.foundation.layout.Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = request.booking.destination,
                        style = MaterialTheme.typography.titleSmall,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = request.requesterName +
                                " • " + request.requesterDepartment,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                com.example.unitransport.core.ui.components
                    .BookingStatusChip(
                        status = request.booking.status
                    )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "${request.booking.departureDate} • " +
                        "${request.booking.departureTime} • " +
                        "${request.booking.passengerCount} passengers",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Text(
                text = "Preference: ${request.booking.vehiclePreference}",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.primary
            )

            if (request.booking.status == BookingRequestStatus.REJECTED &&
                request.rejectionReason.isNotBlank()) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Reason: ${request.rejectionReason}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.error
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Action buttons
            if (request.booking.status == BookingRequestStatus.PENDING) {
                androidx.compose.foundation.layout.Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    androidx.compose.material3.OutlinedButton(
                        onClick = onReject,
                        modifier = Modifier.weight(1f),
                        enabled = !isRejecting && !isApproving,
                        shape = MaterialTheme.shapes.medium,
                        colors = androidx.compose.material3.ButtonDefaults
                            .outlinedButtonColors(
                                contentColor =
                                    MaterialTheme.colorScheme.error
                            ),
                        border = androidx.compose.foundation.BorderStroke(
                            1.dp,
                            MaterialTheme.colorScheme.error.copy(alpha = 0.5f)
                        )
                    ) {
                        Text(
                            text = "Reject",
                            style = MaterialTheme.typography.labelLarge
                        )
                    }
                    Button(
                        onClick = onApprove,
                        modifier = Modifier.weight(1f),
                        enabled = !isApproving && !isRejecting,
                        shape = MaterialTheme.shapes.medium,
                        colors = ButtonDefaults.buttonColors(
                            containerColor =
                                com.example.unitransport.core.ui.theme
                                    .StatusApproved
                        )
                    ) {
                        if (isApproving) {
                            CircularProgressIndicator(
                                modifier = Modifier.height(16.dp),
                                color = MaterialTheme.colorScheme.onPrimary,
                                strokeWidth = 2.dp
                            )
                        } else {
                            Text(
                                text = "Approve",
                                style = MaterialTheme.typography.labelLarge,
                                color = MaterialTheme.colorScheme.onPrimary
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
            }

            TextButton(
                onClick = onViewDetail,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "View Full Details →",
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}