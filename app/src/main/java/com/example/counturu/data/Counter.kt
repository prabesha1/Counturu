package com.example.counturu.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "counters")
data class Counter(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String,
    val targetDateTime: Long,
    val isFavorite: Boolean = false,
    val imageUri: String? = null,
    val hasReminder: Boolean = false,
    val createdAt: Long = System.currentTimeMillis(),
    val icon: String = "⏱️",
    val backgroundColor: Long? = null, // Store color as Long (ARGB)
    val notes: String? = null // Store notes as JSON string or pipe-separated
)

