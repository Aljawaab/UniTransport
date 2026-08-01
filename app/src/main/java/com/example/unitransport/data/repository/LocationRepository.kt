package com.example.unitransport.data.repository

import com.example.unitransport.features.driver.model.LiveLocation
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LocationRepository @Inject constructor() {

    private val database = FirebaseDatabase.getInstance()
    private val locationsRef = database.getReference("liveLocations")

    // Driver pushes live location to Realtime Database
    suspend fun updateDriverLocation(
        driverId: String,
        location: LiveLocation
    ): Result<Unit> {
        return try {
            val locationData = mapOf(
                "latitude" to location.latitude,
                "longitude" to location.longitude,
                "speed" to location.speed,
                "timestamp" to location.timestamp,
                "isSharing" to location.isSharing
            )
            locationsRef
                .child(driverId)
                .setValue(locationData)
                .await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // Officer listens to a specific driver's location
    fun getDriverLocation(driverId: String): Flow<LiveLocation?> =
        callbackFlow {
            val listener = object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (!snapshot.exists()) {
                        trySend(null)
                        return
                    }
                    val location = LiveLocation(
                        latitude = snapshot.child("latitude")
                            .getValue(Double::class.java) ?: 0.0,
                        longitude = snapshot.child("longitude")
                            .getValue(Double::class.java) ?: 0.0,
                        speed = snapshot.child("speed")
                            .getValue(Double::class.java) ?: 0.0,
                        timestamp = snapshot.child("timestamp")
                            .getValue(String::class.java) ?: "",
                        isSharing = snapshot.child("isSharing")
                            .getValue(Boolean::class.java) ?: false
                    )
                    trySend(location)
                }

                override fun onCancelled(error: DatabaseError) {
                    trySend(null)
                }
            }
            val ref = locationsRef.child(driverId)
            ref.addValueEventListener(listener)
            awaitClose { ref.removeEventListener(listener) }
        }

    // Stop sharing location
    suspend fun stopSharing(driverId: String) {
        try {
            locationsRef.child(driverId)
                .child("isSharing")
                .setValue(false)
                .await()
        } catch (e: Exception) {
            // Silently fail
        }
    }
}