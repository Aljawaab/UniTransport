package com.example.unitransport.data.repository

import com.example.unitransport.features.auth.model.UserRole
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthRepository @Inject constructor() {

    private val auth = FirebaseAuth.getInstance()
    private val firestore = FirebaseFirestore.getInstance()

    // Current logged-in user ID
    val currentUserId: String?
        get() = auth.currentUser?.uid

    val isLoggedIn: Boolean
        get() = auth.currentUser != null

    // Login with email and password
    // Returns the user's role from Firestore
    suspend fun login(email: String, password: String): Result<UserRole> {
        return try {
            val result = auth.signInWithEmailAndPassword(
                email, password
            ).await()
            val uid = result.user?.uid
                ?: return Result.failure(
                    Exception("Login failed: no user ID")
                )
            val role = getUserRole(uid)
            Result.success(role)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // Register new user
    suspend fun register(
        fullName: String,
        email: String,
        password: String,
        department: String,
        role: UserRole,
        phone: String = ""
    ): Result<Unit> {
        return try {
            val result = auth.createUserWithEmailAndPassword(email, password).await()
            val uid = result.user?.uid
                ?: return Result.failure(Exception("Registration failed: no user ID"))

            val userDoc = hashMapOf(
                "uid" to uid,
                "fullName" to fullName,
                "email" to email,
                "phone" to phone,
                "department" to department,
                "role" to role.name,
                "isActive" to true,
                "createdAt" to System.currentTimeMillis(),
                "totalBookings" to 0
            )
            firestore.collection("users").document(uid).set(userDoc).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun updateProfile(
        fullName: String,
        phone: String,
        department: String
    ): Result<Unit> {
        val uid = currentUserId ?: return Result.failure(Exception("Not logged in"))
        return try {
            val updates = hashMapOf<String, Any>(
                "fullName" to fullName,
                "phone" to phone,
                "department" to department
            )
            firestore.collection("users").document(uid).update(updates).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun changePassword(
        currentPassword: String,
        newPassword: String
    ): Result<Unit> {
        val user = auth.currentUser
            ?: return Result.failure(Exception("Not logged in"))
        val email = user.email
            ?: return Result.failure(Exception("No email on account"))
        return try {
            val credential = com.google.firebase.auth.EmailAuthProvider
                .getCredential(email, currentPassword)
            user.reauthenticate(credential).await()
            user.updatePassword(newPassword).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // Get user role from Firestore
    suspend fun getUserRole(uid: String): UserRole {
        return try {
            val doc = firestore.collection("users")
                .document(uid)
                .get()
                .await()
            val roleString = doc.getString("role") ?: "STUDENT"
            UserRole.valueOf(roleString)
        } catch (e: Exception) {
            UserRole.STUDENT
        }
    }

    // Get current user profile data
    suspend fun getCurrentUserProfile(): Map<String, Any?> {
        val uid = currentUserId ?: return emptyMap()
        return try {
            val doc = firestore.collection("users")
                .document(uid)
                .get()
                .await()
            doc.data ?: emptyMap()
        } catch (e: Exception) {
            emptyMap()
        }
    }

    fun logout() {
        auth.signOut()
    }
}