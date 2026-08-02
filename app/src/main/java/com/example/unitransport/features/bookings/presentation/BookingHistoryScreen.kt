package com.example.unitransport.features.bookings.presentation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.unitransport.core.base.UiState
import com.example.unitransport.core.ui.components.BookingCard
import com.example.unitransport.features.bookings.model.BookingRequestStatus

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BookingHistoryScreen(
    onNavigateToDetail: (String) -> Unit = {},
    onNavigateToRateDriver: (
        bookingId: String,
        driverName: String,
        driverId: String
    ) -> Unit = { _, _, _ -> },
    viewModel: BookingHistoryViewModel = hiltViewModel()
) {
    val bookingsState by viewModel.bookingsState.collectAsState()
    val ratedBookingIds by viewModel.ratedBookingIds.collectAsState()

    LaunchedEffect(key1=Unit) {
        viewModel.loadBookings()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "My Bookings",
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.onPrimary
                    )
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
            // Tab row
            ScrollableTabRow(
                selectedTabIndex = viewModel.selectedTabIndex,
                containerColor = MaterialTheme.colorScheme.surface,
                contentColor = MaterialTheme.colorScheme.primary,
                edgePadding = 16.dp
            ) {
                viewModel.tabs.forEachIndexed { index, tab ->
                    Tab(
                        selected = viewModel.selectedTabIndex == index,
                        onClick = { viewModel.onTabSelected(index) },
                        text = {
                            Text(
                                text = tab,
                                style = MaterialTheme.typography.labelLarge
                            )
                        }
                    )
                }
            }

            // Content
            when (val state = bookingsState) {
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
                    val filtered = viewModel.getFilteredBookings(state.data)

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
                                    style = MaterialTheme.typography.displayLarge
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = "No bookings found",
                                    style = MaterialTheme.typography.titleMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Text(
                                    text = "Bookings in this category\nwill appear here",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    textAlign = TextAlign.Center
                                )
                            }
                        }
                    } else {
                        LazyColumn(
                            contentPadding = PaddingValues(16.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp),
                            modifier = Modifier.fillMaxSize()
                        ) {
                            items(filtered) { booking ->
                                val isRated = booking.id in ratedBookingIds
                                BookingCard(
                                    booking = booking,
                                    onClick = { onNavigateToDetail(booking.id) },
                                    onRateDriver = if (
                                        booking.status == BookingRequestStatus.COMPLETED && !isRated
                                    ) {
                                        {
                                            onNavigateToRateDriver(
                                                booking.id,
                                                booking.driverAssigned ?: "Driver",
                                                booking.driverId ?: ""
                                            )
                                        }
                                    } else null,
                                    isDriverRated = isRated
                                )
                            }
                        }
                    }
                }

                is UiState.Error -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Failed to load bookings.\nPlease try again.",
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
}