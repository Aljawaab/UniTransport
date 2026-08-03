package com.example.unitransport.features.notifications.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.unitransport.core.base.UiState
import com.example.unitransport.data.repository.AuthRepository
import com.example.unitransport.data.repository.NotificationRepository
import com.example.unitransport.features.notifications.model.AppNotification
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NotificationsViewModel @Inject constructor(
    private val notificationRepository: NotificationRepository,
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _notificationsState =
        MutableStateFlow<UiState<List<AppNotification>>>(UiState.Loading)
    val notificationsState: StateFlow<UiState<List<AppNotification>>> =
        _notificationsState.asStateFlow()

    fun loadNotifications() {
        viewModelScope.launch {
            _notificationsState.value = UiState.Loading
            val uid = authRepository.currentUserId
            if (uid == null) {
                _notificationsState.value = UiState.Error("Not logged in")
                return@launch
            }
            try {
                notificationRepository.getNotificationsForUser(uid).collect { notifications ->
                    _notificationsState.value = UiState.Success(notifications)
                }
            } catch (e: Exception) {
                _notificationsState.value = UiState.Error(
                    e.message ?: "Failed to load notifications"
                )
            }
        }
    }

    fun markAsRead(notificationId: String) {
        viewModelScope.launch {
            notificationRepository.markAsRead(notificationId)
        }
    }

    fun markAllAsRead() {
        viewModelScope.launch {
            val uid = authRepository.currentUserId ?: return@launch
            notificationRepository.markAllAsRead(uid)
        }
    }

    fun groupNotifications(
        notifications: List<AppNotification>
    ): Map<String, List<AppNotification>> {
        val today = notifications.filter {
            it.time.contains("hour") || it.time.contains("minute") || it.time == "Just now"
        }
        val earlierIds = notifications.filterNot { today.contains(it) }
        return buildMap {
            if (today.isNotEmpty()) put("Today", today)
            if (earlierIds.isNotEmpty()) put("Earlier", earlierIds)
        }
    }
}