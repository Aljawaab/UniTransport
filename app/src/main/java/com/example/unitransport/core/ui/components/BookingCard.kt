package com.example.unitransport.core.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.DirectionsBus
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.unitransport.core.ui.theme.StatusApproved
import com.example.unitransport.core.ui.theme.StatusMaintenance
import com.example.unitransport.core.ui.theme.StatusPending
import com.example.unitransport.core.ui.theme.StatusRejected
import com.example.unitransport.features.bookings.model.Booking
import com.example.unitransport.features.bookings.model.BookingRequestStatus

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BookingCard(
    booking: Booking,
    onClick: () -> Unit,
    onRateDriver: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    Card(
        onClick = onClick,
        modifier = modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.large,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {

            // Header row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = booking.id,
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                BookingStatusChip(status = booking.status)
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Destination
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Filled.LocationOn,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = booking.destination,
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            Spacer(modifier = Modifier.height(4.dp))

            // Purpose
            Text(
                text = booking.purpose,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.padding(start = 24.dp)
            )

            Spacer(modifier = Modifier.height(12.dp))
            Divider(
                color = MaterialTheme.colorScheme.outlineVariant
                    .copy(alpha = 0.5f)
            )
            Spacer(modifier = Modifier.height(12.dp))

            // Details row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                BookingInfoItem(
                    icon = Icons.Filled.CalendarMonth,
                    text = booking.departureDate
                )
                BookingInfoItem(
                    icon = Icons.Filled.Schedule,
                    text = booking.departureTime
                )
                BookingInfoItem(
                    icon = Icons.Filled.People,
                    text = "${booking.passengerCount} pax"
                )
                BookingInfoItem(
                    icon = Icons.Filled.DirectionsBus,
                    text = booking.vehiclePreference
                )
            }

            // Vehicle/driver assignment if approved
            if (booking.vehicleAssigned != null) {
                Spacer(modifier = Modifier.height(12.dp))
                Divider(
                    color = MaterialTheme.colorScheme.outlineVariant
                        .copy(alpha = 0.5f)
                )
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Vehicle: ${booking.vehicleAssigned}",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.primary
                    )
                    if (booking.driverAssigned != null) {
                        Text(
                            text = "Driver: ${booking.driverAssigned}",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.secondary
                        )
                    }
                }
            }

            // Rate Driver button — ONLY for completed bookings
            if (booking.status == BookingRequestStatus.COMPLETED
                && onRateDriver != null) {
                Spacer(modifier = Modifier.height(12.dp))
                Divider(
                    color = MaterialTheme.colorScheme.outlineVariant
                        .copy(alpha = 0.5f)
                )
                Spacer(modifier = Modifier.height(12.dp))
                Button(
                    onClick = onRateDriver,
                    modifier = Modifier.fillMaxWidth(),
                    shape = MaterialTheme.shapes.medium,
                    colors = ButtonDefaults.buttonColors(
                        containerColor =
                            StatusPending.copy(alpha = 0.15f),
                        contentColor = StatusPending
                    )
                ) {
                    Icon(
                        imageVector = Icons.Filled.Star,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Rate Your Driver",
                        style = MaterialTheme.typography.labelLarge
                    )
                }
            }
        }
    }
}

@Composable
private fun BookingInfoItem(
    icon: ImageVector,
    text: String
) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.size(14.dp)
        )
        Spacer(modifier = Modifier.width(4.dp))
        Text(
            text = text,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@Composable
fun BookingStatusChip(status: BookingRequestStatus) {
    val (color, label) = when (status) {
        BookingRequestStatus.PENDING -> StatusPending to "Pending"
        BookingRequestStatus.APPROVED -> StatusApproved to "Approved"
        BookingRequestStatus.REJECTED -> StatusRejected to "Rejected"
        BookingRequestStatus.ACTIVE -> StatusMaintenance to "Active"
        BookingRequestStatus.COMPLETED ->
            StatusApproved to "Completed"
        BookingRequestStatus.CANCELLED -> StatusRejected to "Cancelled"
    }
    Box(
        modifier = Modifier
            .clip(MaterialTheme.shapes.extraSmall)
            .background(color.copy(alpha = 0.12f))
            .padding(horizontal = 8.dp, vertical = 3.dp)
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = color
        )
    }
}