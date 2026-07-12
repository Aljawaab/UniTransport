package com.example.unitransport.features.officer.presentation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.background
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
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.unitransport.core.base.UiState
import com.example.unitransport.core.ui.components.StarRatingDisplay
import com.example.unitransport.core.ui.theme.StatusApproved
import com.example.unitransport.core.ui.theme.StatusPending
import com.example.unitransport.core.ui.theme.StatusRejected
import com.example.unitransport.features.driver.model.DriverRatingSummary
import com.example.unitransport.features.driver.model.Rating
import com.example.unitransport.features.driver.model.RatingType
import com.example.unitransport.features.driver.presentation.RatingViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DriverRatingsScreen(
    onNavigateBack: () -> Unit = {},
    viewModel: RatingViewModel = hiltViewModel()
) {
    val ratingsState by viewModel.driverRatingsState.collectAsState()

    LaunchedEffect(key1 = Unit) {
        viewModel.loadDriverRatings()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Driver Ratings",
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

        when (val state = ratingsState) {
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
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    item {
                        Text(
                            text = "Driver performance ratings " +
                                    "from completed trips",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                    }

                    items(state.data) { summary ->
                        DriverRatingSummaryCard(summary = summary)
                    }
                }
            }

            else -> Unit
        }
    }
}

@Composable
private fun DriverRatingSummaryCard(summary: DriverRatingSummary) {
    val ratingColor = when {
        summary.averageRating >= 4.5f -> StatusApproved
        summary.averageRating >= 3.0f -> StatusPending
        summary.averageRating > 0f -> StatusRejected
        else -> MaterialTheme.colorScheme.onSurfaceVariant
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.large,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {

            // Driver header
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Box(
                    modifier = Modifier
                        .size(52.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.primaryContainer),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = summary.driverName
                            .take(2).uppercase(),
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.primary
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = summary.driverName,
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    if (summary.totalRatings > 0) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            StarRatingDisplay(
                                rating = summary.averageRating,
                                starSize = 14.dp
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "${"%.1f".format(
                                    summary.averageRating
                                )} (${summary.totalRatings} " +
                                        if (summary.totalRatings == 1)
                                            "review)"
                                        else "reviews)",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme
                                    .onSurfaceVariant
                            )
                        }
                    } else {
                        Text(
                            text = "No ratings yet",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                // Big rating badge
                if (summary.totalRatings > 0) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "${"%.1f".format(summary.averageRating)}",
                            style = MaterialTheme.typography.titleLarge,
                            color = ratingColor
                        )
                        Icon(
                            imageVector = Icons.Filled.Star,
                            contentDescription = null,
                            tint = ratingColor,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }

            // Individual reviews
            if (summary.ratings.isNotEmpty()) {
                Spacer(modifier = Modifier.height(12.dp))
                Divider(
                    color = MaterialTheme.colorScheme.outlineVariant
                        .copy(alpha = 0.5f)
                )
                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = "Recent Reviews",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(8.dp))

                summary.ratings.forEach { rating ->
                    RatingReviewItem(rating = rating)
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }
        }
    }
}

@Composable
private fun RatingReviewItem(rating: Rating) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.medium,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.background
        ),
        elevation = CardDefaults.cardElevation(0.dp)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = rating.raterName,
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = rating.raterRole,
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                StarRatingDisplay(
                    rating = rating.stars.toFloat(),
                    starSize = 14.dp
                )
            }
            if (rating.comment.isNotBlank()) {
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "\"${rating.comment}\"",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 3,
                    overflow = TextOverflow.Ellipsis
                )
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = rating.timestamp,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
                    .copy(alpha = 0.6f)
            )
        }
    }
}