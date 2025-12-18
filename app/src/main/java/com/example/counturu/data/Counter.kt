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
    val notes: String? = null, // Store notes as JSON string or pipe-separated
    // New fields for categories and goals
    val category: String? = null, // Category tag (e.g., "Birthday", "Holiday", "Work")
    val goalTarget: Int? = null, // Daily/weekly goal target
    val goalType: String? = null, // "daily" or "weekly"
    val currentProgress: Int = 0, // Current progress towards goal
    val isArchived: Boolean = false, // For swipe-to-archive
    val history: String? = null // JSON string for tracking increment/decrement over time
)

// Counter History Entry for analytics
data class CounterHistoryEntry(
    val timestamp: Long,
    val action: String, // "increment", "decrement", "reset"
    val value: Int
)

// Counter category enum
enum class CounterCategory(val displayName: String, val icon: String) {
    BIRTHDAY("Birthday", "🎂"),
    HOLIDAY("Holiday", "🎄"),
    WORK("Work", "💼"),
    PERSONAL("Personal", "🏠"),
    TRAVEL("Travel", "✈️"),
    FITNESS("Fitness", "💪"),
    EDUCATION("Education", "📚"),
    EVENT("Event", "🎉"),
    ANNIVERSARY("Anniversary", "💑"),
    OTHER("Other", "📌")
}

// Counter templates
data class CounterTemplate(
    val name: String,
    val icon: String,
    val category: CounterCategory,
    val description: String,
    val suggestedGoal: Int? = null
)

object CounterTemplates {
    val templates = listOf(
        CounterTemplate("New Year", "🎆", CounterCategory.HOLIDAY, "Countdown to New Year celebration"),
        CounterTemplate("Birthday", "🎂", CounterCategory.BIRTHDAY, "Birthday countdown"),
        CounterTemplate("Vacation", "🏖️", CounterCategory.TRAVEL, "Days until vacation"),
        CounterTemplate("Wedding", "💒", CounterCategory.ANNIVERSARY, "Wedding countdown"),
        CounterTemplate("Exam", "📝", CounterCategory.EDUCATION, "Days until exam"),
        CounterTemplate("Project Deadline", "📊", CounterCategory.WORK, "Work project deadline"),
        CounterTemplate("Water Intake", "💧", CounterCategory.FITNESS, "Daily water intake tracker", 8),
        CounterTemplate("Steps Goal", "👟", CounterCategory.FITNESS, "Daily steps goal", 10000),
        CounterTemplate("Reading", "📖", CounterCategory.EDUCATION, "Pages to read", 30),
        CounterTemplate("Meditation", "🧘", CounterCategory.PERSONAL, "Daily meditation minutes", 10)
    )
}

