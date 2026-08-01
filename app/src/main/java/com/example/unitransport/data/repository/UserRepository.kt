package com.example.unitransport.data.repository

import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

data class UserProfile(
    val uid: String = "",
    val fullName: String = "",
    val email: String = "",
    val department: String = "",
    val role: String = "",
    val phone: String = ""
)

@Singleton
class UserRepository @Inject constructor() {
    private val firestore = FirebaseFirestore.getInstance()
    private val usersCollection = firestore.collection("users")

    suspend fun getUserById(uid: String): UserProfile? {
        return try {
            val doc = usersCollection.document(uid).get().await()
            if (!doc.exists()) return null
            doc.toUserProfile()
        } catch (e: Exception) {
            null
        }
    }

    suspend fun getUsersByRole(role: String): List<UserProfile> {
        return try {
            usersCollection.whereEqualTo("role", role).get().await()
                .documents.map { it.toUserProfile() }
        } catch (e: Exception) {
            emptyList()
        }
    }

    suspend fun getAllUsers(): List<UserProfile> {
        return try {
            usersCollection.get().await().documents.map { it.toUserProfile() }
        } catch (e: Exception) {
            emptyList()
        }
    }

    private fun com.google.firebase.firestore.DocumentSnapshot.toUserProfile() = UserProfile(
        uid = id,
        fullName = getString("fullName") ?: "",
        email = getString("email") ?: "",
        department = getString("department") ?: "",
        role = getString("role") ?: "",
        phone = getString("phone") ?: ""
    )
}