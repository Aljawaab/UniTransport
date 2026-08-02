package com.example.unitransport.data.repository

import com.example.unitransport.features.driver.model.Rating
import com.example.unitransport.features.driver.model.RatingType
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RatingRepository @Inject constructor() {
    private val firestore = FirebaseFirestore.getInstance()
    private val ratingsCollection = firestore.collection("ratings")

    suspend fun submitRating(
        tripId: String,
        bookingId: String,
        raterName: String,
        raterRole: String,
        targetName: String,
        targetId: String,
        stars: Int,
        comment: String,
        type: RatingType
    ): Result<Unit> {
        return try {
            val data = hashMapOf(
                "tripId" to tripId,
                "bookingId" to bookingId,
                "raterName" to raterName,
                "raterRole" to raterRole,
                "targetName" to targetName,
                "targetId" to targetId,
                "stars" to stars,
                "comment" to comment,
                "type" to type.name,
                "timestamp" to System.currentTimeMillis()
            )
            ratingsCollection.add(data).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // Real-time ratings where targetId matches (used for a specific driver's ratings)
    fun getRatingsForTarget(targetId: String): Flow<List<Rating>> = callbackFlow {
        val listener = ratingsCollection
            .whereEqualTo("targetId", targetId)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    trySend(emptyList())
                    return@addSnapshotListener
                }
                val ratings = snapshot?.documents?.mapNotNull { doc ->
                    try {
                        Rating(
                            id = doc.id,
                            tripId = doc.getString("tripId") ?: "",
                            bookingId = doc.getString("bookingId") ?: "",
                            raterName = doc.getString("raterName") ?: "",
                            raterRole = doc.getString("raterRole") ?: "",
                            targetName = doc.getString("targetName") ?: "",
                            targetId = doc.getString("targetId") ?: "",
                            stars = (doc.getLong("stars") ?: 0L).toInt(),
                            comment = doc.getString("comment") ?: "",
                            timestamp = (doc.getLong("timestamp") ?: 0L).toString(),
                            type = RatingType.valueOf(
                                doc.getString("type") ?: "BOOKER_RATES_DRIVER"
                            )
                        )
                    } catch (e: Exception) {
                        null
                    }
                } ?: emptyList()
                trySend(ratings)
            }
        awaitClose { listener.remove() }
    }

    // All driver ratings (type == BOOKER_RATES_DRIVER), for Officer's full list view
    fun getAllDriverRatings(): Flow<List<Rating>> = callbackFlow {
        val listener = ratingsCollection
            .whereEqualTo("type", RatingType.BOOKER_RATES_DRIVER.name)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    trySend(emptyList())
                    return@addSnapshotListener
                }
                val ratings = snapshot?.documents?.mapNotNull { doc ->
                    try {
                        Rating(
                            id = doc.id,
                            tripId = doc.getString("tripId") ?: "",
                            bookingId = doc.getString("bookingId") ?: "",
                            raterName = doc.getString("raterName") ?: "",
                            raterRole = doc.getString("raterRole") ?: "",
                            targetName = doc.getString("targetName") ?: "",
                            targetId = doc.getString("targetId") ?: "",
                            stars = (doc.getLong("stars") ?: 0L).toInt(),
                            comment = doc.getString("comment") ?: "",
                            timestamp = (doc.getLong("timestamp") ?: 0L).toString(),
                            type = RatingType.BOOKER_RATES_DRIVER
                        )
                    } catch (e: Exception) {
                        null
                    }
                } ?: emptyList()
                trySend(ratings)
            }
        awaitClose { listener.remove() }
    }

    // Check if a rating already exists for this booking + type combo
    suspend fun hasRated(bookingId: String, type: RatingType): Boolean {
        return try {
            val snapshot = ratingsCollection
                .whereEqualTo("bookingId", bookingId)
                .whereEqualTo("type", type.name)
                .limit(1)
                .get()
                .await()
            !snapshot.isEmpty
        } catch (e: Exception) {
            false
        }
    }

    // Returns the set of bookingIds that already have a BOOKER_RATES_DRIVER rating
    suspend fun getRatedBookingIds(bookingIds: List<String>): Set<String> {
        if (bookingIds.isEmpty()) return emptySet()
        return try {
            // Firestore 'in' queries max out at 30 items per query
            bookingIds.chunked(30).flatMap { chunk ->
                ratingsCollection
                    .whereIn("bookingId", chunk)
                    .whereEqualTo("type", RatingType.BOOKER_RATES_DRIVER.name)
                    .get()
                    .await()
                    .documents
                    .mapNotNull { it.getString("bookingId") }
            }.toSet()
        } catch (e: Exception) {
            emptySet()
        }
    }
}