package com.example.unitransport.features.profile.presentation

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.NotificationsOff
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.WifiOff
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.unitransport.core.ui.theme.StatusApproved

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    onNavigateBack: () -> Unit = {},
    viewModel: ProfileViewModel = hiltViewModel()
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Settings",
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

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp)
        ) {
            // Appearance
            item {
                Spacer(modifier = Modifier.height(16.dp))
                SettingsSectionLabel(title = "Appearance")
                Spacer(modifier = Modifier.height(8.dp))
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = MaterialTheme.shapes.large,
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    ),
                    elevation = CardDefaults.cardElevation(2.dp)
                ) {
                    SettingsToggleItem(
                        icon = Icons.Filled.DarkMode,
                        title = "Dark Mode",
                        subtitle = "Switch between light and dark theme",
                        checked = viewModel.isDarkMode,
                        onCheckedChange = { viewModel.toggleDarkMode() }
                    )
                }
            }

            // Notifications
            item {
                Spacer(modifier = Modifier.height(16.dp))
                SettingsSectionLabel(title = "Notifications")
                Spacer(modifier = Modifier.height(8.dp))
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = MaterialTheme.shapes.large,
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    ),
                    elevation = CardDefaults.cardElevation(2.dp)
                ) {
                    SettingsToggleItem(
                        icon = Icons.Filled.Notifications,
                        title = "Push Notifications",
                        subtitle = "Receive alerts for booking updates",
                        checked = viewModel.notificationsEnabled,
                        onCheckedChange = { viewModel.toggleNotifications() }
                    )
                    Divider(
                        color = MaterialTheme.colorScheme.outlineVariant
                            .copy(alpha = 0.5f),
                        modifier = Modifier.padding(horizontal = 16.dp)
                    )
                    SettingsToggleItem(
                        icon = Icons.Filled.NotificationsOff,
                        title = "Email Notifications",
                        subtitle = "Receive booking confirmations via email",
                        checked = viewModel.emailNotifications,
                        onCheckedChange = { viewModel.toggleEmailNotifications() }
                    )
                }
            }

            // Privacy & Security
            item {
                Spacer(modifier = Modifier.height(16.dp))
                SettingsSectionLabel(title = "Privacy & Security")
                Spacer(modifier = Modifier.height(8.dp))
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = MaterialTheme.shapes.large,
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    ),
                    elevation = CardDefaults.cardElevation(2.dp)
                ) {
                    SettingsInfoItem(
                        icon = Icons.Filled.Security,
                        title = "Data Privacy",
                        subtitle = "Your data is encrypted and stored securely " +
                                "on Firebase servers"
                    )
                    Divider(
                        color = MaterialTheme.colorScheme.outlineVariant
                            .copy(alpha = 0.5f),
                        modifier = Modifier.padding(horizontal = 16.dp)
                    )
                    SettingsInfoItem(
                        icon = Icons.Filled.WifiOff,
                        title = "Offline Mode",
                        subtitle = "App works on low connectivity and syncs " +
                                "when connection is restored"
                    )
                }
            }

            // App Info
            item {
                Spacer(modifier = Modifier.height(16.dp))
                SettingsSectionLabel(title = "About")
                Spacer(modifier = Modifier.height(8.dp))
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = MaterialTheme.shapes.large,
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    ),
                    elevation = CardDefaults.cardElevation(2.dp)
                ) {
                    SettingsInfoItem(
                        icon = Icons.Filled.Info,
                        title = "App Version",
                        subtitle = "UniTransport v1.0.0"
                    )
                    Divider(
                        color = MaterialTheme.colorScheme.outlineVariant
                            .copy(alpha = 0.5f),
                        modifier = Modifier.padding(horizontal = 16.dp)
                    )
                    SettingsInfoItem(
                        icon = Icons.Filled.Phone,
                        title = "Support",
                        subtitle = "transport@university.ac.ke  |  +254 700 000 000"
                    )
                    Divider(
                        color = MaterialTheme.colorScheme.outlineVariant
                            .copy(alpha = 0.5f),
                        modifier = Modifier.padding(horizontal = 16.dp)
                    )
                    SettingsInfoItem(
                        icon = Icons.Filled.Language,
                        title = "Platform",
                        subtitle = "Built with Kotlin + Jetpack Compose + Firebase"
                    )
                }
            }

            item { Spacer(modifier = Modifier.height(32.dp)) }
        }
    }
}

@Composable
private fun SettingsSectionLabel(title: String) {
    Text(
        text = title.uppercase(),
        style = MaterialTheme.typography.labelSmall,
        color = MaterialTheme.colorScheme.primary,
        modifier = Modifier.padding(horizontal = 4.dp)
    )
}

@Composable
private fun SettingsToggleItem(
    icon: ImageVector,
    title: String,
    subtitle: String,
    checked: Boolean,
    onCheckedChange: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(22.dp)
        )
        Column(
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 16.dp)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        Switch(
            checked = checked,
            onCheckedChange = { onCheckedChange() },
            colors = SwitchDefaults.colors(
                checkedThumbColor = MaterialTheme.colorScheme.onPrimary,
                checkedTrackColor = StatusApproved
            )
        )
    }
}

@Composable
private fun SettingsInfoItem(
    icon: ImageVector,
    title: String,
    subtitle: String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(22.dp)
        )
        Column(
            modifier = Modifier.padding(start = 16.dp)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}