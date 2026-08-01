package com.example.unitransport.features.driver.presentation

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.unitransport.core.base.UiState
import com.example.unitransport.data.repository.RatingRepository
import com.example.unitransport.data.repository.UserRepository
import com.example.unitransport.features.driver.model.DriverRatingSummary
import com.example.unitransport.features.driver.model.RatingType
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RatingViewModel @Inject constructor(
    private val ratingRepository: RatingRepository,
    private val userRepository: UserRepository
) : ViewModel() {

    private val _submitState =
        MutableStateFlow<UiState<Unit>>(UiState.Idle)
    val submitState: StateFlow<UiState<Unit>> = _submitState.asStateFlow()

    private val _driverRatingsState =
        MutableStateFlow<UiState<List<DriverRatingSummary>>>(UiState.Loading)
    val driverRatingsState: StateFlow<UiState<List<DriverRatingSummary>>> =
        _driverRatingsState.asStateFlow()

    private val _driverDetailState =
        MutableStateFlow<UiState<DriverRatingSummary>>(UiState.Idle)
    val driverDetailState: StateFlow<UiState<DriverRatingSummary>> =
        _driverDetailState.asStateFlow()

    var selectedStars by mutableIntStateOf(0)
        private set
    var comment by mutableStateOf("")
        private set
    var commentError by mutableStateOf<String?>(null)
        private set

    fun onStarSelected(stars: Int) { selectedStars = stars }
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
            val result = ratingRepository.submitRating(
                tripId = tripId,
                bookingId = bookingId,
                raterName = raterName,
                raterRole = raterRole,
                targetName = targetName,
                targetId = targetId,
                stars = selectedStars,
                comment = comment,
                type = type
            )
            result.fold(
                onSuccess = {
                    _submitState.value = UiState.Success(Unit)
                    onSuccess()
                },
                onFailure = { error ->
                    _submitState.value = UiState.Error(
                        error.message ?: "Failed to submit rating"
                    )
                }
            )
        }
    }

    fun loadDriverRatings() {
        viewModelScope.launch {
            _driverRatingsState.value = UiState.Loading
            try {
                ratingRepository.getAllDriverRatings().collect { ratings ->
                    val grouped = ratings.groupBy { it.targetId }
                    val summaries = grouped.map { (driverId, driverRatings) ->
                        val profile = userRepository.getUserById(driverId)
                        DriverRatingSummary(
                            driverId = driverId,
                            driverName = profile?.fullName
                                ?: driverRatings.firstOrNull()?.targetName
                                ?: "Unknown",
                            averageRating = if (driverRatings.isNotEmpty())
                                driverRatings.map { it.stars }.average().toFloat()
                            else 0f,
                            totalRatings = driverRatings.size,
                            ratings = driverRatings
                        )
                    }
                    _driverRatingsState.value = UiState.Success(summaries)
                }
            } catch (e: Exception) {
                _driverRatingsState.value = UiState.Error(
                    e.message ?: "Failed to load ratings"
                )
            }
        }
    }

    fun loadDriverRatingById(driverId: String) {
        viewModelScope.launch {
            _driverDetailState.value = UiState.Loading
            try {
                ratingRepository.getRatingsForTarget(driverId).collect { ratings ->
                    if (ratings.isEmpty()) {
                        _driverDetailState.value = UiState.Error("No ratings found")
                        return@collect
                    }
                    val profile = userRepository.getUserById(driverId)
                    val summary = DriverRatingSummary(
                        driverId = driverId,
                        driverName = profile?.fullName
                            ?: ratings.first().targetName,
                        averageRating = ratings.map { it.stars }.average().toFloat(),
                        totalRatings = ratings.size,
                        ratings = ratings
                    )
                    _driverDetailState.value = UiState.Success(summary)
                }
            } catch (e: Exception) {
                _driverDetailState.value = UiState.Error(
                    e.message ?: "Failed to load driver ratings"
                )
            }
        }
    }

    fun resetSubmitState() {
        _submitState.value = UiState.Idle
        selectedStars = 0
        comment = ""
    }
}