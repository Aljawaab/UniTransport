package com.example.unitransport.data.repository

import com.example.unitransport.features.vehicles.model.Vehicle
import com.example.unitransport.features.vehicles.model.VehicleStatus
import com.example.unitransport.features.vehicles.model.VehicleType
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class VehicleRepository @Inject constructor() {

    private val firestore = FirebaseFirestore.getInstance()
    private val vehiclesCollection = firestore.collection("vehicles")

    // Real-time vehicle list from Firestore
    fun getVehicles(): Flow<List<Vehicle>> = callbackFlow {
        val listener = vehiclesCollection
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    trySend(emptyList())
                    return@addSnapshotListener
                }
                val vehicles = snapshot?.documents?.mapNotNull { doc ->
                    try {
                        Vehicle(
                            id = doc.id,
                            registrationNumber = doc.getString(
                                "registrationNumber"
                            ) ?: "",
                            make = doc.getString("make") ?: "",
                            model = doc.getString("model") ?: "",
                            type = VehicleType.valueOf(
                                doc.getString("type") ?: "BUS"
                            ),
                            capacity = (doc.getLong("capacity")
                                ?: 0L).toInt(),
                            status = VehicleStatus.valueOf(
                                doc.getString("status") ?: "AVAILABLE"
                            ),
                            description = doc.getString(
                                "description"
                            ) ?: "",
                            yearOfManufacture = (doc.getLong(
                                "yearOfManufacture"
                            ) ?: 2020L).toInt(),
                            fuelType = doc.getString("fuelType")
                                ?: "Diesel",
                            features = (doc.get("features")
                                    as? List<*>)
                                ?.filterIsInstance<String>()
                                ?: emptyList()
                        )
                    } catch (e: Exception) {
                        null
                    }
                } ?: emptyList()
                trySend(vehicles)
            }
        awaitClose { listener.remove() }
    }

    // Get single vehicle by ID
    suspend fun getVehicleById(vehicleId: String): Vehicle? {
        return try {
            val doc = vehiclesCollection
                .document(vehicleId)
                .get()
                .await()
            if (!doc.exists()) return null
            Vehicle(
                id = doc.id,
                registrationNumber = doc.getString(
                    "registrationNumber"
                ) ?: "",
                make = doc.getString("make") ?: "",
                model = doc.getString("model") ?: "",
                type = VehicleType.valueOf(
                    doc.getString("type") ?: "BUS"
                ),
                capacity = (doc.getLong("capacity") ?: 0L).toInt(),
                status = VehicleStatus.valueOf(
                    doc.getString("status") ?: "AVAILABLE"
                ),
                description = doc.getString("description") ?: "",
                yearOfManufacture = (doc.getLong(
                    "yearOfManufacture"
                ) ?: 2020L).toInt(),
                fuelType = doc.getString("fuelType") ?: "Diesel",
                features = (doc.get("features") as? List<*>)
                    ?.filterIsInstance<String>() ?: emptyList()
            )
        } catch (e: Exception) {
            null
        }
    }

    // Update vehicle status
    suspend fun updateVehicleStatus(
        vehicleId: String,
        status: VehicleStatus
    ): Result<Unit> {
        return try {
            vehiclesCollection
                .document(vehicleId)
                .update("status", status.name)
                .await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // Seed vehicles to Firestore (call once from Admin)
    suspend fun seedVehicles(
        vehicles: List<Vehicle>
    ): Result<Unit> {
        return try {
            val batch = firestore.batch()
            vehicles.forEach { vehicle ->
                val ref = vehiclesCollection.document()
                val data = hashMapOf(
                    "registrationNumber" to vehicle.registrationNumber,
                    "make" to vehicle.make,
                    "model" to vehicle.model,
                    "type" to vehicle.type.name,
                    "capacity" to vehicle.capacity,
                    "status" to vehicle.status.name,
                    "description" to vehicle.description,
                    "yearOfManufacture" to vehicle.yearOfManufacture,
                    "fuelType" to vehicle.fuelType,
                    "features" to vehicle.features
                )
                batch.set(ref, data)
            }
            batch.commit().await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getVehicleByRegistration(registration: String): Vehicle? {
        return try {
            val snapshot = vehiclesCollection
                .whereEqualTo("registrationNumber", registration)
                .limit(1)
                .get()
                .await()
            val doc = snapshot.documents.firstOrNull() ?: return null
            Vehicle(
                id = doc.id,
                registrationNumber = doc.getString("registrationNumber") ?: "",
                make = doc.getString("make") ?: "",
                model = doc.getString("model") ?: "",
                type = VehicleType.valueOf(doc.getString("type") ?: "BUS"),
                capacity = (doc.getLong("capacity") ?: 0L).toInt(),
                status = VehicleStatus.valueOf(doc.getString("status") ?: "AVAILABLE"),
                description = doc.getString("description") ?: "",
                yearOfManufacture = (doc.getLong("yearOfManufacture") ?: 2020L).toInt(),
                fuelType = doc.getString("fuelType") ?: "Diesel",
                features = (doc.get("features") as? List<*>)?.filterIsInstance<String>() ?: emptyList()
            )
        } catch (e: Exception) {
            null
        }
    }

    suspend fun addVehicle(vehicle: Vehicle): Result<Unit> {
        return try {
            val data = hashMapOf(
                "registrationNumber" to vehicle.registrationNumber,
                "make" to vehicle.make,
                "model" to vehicle.model,
                "type" to vehicle.type.name,
                "capacity" to vehicle.capacity,
                "status" to vehicle.status.name,
                "description" to vehicle.description,
                "yearOfManufacture" to vehicle.yearOfManufacture,
                "fuelType" to vehicle.fuelType,
                "features" to vehicle.features
            )
            vehiclesCollection.add(data).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun updateVehicle(vehicle: Vehicle): Result<Unit> {
        return try {
            val data = hashMapOf(
                "registrationNumber" to vehicle.registrationNumber,
                "make" to vehicle.make,
                "model" to vehicle.model,
                "type" to vehicle.type.name,
                "capacity" to vehicle.capacity,
                "status" to vehicle.status.name,
                "description" to vehicle.description,
                "yearOfManufacture" to vehicle.yearOfManufacture,
                "fuelType" to vehicle.fuelType,
                "features" to vehicle.features
            )
            vehiclesCollection.document(vehicle.id).update(data as Map<String, Any>).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun deleteVehicle(vehicleId: String): Result<Unit> {
        return try {
            vehiclesCollection.document(vehicleId).delete().await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // Checks if this vehicle is currently assigned to an ACTIVE booking
    suspend fun isVehicleInActiveTrip(registrationNumber: String): Boolean {
        return try {
            val snapshot = firestore.collection("bookings")
                .whereEqualTo("vehicleAssigned", registrationNumber)
                .whereEqualTo("status", "ACTIVE")
                .limit(1)
                .get()
                .await()
            !snapshot.isEmpty
        } catch (e: Exception) {
            false
        }
    }
}