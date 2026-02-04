package com.pratipsarkar.kitchentimer

// Remaining time is calculated, not stored
data class TimerState(
    val durationMillis: Long,
    val startTimeMillis: Long?,
)