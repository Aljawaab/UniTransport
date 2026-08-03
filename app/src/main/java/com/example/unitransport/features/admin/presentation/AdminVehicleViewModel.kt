package com.example.unitransport.features.admin.presentation

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.unitransport.core.base.UiState
import com.example.unitransport.data.repository.BookingRepository
import com.example.unitransport.data.repository.VehicleRepository
import com.example.unitransport.features.vehicles.model.Vehicle
import com.example.unitransport.features.vehicles.model.VehicleDisplayStatus
import com.example.unitransport.features.vehicles.model.VehicleStatus
import com.example.unitransport.features.vehicles.model.VehicleType
import com.example.unitransport.features.vehicles.model.computeVehicleDisplayStatus
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AdminVehicleViewModel @Inject constructor(
    private val vehicleRepository: VehicleRepository,
    private val bookingRepository: BookingRepository
) : ViewModel() {

    private val _vehiclesState =
        MutableStateFlow<UiState<List<Vehicle>>>(UiState.Loading)
    val vehiclesState: StateFlow<UiState<List<Vehicle>>> = _vehiclesState.asStateFlow()

    private val _selectedVehicle =
        MutableStateFlow<UiState<Vehicle>>(UiState.Idle)
    val selectedVehicle: StateFlow<UiState<Vehicle>> = _selectedVehicle.asStateFlow()

    private val _saveState = MutableStateFlow<UiState<Unit>>(UiState.Idle)
    val saveState: StateFlow<UiState<Unit>> = _saveState.asStateFlow()

    private val _deleteState = MutableStateFlow<UiState<Unit>>(UiState.Idle)
    val deleteState: StateFlow<UiState<Unit>> = _deleteState.asStateFlow()

    private val _displayStatusMap =
        MutableStateFlow<Map<String, VehicleDisplayStatus>>(emptyMap())
    val displayStatusMap: StateFlow<Map<String, VehicleDisplayStatus>> =
        _displayStatusMap.asStateFlow()

    // Form fields
    var formRegistration by mutableStateOf("")
        private set
    var formMake by mutableStateOf("")
        private set
    var formModel by mutableStateOf("")
        private set
    var formType by mutableStateOf(VehicleType.BUS)
        private set
    var formCapacity by mutableStateOf("")
        private set
    var formStatus by mutableStateOf(VehicleStatus.AVAILABLE)
        private set
    var formDescription by mutableStateOf("")
        private set
    var formYear by mutableStateOf("")
        private set
    var formFuelType by mutableStateOf("Diesel")
        private set

    private var editingVehicleId: String? = null

    fun loadVehicles() {
        viewModelScope.launch {
            _vehiclesState.value = UiState.Loading
            try {
                kotlinx.coroutines.flow.combine(
                    vehicleRepository.getVehicles(),
                    bookingRepository.getAllBookings()
                ) { vehicles, bookings -> vehicles to bookings }
                    .collect { (vehicles, bookings) ->
                        _vehiclesState.value = UiState.Success(vehicles)
                        _displayStatusMap.value = vehicles.associate { v ->
                            v.id to computeVehicleDisplayStatus(v, bookings)
                        }
                    }
            } catch (e: Exception) {
                _vehiclesState.value = UiState.Error(
                    e.message ?: "Failed to load vehicles"
                )
            }
        }
    }

    fun startNewVehicle() {
        editingVehicleId = null
        formRegistration = ""
        formMake = ""
        formModel = ""
        formType = VehicleType.BUS
        formCapacity = ""
        formStatus = VehicleStatus.AVAILABLE
        formDescription = ""
        formYear = ""
        formFuelType = "Diesel"
    }

    fun loadVehicleForEdit(vehicleId: String) {
        viewModelScope.launch {
            val vehicle = vehicleRepository.getVehicleById(vehicleId)
            if (vehicle != null) {
                editingVehicleId = vehicle.id
                formRegistration = vehicle.registrationNumber
                formMake = vehicle.make
                formModel = vehicle.model
                formType = vehicle.type
                formCapacity = vehicle.capacity.toString()
                formStatus = vehicle.status
                formDescription = vehicle.description
                formYear = vehicle.yearOfManufacture.toString()
                formFuelType = vehicle.fuelType
            }
        }
    }

    fun onRegistrationChange(v: String) { formRegistration = v }
    fun onMakeChange(v: String) { formMake = v }
    fun onModelChange(v: String) { formModel = v }
    fun onTypeChange(v: VehicleType) { formType = v }
    fun onCapacityChange(v: String) { formCapacity = v }
    fun onStatusChange(v: VehicleStatus) { formStatus = v }
    fun onDescriptionChange(v: String) { formDescription = v }
    fun onYearChange(v: String) { formYear = v }
    fun onFuelTypeChange(v: String) { formFuelType = v }

    fun saveVehicle(onSuccess: () -> Unit) {
        val capacityInt = formCapacity.toIntOrNull()
        val yearInt = formYear.toIntOrNull()
        if (formRegistration.isBlank() || formMake.isBlank() ||
            formModel.isBlank() || capacityInt == null || yearInt == null) {
            _saveState.value = UiState.Error("Please fill all required fields correctly")
            return
        }

        viewModelScope.launch {
            _saveState.value = UiState.Loading
            val vehicle = Vehicle(
                id = editingVehicleId ?: "",
                registrationNumber = formRegistration,
                make = formMake,
                model = formModel,
                type = formType,
                capacity = capacityInt,
                status = formStatus,
                description = formDescription,
                yearOfManufacture = yearInt,
                fuelType = formFuelType
            )
            val result = if (editingVehicleId != null) {
                vehicleRepository.updateVehicle(vehicle)
            } else {
                vehicleRepository.addVehicle(vehicle)
            }
            result.fold(
                onSuccess = {
                    _saveState.value = UiState.Success(Unit)
                    onSuccess()
                },
                onFailure = { error ->
                    _saveState.value = UiState.Error(
                        error.message ?: "Failed to save vehicle"
                    )
                }
            )
        }
    }

    fun deleteVehicle(vehicle: Vehicle, onSuccess: () -> Unit) {
        viewModelScope.launch {
            _deleteState.value = UiState.Loading
            val inActiveTrip = vehicleRepository.isVehicleInActiveTrip(
                vehicle.registrationNumber
            )
            if (inActiveTrip) {
                _deleteState.value = UiState.Error(
                    "Can't delete — vehicle is on an active trip"
                )
                return@launch
            }
            val result = vehicleRepository.deleteVehicle(vehicle.id)
            result.fold(
                onSuccess = {
                    _deleteState.value = UiState.Success(Unit)
                    onSuccess()
                },
                onFailure = { error ->
                    _deleteState.value = UiState.Error(
                        error.message ?: "Failed to delete vehicle"
                    )
                }
            )
        }
    }

    fun resetSaveState() { _saveState.value = UiState.Idle }
    fun resetDeleteState() { _deleteState.value = UiState.Idle }
}