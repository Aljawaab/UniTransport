package com.example.unitransport.features.driver.presentation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.material.icons.filled.DirectionsBus
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.unitransport.core.base.UiState
import com.example.unitransport.core.ui.components.StarRatingInput
import com.example.unitransport.features.driver.model.RatingType

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RatingScreen(
    tripId: String,
    bookingId: String,
    targetName: String,
    targetId: String,
    raterName: String,
    raterRole: String,
    ratingType: RatingType,
    onNavigateBack: () -> Unit = {},
    viewModel: RatingViewModel = hiltViewModel()
) {
    val submitState by viewModel.submitState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val isLoading = submitState is UiState.Loading

    // Title and description based on who is rating whom
    val screenTitle = when (ratingType) {
        RatingType.BOOKER_RATES_DRIVER -> "Rate Your Driver"
        RatingType.DRIVER_RATES_PASSENGERS -> "Rate Passengers"
    }

    val targetLabel = when (ratingType) {
        RatingType.BOOKER_RATES_DRIVER -> "Driver"
        RatingType.DRIVER_RATES_PASSENGERS -> "Passengers"
    }

    val targetIcon = when (ratingType) {
        RatingType.BOOKER_RATES_DRIVER -> Icons.Filled.DirectionsBus
        RatingType.DRIVER_RATES_PASSENGERS -> Icons.Filled.Person
    }

    LaunchedEffect(key1 = submitState) {
        if (submitState is UiState.Success) {
            snackbarHostState.showSnackbar("Rating submitted! Thank you.")
            viewModel.resetSubmitState()
            onNavigateBack()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = screenTitle,
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
                .verticalScroll(rememberScrollState())
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            // Target info card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = MaterialTheme.shapes.large,
                colors = CardDefaults.cardColors(
                    containerColor =
                        MaterialTheme.colorScheme.primaryContainer
                ),
                elevation = CardDefaults.cardElevation(0.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        imageVector = targetIcon,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(56.dp)
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = targetName,
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                        textAlign = TextAlign.Center
                    )
                    Text(
                        text = targetLabel,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                            .copy(alpha = 0.7f)
                    )
                    Text(
                        text = "Trip: $bookingId",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Star rating
            Text(
                text = "How was your experience?",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onBackground,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Star rating label
            Text(
                text = when (viewModel.selectedStars) {
                    1 -> "⭐ Poor"
                    2 -> "⭐⭐ Fair"
                    3 -> "⭐⭐⭐ Good"
                    4 -> "⭐⭐⭐⭐ Very Good"
                    5 -> "⭐⭐⭐⭐⭐ Excellent!"
                    else -> "Tap a star to rate"
                },
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(16.dp))

            StarRatingInput(
                selectedStars = viewModel.selectedStars,
                onStarSelected = viewModel::onStarSelected
            )

            if (viewModel.commentError != null &&
                viewModel.selectedStars == 0) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = viewModel.commentError!!,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.error
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Comment field
            OutlinedTextField(
                value = viewModel.comment,
                onValueChange = viewModel::onCommentChange,
                label = { Text("Leave a comment (optional)") },
                placeholder = {
                    Text("Share your experience...")
                },
                minLines = 3,
                maxLines = 5,
                enabled = !isLoading,
                modifier = Modifier.fillMaxWidth(),
                shape = MaterialTheme.shapes.medium
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Submit button
            Button(
                onClick = {
                    viewModel.submitRating(
                        tripId = tripId,
                        bookingId = bookingId,
                        raterName = raterName,
                        raterRole = raterRole,
                        targetName = targetName,
                        targetId = targetId,
                        type = ratingType,
                        onSuccess = {}
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                enabled = !isLoading,
                shape = MaterialTheme.shapes.medium,
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary
                )
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        color = MaterialTheme.colorScheme.onPrimary,
                        strokeWidth = 2.dp
                    )
                } else {
                    Text(
                        text = "Submit Rating",
                        style = MaterialTheme.typography.labelLarge
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}