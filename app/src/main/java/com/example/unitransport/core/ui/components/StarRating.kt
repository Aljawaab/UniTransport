package com.example.unitransport.core.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.StarOutline
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.unitransport.core.ui.theme.StatusPending

// Interactive star rating (for submitting)
@Composable
fun StarRatingInput(
    selectedStars: Int,
    onStarSelected: (Int) -> Unit,
    modifier: Modifier = Modifier,
    starSize: Dp = 40.dp
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        (1..5).forEach { star ->
            Icon(
                imageVector = if (star <= selectedStars)
                    Icons.Filled.Star
                else
                    Icons.Outlined.StarOutline,
                contentDescription = "$star stars",
                tint = if (star <= selectedStars)
                    StatusPending
                else
                    MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f),
                modifier = Modifier
                    .size(starSize)
                    .clickable { onStarSelected(star) }
            )
        }
    }
}

// Display-only star rating (for showing ratings)
@Composable
fun StarRatingDisplay(
    rating: Float,
    modifier: Modifier = Modifier,
    starSize: Dp = 16.dp,
    tint: Color = StatusPending
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(2.dp)
    ) {
        (1..5).forEach { star ->
            Icon(
                imageVector = if (star <= rating)
                    Icons.Filled.Star
                else
                    Icons.Outlined.StarOutline,
                contentDescription = null,
                tint = if (star <= rating) tint
                else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f),
                modifier = Modifier.size(starSize)
            )
        }
    }
}