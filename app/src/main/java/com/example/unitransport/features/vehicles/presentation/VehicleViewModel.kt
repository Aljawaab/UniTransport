package com.example.unitransport.features.vehicles.presentation

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.unitransport.core.base.UiState
import com.example.unitransport.data.repository.VehicleRepository
import com.example.unitransport.features.vehicles.model.Vehicle
import com.example.unitransport.features.vehicles.model.VehicleStatus
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class VehicleViewModel @Inject constructor(
    private val vehicleRepository: VehicleRepository
) : ViewModel() {

    private val _vehiclesState =
        MutableStateFlow<UiState<List<Vehicle>>>(UiState.Loading)
    val vehiclesState: StateFlow<UiState<List<Vehicle>>> =
        _vehiclesState.asStateFlow()

    private val _selectedVehicle =
        MutableStateFlow<UiState<Vehicle>>(UiState.Idle)
    val selectedVehicle: StateFlow<UiState<Vehicle>> =
        _selectedVehicle.asStateFlow()

    var searchQuery by mutableStateOf("")
        private set
    var selectedFilter by mutableStateOf<VehicleStatus?>(null)
        private set

    // Cache all vehicles for filtering
    private var allVehicles: List<Vehicle> = emptyList()

    fun loadVehicles() {
        viewModelScope.launch {
            _vehiclesState.value = UiState.Loading
            try {
                // Real-time listener from Firestore
                vehicleRepository.getVehicles().collect { vehicles ->
                    allVehicles = vehicles
                    applyFilters()
                }
            } catch (e: Exception) {
                _vehiclesState.value = UiState.Error(
                    e.message ?: "Failed to load vehicles"
                )
            }
        }
    }

    fun loadVehicleById(vehicleId: String) {
        viewModelScope.launch {
            _selectedVehicle.value = UiState.Loading
            val vehicle = vehicleRepository.getVehicleById(vehicleId)
            if (vehicle != null) {
                _selectedVehicle.value = UiState.Success(vehicle)
            } else {
                _selectedVehicle.value = UiState.Error(
                    "Vehicle not found"
                )
            }
        }
    }

    fun onSearchQueryChange(query: String) {
        searchQuery = query
        applyFilters()
    }

    fun onFilterSelected(status: VehicleStatus?) {
        selectedFilter = status
        applyFilters()
    }

    private fun applyFilters() {
        val filtered = allVehicles.filter { vehicle ->
            val matchesSearch = searchQuery.isBlank() ||
                    vehicle.registrationNumber.contains(
                        searchQuery, ignoreCase = true
                    ) ||
                    vehicle.make.contains(
                        searchQuery, ignoreCase = true
                    ) ||
                    vehicle.model.contains(
                        searchQuery, ignoreCase = true
                    ) ||
                    vehicle.type.displayName.contains(
                        searchQuery, ignoreCase = true
                    )
            val matchesFilter = selectedFilter == null ||
                    vehicle.status == selectedFilter
            matchesSearch && matchesFilter
        }
        _vehiclesState.value = UiState.Success(filtered)
    }
}