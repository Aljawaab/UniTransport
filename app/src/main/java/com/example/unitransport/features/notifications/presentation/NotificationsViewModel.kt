package com.example.unitransport.features.notifications.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.unitransport.core.base.UiState
import com.example.unitransport.features.notifications.model.AppNotification
import com.example.unitransport.features.notifications.model.mockNotifications
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NotificationsViewModel @Inject constructor() : ViewModel() {

    private val _notificationsState =
        MutableStateFlow<UiState<List<AppNotification>>>(UiState.Loading)
    val notificationsState: StateFlow<UiState<List<AppNotification>>> =
        _notificationsState.asStateFlow()

    private val _allNotifications =
        MutableStateFlow<List<AppNotification>>(emptyList())

    val unreadCount: Int
        get() = _allNotifications.value.count { !it.isRead }

    fun loadNotifications() {
        viewModelScope.launch {
            _notificationsState.value = UiState.Loading
            delay(600)
            _allNotifications.value = mockNotifications
            _notificationsState.value = UiState.Success(mockNotifications)
        }
    }

    fun markAsRead(notificationId: String) {
        viewModelScope.launch {
            val updated = _allNotifications.value.map { notification ->
                if (notification.id == notificationId) {
                    notification.copy(isRead = true)
                } else {
                    notification
                }
            }
            _allNotifications.value = updated
            _notificationsState.value = UiState.Success(updated)
        }
    }

    fun markAllAsRead() {
        viewModelScope.launch {
            val updated = _allNotifications.value.map {
                it.copy(isRead = true)
            }
            _allNotifications.value = updated
            _notificationsState.value = UiState.Success(updated)
        }
    }

    // Group notifications into Today and Earlier
    fun groupNotifications(
        notifications: List<AppNotification>
    ): Map<String, List<AppNotification>> {
        val today = notifications.filter {
            it.time.contains("hour") || it.time == "Just now"
        }
        val earlier = notifications.filter {
            !it.time.contains("hour") && it.time != "Just now"
        }
        return buildMap {
            if (today.isNotEmpty()) put("Today", today)
            if (earlier.isNotEmpty()) put("Earlier", earlier)
        }
    }
}