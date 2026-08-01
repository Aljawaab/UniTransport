package com.example.unitransport.data.repository

import com.example.unitransport.features.driver.model.IssueCategory
import com.example.unitransport.features.driver.model.IssueSeverity
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class IssueReportRepository @Inject constructor() {
    private val firestore = FirebaseFirestore.getInstance()
    private val issuesCollection = firestore.collection("issueReports")

    suspend fun submitIssue(
        tripId: String,
        driverId: String,
        category: IssueCategory,
        description: String,
        severity: IssueSeverity
    ): Result<Unit> {
        return try {
            val data = hashMapOf(
                "tripId" to tripId,
                "driverId" to driverId,
                "category" to category.name,
                "description" to description,
                "severity" to severity.name,
                "reportedAt" to System.currentTimeMillis()
            )
            issuesCollection.add(data).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}