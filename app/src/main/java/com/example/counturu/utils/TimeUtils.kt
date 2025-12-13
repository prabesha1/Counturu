package com.example.counturu.utils

data class TimeRemaining(
    val days: Long,
    val hours: Long,
    val minutes: Long,
    val seconds: Long,
    val isExpired: Boolean
)

fun calculateTimeRemaining(targetDateTime: Long): TimeRemaining {
    val now = System.currentTimeMillis()
    val diff = targetDateTime - now

    if (diff <= 0) {
        return TimeRemaining(0, 0, 0, 0, true)
    }

    val seconds = (diff / 1000) % 60
    val minutes = (diff / (1000 * 60)) % 60
    val hours = (diff / (1000 * 60 * 60)) % 24
    val days = diff / (1000 * 60 * 60 * 24)

    return TimeRemaining(days, hours, minutes, seconds, false)
}

