package com.example.unitransport.features.auth.presentation

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.EaseOutBack
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DirectionsBus
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.unitransport.core.ui.theme.PrimaryBlue
import com.example.unitransport.core.ui.theme.PrimaryBlueDark
import com.example.unitransport.features.auth.model.UserRole
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.delay
import kotlinx.coroutines.tasks.await

@Composable
fun SplashScreen(
    onNavigateToLogin: () -> Unit = {},
    onNavigateToDashboard: (UserRole) -> Unit = {}
) {
    val logoScale = remember { Animatable(0f) }
    val logoAlpha = remember { Animatable(0f) }
    val titleAlpha = remember { Animatable(0f) }
    val taglineAlpha = remember { Animatable(0f) }

    LaunchedEffect(key1 = Unit) {
        // Run animations
        logoAlpha.animateTo(1f, animationSpec = tween(400))
        logoScale.animateTo(
            1f,
            animationSpec = tween(600, easing = EaseOutBack)
        )
        delay(200)
        titleAlpha.animateTo(1f, animationSpec = tween(500))
        delay(200)
        taglineAlpha.animateTo(1f, animationSpec = tween(500))
        delay(1000)

        // Check Firebase session
        val firebaseUser = FirebaseAuth.getInstance().currentUser

        if (firebaseUser == null) {
            // No session → go to login
            onNavigateToLogin()
            return@LaunchedEffect
        }

        // Reload user to confirm session is still valid
        try {
            firebaseUser.reload().await()
        } catch (e: Exception) {
            // Session expired → go to login
            FirebaseAuth.getInstance().signOut()
            onNavigateToLogin()
            return@LaunchedEffect
        }

        // Session valid → get role from Firestore
        try {
            val doc = FirebaseFirestore.getInstance()
                .collection("users")
                .document(firebaseUser.uid)
                .get()
                .await()

            if (!doc.exists()) {
                // User in Auth but not in Firestore → go to login
                FirebaseAuth.getInstance().signOut()
                onNavigateToLogin()
                return@LaunchedEffect
            }

            val roleString = doc.getString("role") ?: "STUDENT"
            val role = try {
                UserRole.valueOf(roleString)
            } catch (e: Exception) {
                UserRole.STUDENT
            }
            onNavigateToDashboard(role)

        } catch (e: Exception) {
            // Firestore error → go to login safely
            onNavigateToLogin()
        }
    }

    // UI — same as before
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(PrimaryBlue, PrimaryBlueDark)
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.padding(24.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(120.dp)
                    .scale(logoScale.value)
                    .alpha(logoAlpha.value)
                    .clip(CircleShape)
                    .background(
                        MaterialTheme.colorScheme.onPrimary
                            .copy(alpha = 0.15f)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Filled.DirectionsBus,
                    contentDescription = "UniTransport Logo",
                    tint = MaterialTheme.colorScheme.onPrimary,
                    modifier = Modifier.size(64.dp)
                )
            }
            Spacer(modifier = Modifier.height(32.dp))
            Text(
                text = "UniTransport",
                style = MaterialTheme.typography.headlineLarge,
                color = MaterialTheme.colorScheme.onPrimary,
                textAlign = TextAlign.Center,
                modifier = Modifier.alpha(titleAlpha.value)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "University Transport Management",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onPrimary
                    .copy(alpha = 0.8f),
                textAlign = TextAlign.Center,
                modifier = Modifier.alpha(taglineAlpha.value)
            )
        }
        Text(
            text = "Version 1.0.0",
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onPrimary
                .copy(alpha = 0.5f),
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 32.dp)
                .alpha(taglineAlpha.value)
        )
    }
}