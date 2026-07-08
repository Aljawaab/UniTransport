package com.example.unitransport.features.vehicles.presentation

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.unitransport.core.base.UiState
import com.example.unitransport.features.vehicles.model.Vehicle
import com.example.unitransport.features.vehicles.model.VehicleMockData
import com.example.unitransport.features.vehicles.model.VehicleStatus
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class VehicleViewModel @Inject constructor() : ViewModel() {

    private val _vehiclesState =
        MutableStateFlow<UiState<List<Vehicle>>>(UiState.Loading)
    val vehiclesState: StateFlow<UiState<List<Vehicle>>> =
        _vehiclesState.asStateFlow()

    var searchQuery by mutableStateOf("")
        private set

    var selectedFilter by mutableStateOf<VehicleStatus?>(null)
        private set

    private val allVehicles = VehicleMockData.vehicles

    fun loadVehicles() {
        viewModelScope.launch {
            _vehiclesState.value = UiState.Loading
            delay(800)
            _vehiclesState.value = UiState.Success(allVehicles)
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
        viewModelScope.launch {
            val filtered = allVehicles.filter { vehicle ->
                val matchesSearch = searchQuery.isBlank() ||
                        vehicle.registrationNumber.contains(
                            searchQuery, ignoreCase = true
                        ) ||
                        vehicle.make.contains(searchQuery, ignoreCase = true) ||
                        vehicle.model.contains(searchQuery, ignoreCase = true) ||
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

    // Add these two new properties and function
// inside the VehicleViewModel class

    private val _selectedVehicle = MutableStateFlow<UiState<Vehicle>>(UiState.Idle)
    val selectedVehicle: StateFlow<UiState<Vehicle>> = _selectedVehicle.asStateFlow()

    fun loadVehicleById(vehicleId: String) {
        viewModelScope.launch {
            _selectedVehicle.value = UiState.Loading
            delay(500)
            val vehicle = allVehicles.find { it.id == vehicleId }
            if (vehicle != null) {
                _selectedVehicle.value = UiState.Success(vehicle)
            } else {
                _selectedVehicle.value = UiState.Error("Vehicle not found")
            }
        }
    }
}
