package com.example.taskscheduler.data

import java.util.Date

data class SessionMonitoring(
    var sessionCount: Int,
    var totalTimeMillisSpent: Long,
    val usageDates: List<Date> // List of dates when the session was started
) {
}