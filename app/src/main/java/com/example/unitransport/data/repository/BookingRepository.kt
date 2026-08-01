package com.example.unitransport.data.repository

import com.example.unitransport.features.bookings.model.Booking
import com.example.unitransport.features.bookings.model.BookingRequestStatus
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class BookingRepository @Inject constructor(
    private val authRepository: AuthRepository
) {
    private val firestore = FirebaseFirestore.getInstance()
    private val bookingsCollection = firestore.collection("bookings")

    // Create a new booking in Firestore
    suspend fun createBooking(booking: Booking): Result<String> {
        return try {
            val uid = authRepository.currentUserId
                ?: return Result.failure(
                    Exception("User not logged in")
                )
            val bookingData = hashMapOf(
                "userId" to uid,
                "destination" to booking.destination,
                "purpose" to booking.purpose,
                "passengerCount" to booking.passengerCount,
                "departureDate" to booking.departureDate,
                "departureTime" to booking.departureTime,
                "returnDate" to booking.returnDate,
                "returnTime" to booking.returnTime,
                "vehiclePreference" to booking.vehiclePreference,
                "additionalNotes" to booking.additionalNotes,
                "status" to BookingRequestStatus.PENDING.name,
                "createdAt" to System.currentTimeMillis(),
                "vehicleAssigned" to null,
                "driverAssigned" to null
            )
            val doc = bookingsCollection.add(bookingData).await()
            Result.success(doc.id)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // Get bookings for current user as a real-time Flow
    fun getUserBookings(): Flow<List<Booking>> = callbackFlow {
        val uid = authRepository.currentUserId ?: run {
            trySend(emptyList())
            close()
            return@callbackFlow
        }
        val listener = bookingsCollection
            .whereEqualTo("userId", uid)
            .orderBy("createdAt", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    trySend(emptyList())
                    return@addSnapshotListener
                }
                val bookings = snapshot?.documents?.map { doc ->
                    Booking(
                        id = doc.id,
                        userId = doc.getString("userId") ?: "",
                        driverId = doc.getString("driverId"),
                        tripStatus = doc.getString("tripStatus") ?: "UPCOMING",
                        destination = doc.getString("destination") ?: "",
                        purpose = doc.getString("purpose") ?: "",
                        passengerCount = (doc.getLong("passengerCount")
                            ?: 1L).toInt(),
                        departureDate = doc.getString("departureDate") ?: "",
                        departureTime = doc.getString("departureTime") ?: "",
                        returnDate = doc.getString("returnDate") ?: "",
                        returnTime = doc.getString("returnTime") ?: "",
                        vehiclePreference = doc.getString(
                            "vehiclePreference"
                        ) ?: "No Preference",
                        additionalNotes = doc.getString(
                            "additionalNotes"
                        ) ?: "",
                        status = try {
                            BookingRequestStatus.valueOf(
                                doc.getString("status") ?: "PENDING"
                            )
                        } catch (e: Exception) {
                            BookingRequestStatus.PENDING
                        },
                        createdAt = (doc.getLong("createdAt") ?: 0L).toString(),
                        vehicleAssigned = doc.getString("vehicleAssigned"),
                        driverAssigned = doc.getString("driverAssigned")
                    )
                } ?: emptyList()
                trySend(bookings)
            }
        awaitClose { listener.remove() }
    }

    // Get ALL bookings for officer/admin view
    fun getAllBookings(): Flow<List<Booking>> = callbackFlow {
        val listener = bookingsCollection
            .orderBy("createdAt", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    trySend(emptyList())
                    return@addSnapshotListener
                }
                val bookings = snapshot?.documents?.map { doc ->
                    Booking(
                        id = doc.id,
                        driverId = doc.getString("driverId"),
                        userId = doc.getString("userId") ?: "",
                        tripStatus = doc.getString("tripStatus") ?: "UPCOMING",
                        destination = doc.getString("destination") ?: "",
                        purpose = doc.getString("purpose") ?: "",
                        passengerCount = (doc.getLong("passengerCount")
                            ?: 1L).toInt(),
                        departureDate = doc.getString("departureDate") ?: "",
                        departureTime = doc.getString("departureTime") ?: "",
                        returnDate = doc.getString("returnDate") ?: "",
                        returnTime = doc.getString("returnTime") ?: "",
                        vehiclePreference = doc.getString(
                            "vehiclePreference"
                        ) ?: "No Preference",
                        additionalNotes = doc.getString(
                            "additionalNotes"
                        ) ?: "",
                        status = try {
                            BookingRequestStatus.valueOf(
                                doc.getString("status") ?: "PENDING"
                            )
                        } catch (e: Exception) {
                            BookingRequestStatus.PENDING
                        },
                        createdAt = (doc.getLong("createdAt") ?: 0L).toString(),
                        vehicleAssigned = doc.getString("vehicleAssigned"),
                        driverAssigned = doc.getString("driverAssigned")
                    )
                } ?: emptyList()
                trySend(bookings)
            }
        awaitClose { listener.remove() }
    }

    // Update booking status
    suspend fun updateBookingStatus(
        bookingId: String,
        status: BookingRequestStatus,
        vehicleAssigned: String? = null,
        driverAssigned: String? = null,
        driverId: String? = null
    ): Result<Unit> {
        return try {
            val updates = hashMapOf<String, Any?>(
                "status" to status.name,
                "vehicleAssigned" to vehicleAssigned,
                "driverAssigned" to driverAssigned,
                "driverId" to driverId
            )
            bookingsCollection.document(bookingId).update(updates).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // Get a single booking by ID as a real-time Flow
    fun getBookingById(bookingId: String): Flow<Booking?> = callbackFlow {
        val listener = bookingsCollection
            .document(bookingId)
            .addSnapshotListener { snapshot, error ->
                if (error != null || snapshot == null || !snapshot.exists()) {
                    trySend(null)
                    return@addSnapshotListener
                }
                val booking = Booking(
                    id = snapshot.id,
                    userId = snapshot.getString("userId") ?: "",
                    driverId = snapshot.getString("driverId"),
                    tripStatus = snapshot.getString("tripStatus") ?: "UPCOMING",
                    destination = snapshot.getString("destination") ?: "",
                    purpose = snapshot.getString("purpose") ?: "",
                    passengerCount = (snapshot.getLong("passengerCount")
                        ?: 1L).toInt(),
                    departureDate = snapshot.getString("departureDate") ?: "",
                    departureTime = snapshot.getString("departureTime") ?: "",
                    returnDate = snapshot.getString("returnDate") ?: "",
                    returnTime = snapshot.getString("returnTime") ?: "",
                    vehiclePreference = snapshot.getString(
                        "vehiclePreference"
                    ) ?: "No Preference",
                    additionalNotes = snapshot.getString(
                        "additionalNotes"
                    ) ?: "",
                    status = try {
                        BookingRequestStatus.valueOf(
                            snapshot.getString("status") ?: "PENDING"
                        )
                    } catch (e: Exception) {
                        BookingRequestStatus.PENDING
                    },
                    createdAt = (snapshot.getLong("createdAt") ?: 0L).toString(),
                    vehicleAssigned = snapshot.getString("vehicleAssigned"),
                    driverAssigned = snapshot.getString("driverAssigned")
                )
                trySend(booking)
            }
        awaitClose { listener.remove() }
    }

    // Bookings assigned to a specific driver (ACTIVE or COMPLETED)
    fun getBookingsForDriver(driverId: String): Flow<List<Booking>> = callbackFlow {
        val listener = bookingsCollection
            .whereEqualTo("driverId", driverId)
            .orderBy("createdAt", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    trySend(emptyList())
                    return@addSnapshotListener
                }
                val bookings = snapshot?.documents?.map { doc ->
                    Booking(
                        id = doc.id,
                        destination = doc.getString("destination") ?: "",
                        purpose = doc.getString("purpose") ?: "",
                        passengerCount = (doc.getLong("passengerCount") ?: 1L).toInt(),
                        departureDate = doc.getString("departureDate") ?: "",
                        departureTime = doc.getString("departureTime") ?: "",
                        returnDate = doc.getString("returnDate") ?: "",
                        returnTime = doc.getString("returnTime") ?: "",
                        vehiclePreference = doc.getString("vehiclePreference") ?: "No Preference",
                        additionalNotes = doc.getString("additionalNotes") ?: "",
                        status = try {
                            BookingRequestStatus.valueOf(doc.getString("status") ?: "PENDING")
                        } catch (e: Exception) { BookingRequestStatus.PENDING },
                        createdAt = (doc.getLong("createdAt") ?: 0L).toString(),
                        vehicleAssigned = doc.getString("vehicleAssigned"),
                        driverAssigned = doc.getString("driverAssigned"),
                        userId = doc.getString("userId") ?: "",
                        driverId = doc.getString("driverId"),
                        tripStatus = doc.getString("tripStatus") ?: "UPCOMING"
                    )
                }?.filter {
                    it.status == BookingRequestStatus.ACTIVE ||
                            it.status == BookingRequestStatus.COMPLETED
                } ?: emptyList()
                trySend(bookings)
            }
        awaitClose { listener.remove() }
    }

    // Update just the trip progress sub-state (UPCOMING -> IN_PROGRESS)
    suspend fun updateTripStatus(bookingId: String, tripStatus: String): Result<Unit> {
        return try {
            bookingsCollection.document(bookingId)
                .update("tripStatus", tripStatus)
                .await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // Updates ONLY the status field, without touching vehicle/driver assignment
    suspend fun updateBookingStatusOnly(
        bookingId: String,
        status: BookingRequestStatus
    ): Result<Unit> {
        return try {
            bookingsCollection.document(bookingId)
                .update("status", status.name)
                .await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}