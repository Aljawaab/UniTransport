package com.example.unitransport.data.repository

import com.example.unitransport.features.notifications.model.AppNotification
import com.example.unitransport.features.notifications.model.NotificationType
import com.example.unitransport.features.notifications.model.formatRelativeTime
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NotificationRepository @Inject constructor() {
    private val firestore = FirebaseFirestore.getInstance()
    private val notificationsCollection = firestore.collection("notifications")

    suspend fun addNotification(
        userId: String,
        title: String,
        message: String,
        type: NotificationType,
        relatedId: String? = null
    ): Result<Unit> {
        return try {
            val data = hashMapOf(
                "userId" to userId,
                "title" to title,
                "message" to message,
                "type" to type.name,
                "relatedId" to relatedId,
                "isRead" to false,
                "timestamp" to System.currentTimeMillis()
            )
            notificationsCollection.add(data).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    fun getNotificationsForUser(userId: String): Flow<List<AppNotification>> = callbackFlow {
        val listener = notificationsCollection
            .whereEqualTo("userId", userId)
            .orderBy("timestamp", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    trySend(emptyList())
                    return@addSnapshotListener
                }
                val notifications = snapshot?.documents?.mapNotNull { doc ->
                    try {
                        val ts = doc.getLong("timestamp") ?: 0L
                        AppNotification(
                            id = doc.id,
                            title = doc.getString("title") ?: "",
                            message = doc.getString("message") ?: "",
                            time = formatRelativeTime(ts),
                            type = NotificationType.valueOf(
                                doc.getString("type") ?: "GENERAL"
                            ),
                            isRead = doc.getBoolean("isRead") ?: false,
                            relatedId = doc.getString("relatedId"),
                            timestamp = ts
                        )
                    } catch (e: Exception) {
                        null
                    }
                } ?: emptyList()
                trySend(notifications)
            }
        awaitClose { listener.remove() }
    }

    suspend fun markAsRead(notificationId: String): Result<Unit> {
        return try {
            notificationsCollection.document(notificationId)
                .update("isRead", true).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun markAllAsRead(userId: String): Result<Unit> {
        return try {
            val snapshot = notificationsCollection
                .whereEqualTo("userId", userId)
                .whereEqualTo("isRead", false)
                .get()
                .await()
            val batch = firestore.batch()
            snapshot.documents.forEach { batch.update(it.reference, "isRead", true) }
            batch.commit().await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}