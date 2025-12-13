package com.example.counturu.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// Predefined icons grouped by category
val iconCategories = mapOf(
    "Events" to listOf("🎉", "🎂", "🎄", "🎃", "🎁", "🎊", "🪅", "🎈", "🎆", "🎇"),
    "Time" to listOf("⏱️", "⏰", "🕐", "📅", "📆", "⌛", "⏳", "🗓️"),
    "Travel" to listOf("✈️", "🚗", "🚀", "🏖️", "🏔️", "🌍", "🗺️", "🧳", "⛱️", "🏕️"),
    "Health" to listOf("💪", "🏃", "🧘", "💊", "🏥", "❤️", "🩺", "🧠", "🦷", "👁️"),
    "Work" to listOf("💼", "📊", "💻", "📝", "📚", "🎓", "📈", "💰", "🏢", "📧"),
    "Love" to listOf("❤️", "💕", "💖", "💗", "💘", "💝", "💑", "👫", "💒", "💍"),
    "Sports" to listOf("⚽", "🏀", "🎾", "🏈", "⚾", "🎯", "🏆", "🥇", "🎮", "🎲"),
    "Nature" to listOf("🌸", "🌺", "🌻", "🌹", "🌴", "🌊", "☀️", "🌙", "⭐", "🌈"),
    "Food" to listOf("🍕", "🍔", "🍰", "🎂", "🍿", "☕", "🍷", "🍾", "🥂", "🍩"),
    "Animals" to listOf("🐶", "🐱", "🦁", "🐼", "🦋", "🐦", "🦄", "🐬", "🦅", "🐢")
)

// Predefined background colors
val backgroundColors = listOf(
    0xFF6366F1, // Indigo
    0xFF8B5CF6, // Purple
    0xFFEC4899, // Pink
    0xFFEF4444, // Red
    0xFFF97316, // Orange
    0xFFF59E0B, // Amber
    0xFF84CC16, // Lime
    0xFF22C55E, // Green
    0xFF14B8A6, // Teal
    0xFF06B6D4, // Cyan
    0xFF3B82F6, // Blue
    0xFF1E293B, // Slate Dark
    0xFF374151, // Gray Dark
    0xFF78716C, // Stone
    0xFF92400E, // Brown
    0xFF7C3AED  // Violet
)

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun IconPickerDialog(
    selectedIcon: String,
    onIconSelected: (String) -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "Choose Icon",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                iconCategories.forEach { (category, icons) ->
                    Text(
                        text = category,
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.primary
                    )
                    FlowRow(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        icons.forEach { icon ->
                            IconItem(
                                icon = icon,
                                isSelected = icon == selectedIcon,
                                onClick = { onIconSelected(icon) }
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Done")
            }
        },
        shape = RoundedCornerShape(24.dp)
    )
}

@Composable
private fun IconItem(
    icon: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .size(48.dp)
            .clip(CircleShape)
            .background(
                if (isSelected) MaterialTheme.colorScheme.primaryContainer
                else MaterialTheme.colorScheme.surfaceVariant
            )
            .then(
                if (isSelected) Modifier.border(
                    2.dp,
                    MaterialTheme.colorScheme.primary,
                    CircleShape
                ) else Modifier
            )
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = icon,
            fontSize = 24.sp
        )
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ColorPickerDialog(
    selectedColor: Long?,
    onColorSelected: (Long?) -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "Choose Background Color",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    text = "Select a color or use default",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                FlowRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Default option (no color)
                    ColorItem(
                        color = null,
                        isSelected = selectedColor == null,
                        onClick = { onColorSelected(null) }
                    )

                    backgroundColors.forEach { color ->
                        ColorItem(
                            color = color,
                            isSelected = selectedColor == color,
                            onClick = { onColorSelected(color) }
                        )
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Done")
            }
        },
        shape = RoundedCornerShape(24.dp)
    )
}

@Composable
private fun ColorItem(
    color: Long?,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .size(48.dp)
            .clip(CircleShape)
            .background(
                if (color != null) Color(color)
                else MaterialTheme.colorScheme.surfaceVariant
            )
            .then(
                if (isSelected) Modifier.border(
                    3.dp,
                    MaterialTheme.colorScheme.onSurface,
                    CircleShape
                ) else Modifier.border(
                    1.dp,
                    MaterialTheme.colorScheme.outline.copy(alpha = 0.3f),
                    CircleShape
                )
            )
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        if (color == null) {
            Text(
                text = "Auto",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

