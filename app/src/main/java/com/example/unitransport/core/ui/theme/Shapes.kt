package com.example.unitransport.core.ui.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Shapes
import androidx.compose.ui.unit.dp

val AppShapes = Shapes(
    extraSmall = RoundedCornerShape(4.dp),   // chips, badges
    small = RoundedCornerShape(8.dp),         // input fields
    medium = RoundedCornerShape(12.dp),       // standard cards
    large = RoundedCornerShape(16.dp),        // large cards
    extraLarge = RoundedCornerShape(28.dp)    // dialogs, modals
)