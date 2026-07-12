package com.example.unitransport.features.driver.presentation

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.unitransport.core.base.UiState
import com.example.unitransport.features.driver.model.DriverRatingSummary
import com.example.unitransport.features.driver.model.Rating
import com.example.unitransport.features.driver.model.RatingType
import com.example.unitransport.features.driver.model.mockDriverRatings
import com.example.unitransport.features.driver.model.mockRatings
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RatingViewModel @Inject constructor() : ViewModel() {

    // Rating submission state
    private val _submitState =
        MutableStateFlow<UiState<Unit>>(UiState.Idle)
    val submitState: StateFlow<UiState<Unit>> =
        _submitState.asStateFlow()

    // Driver ratings for officer view
    private val _driverRatingsState =
        MutableStateFlow<UiState<List<DriverRatingSummary>>>(UiState.Loading)
    val driverRatingsState: StateFlow<UiState<List<DriverRatingSummary>>> =
        _driverRatingsState.asStateFlow()

    // Single driver rating detail
    private val _driverDetailState =
        MutableStateFlow<UiState<DriverRatingSummary>>(UiState.Idle)
    val driverDetailState: StateFlow<UiState<DriverRatingSummary>> =
        _driverDetailState.asStateFlow()

    // Form fields
    var selectedStars by mutableIntStateOf(0)
        private set
    var comment by mutableStateOf("")
        private set
    var commentError by mutableStateOf<String?>(null)
        private set

    fun onStarSelected(stars: Int) {
        selectedStars = stars
    }

    fun onCommentChange(value: String) {
        comment = value
        commentError = null
    }

    fun submitRating(
        tripId: String,
        bookingId: String,
        raterName: String,
        raterRole: String,
        targetName: String,
        targetId: String,
        type: RatingType,
        onSuccess: () -> Unit
    ) {
        if (selectedStars == 0) {
            commentError = "Please select a star rating"
            return
        }

        viewModelScope.launch {
            _submitState.value = UiState.Loading
            delay(1500)
            // In Step 16 this saves to Firestore ratings collection
            _submitState.value = UiState.Success(Unit)
            onSuccess()
        }
    }

    fun loadDriverRatings() {
        viewModelScope.launch {
            _driverRatingsState.value = UiState.Loading
            delay(600)
            _driverRatingsState.value =
                UiState.Success(mockDriverRatings)
        }
    }

    fun loadDriverRatingById(driverId: String) {
        viewModelScope.launch {
            _driverDetailState.value = UiState.Loading
            delay(400)
            val summary = mockDriverRatings.find {
                it.driverId == driverId
            }
            if (summary != null) {
                _driverDetailState.value = UiState.Success(summary)
            } else {
                _driverDetailState.value =
                    UiState.Error("No ratings found")
            }
        }
    }

    fun resetSubmitState() {
        _submitState.value = UiState.Idle
        selectedStars = 0
        comment = ""
    }
}