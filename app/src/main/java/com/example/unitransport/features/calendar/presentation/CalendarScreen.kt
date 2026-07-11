package com.example.unitransport.features.calendar.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIos
import androidx.compose.material.icons.filled.ArrowForwardIos
import androidx.compose.material.icons.filled.Circle
import androidx.compose.material.icons.filled.DirectionsBus
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.unitransport.core.ui.theme.StatusApproved
import com.example.unitransport.core.ui.theme.StatusMaintenance
import com.example.unitransport.core.ui.theme.StatusPending
import com.example.unitransport.core.ui.theme.StatusRejected

// Data models for calendar
data class CalendarDay(
    val day: Int,
    val isCurrentMonth: Boolean = true,
    val events: List<CalendarEvent> = emptyList()
)

data class CalendarEvent(
    val id: String,
    val title: String,
    val vehicleReg: String,
    val time: String,
    val type: CalendarEventType
)

enum class CalendarEventType {
    BOOKING, MAINTENANCE, AVAILABLE, RESERVED
}

// Mock calendar data
private fun getMockEventsForDay(day: Int, month: Int): List<CalendarEvent> {
    return when {
        day == 27 && month == 6 -> listOf(
            CalendarEvent(
                "E1", "Engineering Block Trip",
                "KDA 123A", "08:00 AM",
                CalendarEventType.BOOKING
            ),
            CalendarEvent(
                "E2", "Sports Complex",
                "KDB 456B", "03:30 PM",
                CalendarEventType.BOOKING
            )
        )
        day == 28 && month == 6 -> listOf(
            CalendarEvent(
                "E3", "Library Research",
                "KDC 789C", "09:00 AM",
                CalendarEventType.BOOKING
            ),
            CalendarEvent(
                "E4", "Scheduled Maintenance",
                "KDE 345E", "All Day",
                CalendarEventType.MAINTENANCE
            )
        )
        day == 29 && month == 6 -> listOf(
            CalendarEvent(
                "E5", "Drama Club Dinner",
                "KDB 456B", "06:00 PM",
                CalendarEventType.BOOKING
            )
        )
        day == 30 && month == 6 -> listOf(
            CalendarEvent(
                "E6", "Debate at KU",
                "KDA 123A", "07:00 AM",
                CalendarEventType.BOOKING
            ),
            CalendarEvent(
                "E7", "Staff Outing",
                "KDF 678F", "10:00 AM",
                CalendarEventType.RESERVED
            )
        )
        day == 1 && month == 7 -> listOf(
            CalendarEvent(
                "E8", "Field Trip",
                "KDD 012D", "08:30 AM",
                CalendarEventType.BOOKING
            )
        )
        day == 5 && month == 7 -> listOf(
            CalendarEvent(
                "E9", "Routine Service",
                "KDG 901G", "All Day",
                CalendarEventType.MAINTENANCE
            )
        )
        else -> emptyList()
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CalendarScreen() {
    // State
    var currentMonth by remember { mutableStateOf(6) } // June = 6
    var currentYear by remember { mutableStateOf(2026) }
    var selectedDay by remember { mutableStateOf<CalendarDay?>(null) }
    var showBottomSheet by remember { mutableStateOf(false) }
    val bottomSheetState = rememberModalBottomSheetState()

    val monthNames = listOf(
        "January", "February", "March", "April",
        "May", "June", "July", "August",
        "September", "October", "November", "December"
    )

    val dayNames = listOf("Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat")

    // Build calendar days for current month
    val calendarDays = buildCalendarDays(currentMonth, currentYear)

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Vehicle Calendar",
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary
                )
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { paddingValues ->

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentPadding = PaddingValues(bottom = 32.dp)
        ) {
            // Month navigation header
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    shape = MaterialTheme.shapes.large,
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    ),
                    elevation = CardDefaults.cardElevation(2.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        // Month/Year navigation
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            IconButton(
                                onClick = {
                                    if (currentMonth == 1) {
                                        currentMonth = 12
                                        currentYear--
                                    } else {
                                        currentMonth--
                                    }
                                }
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.ArrowBackIos,
                                    contentDescription = "Previous month",
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(18.dp)
                                )
                            }

                            Text(
                                text = "${monthNames[currentMonth - 1]} $currentYear",
                                style = MaterialTheme.typography.titleMedium,
                                color = MaterialTheme.colorScheme.onSurface
                            )

                            IconButton(
                                onClick = {
                                    if (currentMonth == 12) {
                                        currentMonth = 1
                                        currentYear++
                                    } else {
                                        currentMonth++
                                    }
                                }
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.ArrowForwardIos,
                                    contentDescription = "Next month",
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // Day name headers
                        Row(modifier = Modifier.fillMaxWidth()) {
                            dayNames.forEach { dayName ->
                                Text(
                                    text = dayName,
                                    modifier = Modifier.weight(1f),
                                    textAlign = TextAlign.Center,
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        // Calendar grid
                        LazyVerticalGrid(
                            columns = GridCells.Fixed(7),
                            modifier = Modifier.height(280.dp),
                            userScrollEnabled = false
                        ) {
                            items(calendarDays) { calendarDay ->
                                CalendarDayCell(
                                    calendarDay = calendarDay,
                                    isSelected = selectedDay?.day ==
                                            calendarDay.day &&
                                            calendarDay.isCurrentMonth,
                                    onClick = {
                                        if (calendarDay.isCurrentMonth) {
                                            selectedDay = calendarDay
                                            if (calendarDay.events.isNotEmpty()) {
                                                showBottomSheet = true
                                            }
                                        }
                                    }
                                )
                            }
                        }
                    }
                }
            }

            // Legend
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    shape = MaterialTheme.shapes.large,
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    ),
                    elevation = CardDefaults.cardElevation(2.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "Legend",
                            style = MaterialTheme.typography.titleSmall,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceEvenly
                        ) {
                            LegendItem(
                                color = StatusApproved,
                                label = "Booked"
                            )
                            LegendItem(
                                color = StatusMaintenance,
                                label = "Maintenance"
                            )
                            LegendItem(
                                color = StatusPending,
                                label = "Reserved"
                            )
                            LegendItem(
                                color = MaterialTheme.colorScheme.primary,
                                label = "Available"
                            )
                        }
                    }
                }
            }

            // Upcoming bookings this month
            item {
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "Upcoming This Month",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onBackground,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
                Spacer(modifier = Modifier.height(8.dp))
            }

            // Show all events for the month
            val monthEvents = calendarDays
                .filter { it.isCurrentMonth && it.events.isNotEmpty() }
                .sortedBy { it.day }

            if (monthEvents.isEmpty()) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(32.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "No bookings this month",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            } else {
                items(monthEvents) { day ->
                    day.events.forEach { event ->
                        CalendarEventCard(
                            day = day.day,
                            month = currentMonth,
                            event = event
                        )
                    }
                }
            }
        }

        // Bottom sheet for day details
        if (showBottomSheet) {
            ModalBottomSheet(
                onDismissRequest = { showBottomSheet = false },
                sheetState = bottomSheetState,
                shape = MaterialTheme.shapes.extraLarge
            ) {
                selectedDay?.let { day ->
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(
                                start = 16.dp,
                                end = 16.dp,
                                bottom = 32.dp
                            )
                    ) {
                        Text(
                            text = "${monthNames[currentMonth - 1]}" +
                                    " ${day.day}, $currentYear",
                            style = MaterialTheme.typography.titleLarge,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "${day.events.size} " +
                                    if (day.events.size == 1)
                                        "booking" else "bookings",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(16.dp))

                        day.events.forEach { event ->
                            BottomSheetEventItem(event = event)
                            Spacer(modifier = Modifier.height(8.dp))
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun CalendarDayCell(
    calendarDay: CalendarDay,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val hasEvents = calendarDay.events.isNotEmpty()
    val eventColor = when {
        !calendarDay.isCurrentMonth -> Color.Transparent
        calendarDay.events.any {
            it.type == CalendarEventType.MAINTENANCE
        } -> StatusMaintenance
        calendarDay.events.any {
            it.type == CalendarEventType.BOOKING
        } -> StatusApproved
        calendarDay.events.any {
            it.type == CalendarEventType.RESERVED
        } -> StatusPending
        else -> Color.Transparent
    }

    Column(
        modifier = Modifier
            .padding(2.dp)
            .clip(MaterialTheme.shapes.small)
            .background(
                if (isSelected)
                    MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)
                else
                    Color.Transparent
            )
            .border(
                width = if (isSelected) 1.dp else 0.dp,
                color = if (isSelected)
                    MaterialTheme.colorScheme.primary
                else
                    Color.Transparent,
                shape = MaterialTheme.shapes.small
            )
            .clickable(enabled = calendarDay.isCurrentMonth) { onClick() }
            .padding(4.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = if (calendarDay.day > 0) "${calendarDay.day}" else "",
            style = MaterialTheme.typography.bodySmall,
            color = when {
                !calendarDay.isCurrentMonth ->
                    MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f)
                isSelected ->
                    MaterialTheme.colorScheme.primary
                else ->
                    MaterialTheme.colorScheme.onSurface
            },
            textAlign = TextAlign.Center
        )

        // Event dot indicator
        if (hasEvents && calendarDay.isCurrentMonth) {
            Spacer(modifier = Modifier.height(2.dp))
            Box(
                modifier = Modifier
                    .size(6.dp)
                    .clip(CircleShape)
                    .background(eventColor)
            )
        } else {
            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}

@Composable
private fun LegendItem(
    color: Color,
    label: String
) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
            modifier = Modifier
                .size(10.dp)
                .clip(CircleShape)
                .background(color)
        )
        Spacer(modifier = Modifier.width(4.dp))
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun CalendarEventCard(
    day: Int,
    month: Int,
    event: CalendarEvent
) {
    val eventColor = when (event.type) {
        CalendarEventType.BOOKING -> StatusApproved
        CalendarEventType.MAINTENANCE -> StatusMaintenance
        CalendarEventType.RESERVED -> StatusPending
        CalendarEventType.AVAILABLE -> MaterialTheme.colorScheme.primary
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp),
        shape = MaterialTheme.shapes.large,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Date box
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(MaterialTheme.shapes.medium)
                    .background(
                        MaterialTheme.colorScheme.primaryContainer
                    ),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "$day",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = when (month) {
                            6 -> "JUN"
                            7 -> "JUL"
                            else -> "---"
                        },
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = event.title,
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = event.vehicleReg,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = event.time,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Box(
                modifier = Modifier
                    .clip(MaterialTheme.shapes.extraSmall)
                    .background(eventColor.copy(alpha = 0.12f))
                    .padding(horizontal = 8.dp, vertical = 4.dp)
            ) {
                Text(
                    text = when (event.type) {
                        CalendarEventType.BOOKING -> "Booked"
                        CalendarEventType.MAINTENANCE -> "Maintenance"
                        CalendarEventType.RESERVED -> "Reserved"
                        CalendarEventType.AVAILABLE -> "Available"
                    },
                    style = MaterialTheme.typography.labelSmall,
                    color = eventColor
                )
            }
        }
    }
}

@Composable
private fun BottomSheetEventItem(event: CalendarEvent) {
    val eventColor = when (event.type) {
        CalendarEventType.BOOKING -> StatusApproved
        CalendarEventType.MAINTENANCE -> StatusMaintenance
        CalendarEventType.RESERVED -> StatusPending
        CalendarEventType.AVAILABLE -> MaterialTheme.colorScheme.primary
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.large,
        colors = CardDefaults.cardColors(
            containerColor = eventColor.copy(alpha = 0.08f)
        ),
        elevation = CardDefaults.cardElevation(0.dp)
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Filled.DirectionsBus,
                contentDescription = null,
                tint = eventColor,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = event.title,
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = "${event.vehicleReg} • ${event.time}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Box(
                modifier = Modifier
                    .clip(MaterialTheme.shapes.extraSmall)
                    .background(eventColor.copy(alpha = 0.15f))
                    .padding(horizontal = 6.dp, vertical = 2.dp)
            ) {
                Text(
                    text = when (event.type) {
                        CalendarEventType.BOOKING -> "Booked"
                        CalendarEventType.MAINTENANCE -> "Maintenance"
                        CalendarEventType.RESERVED -> "Reserved"
                        CalendarEventType.AVAILABLE -> "Available"
                    },
                    style = MaterialTheme.typography.labelSmall,
                    color = eventColor
                )
            }
        }
    }
}

// Builds the calendar day grid including padding days
private fun buildCalendarDays(month: Int, year: Int): List<CalendarDay> {
    val days = mutableListOf<CalendarDay>()

    // Days in current month
    val daysInMonth = when (month) {
        1, 3, 5, 7, 8, 10, 12 -> 31
        4, 6, 9, 11 -> 30
        2 -> if (year % 4 == 0) 29 else 28
        else -> 30
    }

    // Day of week the month starts on (0=Sun, 1=Mon, etc)
    // Using Zeller's formula simplified
    val m = if (month < 3) month + 12 else month
    val y = if (month < 3) year - 1 else year
    val k = y % 100
    val j = y / 100
    val firstDay = ((1 + (13 * (m + 1)) / 5 + k +
            k / 4 + j / 4 - 2 * j) % 7 + 7) % 7

    // Pad start with empty days
    repeat(firstDay) {
        days.add(CalendarDay(day = 0, isCurrentMonth = false))
    }

    // Add actual days with mock events
    for (day in 1..daysInMonth) {
        days.add(
            CalendarDay(
                day = day,
                isCurrentMonth = true,
                events = getMockEventsForDay(day, month)
            )
        )
    }

    // Pad end to complete last row
    val remaining = (7 - (days.size % 7)) % 7
    repeat(remaining) {
        days.add(CalendarDay(day = 0, isCurrentMonth = false))
    }

    return days
}