package com.example.unitransport.features.bookings.presentation

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.EaseOutBack
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.material3.OutlinedButton
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.unitransport.core.ui.theme.StatusApproved
import com.example.unitransport.features.bookings.model.Booking

@Composable
fun BookingConfirmationScreen(
    booking: Booking,
    onNavigateToDashboard: () -> Unit = {},
    onNavigateToBookingHistory: () -> Unit = {},
    onNavigateToRateDriver: () -> Unit = {}
) {
    val scale = remember { Animatable(0f) }
    val alpha = remember { Animatable(0f) }

    LaunchedEffect(Unit) {
        scale.animateTo(
            targetValue = 1f,
            animationSpec = tween(600, easing = EaseOutBack)
        )
        alpha.animateTo(
            targetValue = 1f,
            animationSpec = tween(400)
        )
    }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Success icon
            Icon(
                imageVector = Icons.Filled.CheckCircle,
                contentDescription = null,
                tint = StatusApproved,
                modifier = Modifier
                    .size(96.dp)
                    .scale(scale.value)
            )

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "Booking Submitted!",
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.onBackground,
                textAlign = TextAlign.Center,
                modifier = Modifier.alpha(alpha.value)
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Your booking request has been submitted\n" +
                        "and is pending approval.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center,
                modifier = Modifier.alpha(alpha.value)
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Booking reference card
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .alpha(alpha.value),
                shape = MaterialTheme.shapes.large,
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Reference Number",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = booking.id,
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = booking.destination,
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurface,
                        textAlign = TextAlign.Center
                    )
                    Text(
                        text = "${booking.departureDate} • ${booking.departureTime}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            Button(
                onClick = onNavigateToDashboard,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .alpha(alpha.value),
                shape = MaterialTheme.shapes.medium,
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary
                )
            ) {
                Text(
                    text = "Back to Dashboard",
                    style = MaterialTheme.typography.labelLarge
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedButton(
                onClick = onNavigateToBookingHistory,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .alpha(alpha.value),
                shape = MaterialTheme.shapes.medium
            ) {
                Text(
                    text = "View My Bookings",
                    style = MaterialTheme.typography.labelLarge
                )
            }

            // Add after the existing buttons
            Spacer(modifier = Modifier.height(12.dp))

            OutlinedButton(
                onClick = onNavigateToRateDriver,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .alpha(alpha.value),
                shape = MaterialTheme.shapes.medium
            ) {
                Icon(
                    imageVector = Icons.Filled.Star,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Rate Your Driver",
                    style = MaterialTheme.typography.labelLarge
                )
            }
        }
    }
}