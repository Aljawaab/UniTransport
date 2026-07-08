package com.example.unitransport.features.admin.presentation

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.unitransport.core.base.UiState
import com.example.unitransport.features.admin.model.AdminReport
import com.example.unitransport.features.admin.model.SystemLog
import com.example.unitransport.features.admin.model.SystemUser
import com.example.unitransport.features.admin.model.mockAdminReport
import com.example.unitransport.features.admin.model.mockSystemLogs
import com.example.unitransport.features.admin.model.mockSystemUsers
import com.example.unitransport.features.auth.model.UserRole
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AdminViewModel @Inject constructor() : ViewModel() {

    // Report state
    private val _reportState =
        MutableStateFlow<UiState<AdminReport>>(UiState.Loading)
    val reportState: StateFlow<UiState<AdminReport>> =
        _reportState.asStateFlow()

    // Users state
    private val _usersState =
        MutableStateFlow<UiState<List<SystemUser>>>(UiState.Loading)
    val usersState: StateFlow<UiState<List<SystemUser>>> =
        _usersState.asStateFlow()

    // Logs state
    private val _logsState =
        MutableStateFlow<UiState<List<SystemLog>>>(UiState.Loading)
    val logsState: StateFlow<UiState<List<SystemLog>>> =
        _logsState.asStateFlow()

    // Toggle state
    private val _toggleState =
        MutableStateFlow<UiState<Unit>>(UiState.Idle)
    val toggleState: StateFlow<UiState<Unit>> =
        _toggleState.asStateFlow()

    // Search
    var searchQuery by mutableStateOf("")
        private set

    // Selected role filter
    var selectedRoleFilter by mutableStateOf<UserRole?>(null)
        private set

    private val allUsers = mockSystemUsers.toMutableList()

    fun loadReport() {
        viewModelScope.launch {
            _reportState.value = UiState.Loading
            delay(800)
            _reportState.value = UiState.Success(mockAdminReport)
        }
    }

    fun loadUsers() {
        viewModelScope.launch {
            _usersState.value = UiState.Loading
            delay(600)
            _usersState.value = UiState.Success(allUsers.toList())
        }
    }

    fun loadLogs() {
        viewModelScope.launch {
            _logsState.value = UiState.Loading
            delay(600)
            _logsState.value = UiState.Success(mockSystemLogs)
        }
    }

    fun onSearchQueryChange(query: String) {
        searchQuery = query
        applyUserFilters()
    }

    fun onRoleFilterChange(role: UserRole?) {
        selectedRoleFilter = role
        applyUserFilters()
    }

    private fun applyUserFilters() {
        val filtered = allUsers.filter { user ->
            val matchesSearch = searchQuery.isBlank() ||
                    user.fullName.contains(searchQuery, ignoreCase = true) ||
                    user.email.contains(searchQuery, ignoreCase = true) ||
                    user.department.contains(searchQuery, ignoreCase = true)
            val matchesRole = selectedRoleFilter == null ||
                    user.role == selectedRoleFilter
            matchesSearch && matchesRole
        }
        _usersState.value = UiState.Success(filtered)
    }

    fun toggleUserStatus(userId: String) {
        viewModelScope.launch {
            _toggleState.value = UiState.Loading
            delay(1000)
            val index = allUsers.indexOfFirst { it.id == userId }
            if (index != -1) {
                allUsers[index] = allUsers[index].copy(
                    isActive = !allUsers[index].isActive
                )
                applyUserFilters()
            }
            _toggleState.value = UiState.Success(Unit)
            delay(500)
            _toggleState.value = UiState.Idle
        }
    }
}