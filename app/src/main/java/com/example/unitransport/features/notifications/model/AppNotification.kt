package com.example.unitransport.features.notifications.model

data class AppNotification(
    val id: String,
    val title: String,
    val message: String,
    val time: String,
    val type: NotificationType,
    val isRead: Boolean = false,
    val relatedId: String? = null,
    val timestamp: Long = 0L
)

enum class NotificationType {
    BOOKING_APPROVED,
    BOOKING_REJECTED,
    BOOKING_PENDING,
    TRIP_REMINDER,
    TRIP_STARTED,
    TRIP_COMPLETED,
    SYSTEM,
    GENERAL
}

val mockNotifications = listOf(
    AppNotification(
        id = "N001",
        title = "Booking Approved ✓",
        message = "Your booking BK001 to Engineering Block " +
                "has been approved. Vehicle KDA 123A assigned " +
                "with driver John Kamau.",
        time = "2 hours ago",
        type = NotificationType.BOOKING_APPROVED,
        isRead = false,
        relatedId = "BK001"
    ),
    AppNotification(
        id = "N002",
        title = "Trip Reminder",
        message = "Your trip to Engineering Block is scheduled " +
                "for today at 2:00 PM. Please be at the pickup " +
                "point 10 minutes early.",
        time = "3 hours ago",
        type = NotificationType.TRIP_REMINDER,
        isRead = false,
        relatedId = "BK001"
    ),
    AppNotification(
        id = "N003",
        title = "New Booking Received",
        message = "Your booking request BK002 to Main Campus " +
                "Library has been received and is pending review.",
        time = "5 hours ago",
        type = NotificationType.BOOKING_PENDING,
        isRead = true,
        relatedId = "BK002"
    ),
    AppNotification(
        id = "N004",
        title = "Booking Rejected",
        message = "Your booking BK004 to Off-campus Conference " +
                "Center has been rejected. Reason: Insufficient " +
                "notice period. Please re-submit with at least " +
                "48 hours notice.",
        time = "Yesterday",
        type = NotificationType.BOOKING_REJECTED,
        isRead = true,
        relatedId = "BK004"
    ),
    AppNotification(
        id = "N005",
        title = "Trip Completed",
        message = "Your trip to Administration Block has been " +
                "marked as completed. Thank you for using " +
                "UniTransport.",
        time = "2 days ago",
        type = NotificationType.TRIP_COMPLETED,
        isRead = true,
        relatedId = "BK005"
    ),
    AppNotification(
        id = "N006",
        title = "System Maintenance",
        message = "UniTransport will undergo scheduled maintenance " +
                "on Sunday 29th June from 12:00 AM to 4:00 AM. " +
                "Bookings made during this period will be " +
                "processed afterwards.",
        time = "3 days ago",
        type = NotificationType.SYSTEM,
        isRead = true
    ),
    AppNotification(
        id = "N007",
        title = "Booking Approved ✓",
        message = "Your booking BK003 to Sports Complex has been " +
                "approved. Vehicle KDB 456B assigned with " +
                "driver Peter Mwenda.",
        time = "3 days ago",
        type = NotificationType.BOOKING_APPROVED,
        isRead = true,
        relatedId = "BK003"
    )
)

fun formatRelativeTime(timestampMillis: Long): String {
    if (timestampMillis <= 0L) return ""
    val diff = System.currentTimeMillis() - timestampMillis
    val minutes = diff / 60000
    val hours = diff / 3600000
    val days = diff / 86400000
    return when {
        minutes < 1 -> "Just now"
        minutes < 60 -> "$minutes minute${if (minutes == 1L) "" else "s"} ago"
        hours < 24 -> "$hours hour${if (hours == 1L) "" else "s"} ago"
        days == 1L -> "Yesterday"
        else -> "$days days ago"
    }
}