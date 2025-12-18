package com.example.counturu.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.counturu.data.Counter
import com.example.counturu.ui.components.ColorPickerDialog
import com.example.counturu.ui.components.IconPickerDialog
import com.example.counturu.utils.TimeRemaining
import com.example.counturu.utils.calculateTimeRemaining
import kotlinx.coroutines.delay
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun CounterDetailScreen(
    counter: Counter,
    onBack: () -> Unit,
    onEdit: () -> Unit,
    onAddRemark: (String) -> Unit,
    onUpdateCounter: (Counter) -> Unit = {}
) {
    var timeRemaining by remember { mutableStateOf(calculateTimeRemaining(counter.targetDateTime)) }
    var showRemarkDialog by remember { mutableStateOf(false) }
    var remarkText by remember { mutableStateOf("") }
    var showIconPicker by remember { mutableStateOf(false) }
    var showColorPicker by remember { mutableStateOf(false) }

    // Load notes from counter (stored as pipe-separated string)
    var remarks by remember(counter.notes) {
        mutableStateOf(
            counter.notes?.split("|||")?.filter { it.isNotBlank() } ?: emptyList()
        )
    }

    // Function to save notes
    fun saveNotes(notesList: List<String>) {
        val notesString = if (notesList.isEmpty()) null else notesList.joinToString("|||")
        onUpdateCounter(counter.copy(notes = notesString))
    }

    // Determine background color
    val backgroundColor = if (counter.backgroundColor != null) {
        Color(counter.backgroundColor)
    } else {
        MaterialTheme.colorScheme.primary
    }

    val gradientBackground = Brush.verticalGradient(
        colors = listOf(
            backgroundColor.copy(alpha = 0.4f),
            backgroundColor.copy(alpha = 0.2f),
            MaterialTheme.colorScheme.background
        )
    )

    LaunchedEffect(counter.targetDateTime) {
        while (true) {
            timeRemaining = calculateTimeRemaining(counter.targetDateTime)
            delay(1000)
        }
    }

    // Get navigation bar padding for proper insets handling
    val navigationBarPadding = WindowInsets.navigationBars.asPaddingValues()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(gradientBackground)
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding(),
            horizontalAlignment = Alignment.CenterHorizontally,
            contentPadding = PaddingValues(
                bottom = navigationBarPadding.calculateBottomPadding() + 24.dp
            )
        ) {
            // Top Bar
            item {
                DetailTopBar(
                    onBack = onBack,
                    onEdit = onEdit
                )
            }

            // Hero Section - Icon and Title
            item {
                HeroSection(
                    counter = counter,
                    backgroundColor = backgroundColor,
                    onIconClick = { showIconPicker = true },
                    onColorClick = { showColorPicker = true }
                )
            }

            // Main Countdown Card - Larger Numbers
            item {
                MainCountdownCard(
                    timeRemaining = timeRemaining,
                    accentColor = backgroundColor,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
            }

            // Progress Card
            item {
                Spacer(modifier = Modifier.height(16.dp))
                ProgressCard(
                    counter = counter,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
            }

            // Quick Stats
            item {
                Spacer(modifier = Modifier.height(16.dp))
                QuickStatsSection(
                    counter = counter,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
            }

            // Notes Section Header
            item {
                Spacer(modifier = Modifier.height(20.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "📝 Notes",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                    IconButton(onClick = { showRemarkDialog = true }) {
                        Icon(
                            imageVector = Icons.Outlined.Add,
                            contentDescription = "Add note",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }

            // Notes
            if (remarks.isNotEmpty()) {
                itemsIndexed(remarks) { index, remark ->
                    NoteCard(
                        remark = remark,
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp),
                        onDelete = {
                            val newRemarks = remarks.toMutableList().apply { removeAt(index) }
                            remarks = newRemarks
                            saveNotes(newRemarks)
                        }
                    )
                }
            } else {
                item {
                    EmptyNotesCard(
                        onAddNote = { showRemarkDialog = true },
                        modifier = Modifier.padding(horizontal = 16.dp)
                    )
                }
            }

            // Bottom spacing
            item {
                Spacer(modifier = Modifier.height(32.dp))
            }
        }
    }

    // Dialogs
    if (showRemarkDialog) {
        AddNoteDialog(
            remarkText = remarkText,
            onTextChange = { remarkText = it },
            onDismiss = {
                remarkText = ""
                showRemarkDialog = false
            },
            onConfirm = {
                if (remarkText.isNotBlank()) {
                    val newRemarks = remarks + remarkText.trim()
                    remarks = newRemarks
                    saveNotes(newRemarks)
                    onAddRemark(remarkText.trim())
                    remarkText = ""
                    showRemarkDialog = false
                }
            }
        )
    }

    if (showIconPicker) {
        IconPickerDialog(
            selectedIcon = counter.icon,
            onIconSelected = { newIcon ->
                onUpdateCounter(counter.copy(icon = newIcon))
            },
            onDismiss = { showIconPicker = false }
        )
    }

    if (showColorPicker) {
        ColorPickerDialog(
            selectedColor = counter.backgroundColor,
            onColorSelected = { newColor ->
                onUpdateCounter(counter.copy(backgroundColor = newColor))
            },
            onDismiss = { showColorPicker = false }
        )
    }
}

@Composable
private fun DetailTopBar(
    onBack: () -> Unit,
    onEdit: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(
            onClick = onBack,
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.8f))
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = "Back"
            )
        }

        Row {
            IconButton(
                onClick = { /* Share functionality */ },
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.8f))
            ) {
                Icon(
                    imageVector = Icons.Filled.Share,
                    contentDescription = "Share"
                )
            }
            Spacer(modifier = Modifier.width(8.dp))
            IconButton(
                onClick = onEdit,
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.8f))
            ) {
                Icon(
                    imageVector = Icons.Filled.Edit,
                    contentDescription = "Edit"
                )
            }
        }
    }
}

@Composable
private fun HeroSection(
    counter: Counter,
    backgroundColor: Color,
    onIconClick: () -> Unit,
    onColorClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Event Icon - Clickable to change
        Box(
            modifier = Modifier
                .size(140.dp)
                .clip(CircleShape)
                .background(backgroundColor.copy(alpha = 0.25f))
                .clickable { onIconClick() },
            contentAlignment = Alignment.Center
        ) {
            if (counter.imageUri != null) {
                AsyncImage(
                    model = counter.imageUri,
                    contentDescription = counter.title,
                    modifier = Modifier
                        .size(140.dp)
                        .clip(CircleShape),
                    contentScale = ContentScale.Crop
                )
            } else {
                Text(
                    text = counter.icon,
                    fontSize = 72.sp
                )
            }
        }

        // Tap to change hint
        Text(
            text = "Tap to change icon",
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
            modifier = Modifier.padding(top = 8.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Event Title
        Text(
            text = counter.title,
            style = MaterialTheme.typography.displaySmall,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = 24.dp)
        )

        // Category tag
        counter.category?.let { category ->
            Spacer(modifier = Modifier.height(8.dp))
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(
                    containerColor = backgroundColor.copy(alpha = 0.2f)
                )
            ) {
                Text(
                    text = category,
                    style = MaterialTheme.typography.labelMedium,
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
        }

        // Favorite indicator
        if (counter.isFavorite) {
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Icon(
                    imageVector = Icons.Filled.Favorite,
                    contentDescription = "Favorite",
                    tint = MaterialTheme.colorScheme.error,
                    modifier = Modifier.size(16.dp)
                )
                Text(
                    text = "Favorite",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.error
                )
            }
        }

        // Target Date
        Spacer(modifier = Modifier.height(12.dp))
        val dateFormatter = remember { SimpleDateFormat("EEEE, MMM dd, yyyy • hh:mm a", Locale.getDefault()) }
        Text(
            text = dateFormatter.format(Date(counter.targetDateTime)),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )

        // Change Theme Button
        Spacer(modifier = Modifier.height(12.dp))
        TextButton(onClick = onColorClick) {
            Text(
                text = "🎨 Change Theme Color",
                style = MaterialTheme.typography.labelMedium
            )
        }
    }
}

@Composable
private fun MainCountdownCard(
    timeRemaining: TimeRemaining,
    accentColor: Color,
    modifier: Modifier = Modifier
) {
    val containerColor = accentColor.copy(alpha = 0.15f)

    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(28.dp))
            .background(containerColor)
            .padding(vertical = 32.dp, horizontal = 16.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            Text(
                text = if (timeRemaining.isExpired) "🎉 Event Completed!" else "⏱️ Time Remaining",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f)
            )

            if (timeRemaining.isExpired) {
                Text(
                    text = "Congratulations!",
                    style = MaterialTheme.typography.headlineLarge,
                    fontWeight = FontWeight.Bold,
                    color = accentColor
                )
            } else {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    LargeTimeUnit(value = timeRemaining.days, label = "Days", accentColor = accentColor)
                    LargeTimeUnit(value = timeRemaining.hours, label = "Hours", accentColor = accentColor)
                    LargeTimeUnit(value = timeRemaining.minutes, label = "Min", accentColor = accentColor)
                    LargeTimeUnit(value = timeRemaining.seconds, label = "Sec", accentColor = accentColor)
                }
            }
        }
    }
}

@Composable
private fun LargeTimeUnit(
    value: Long,
    label: String,
    accentColor: Color
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = value.toString().padStart(2, '0'),
            fontSize = 48.sp,
            fontWeight = FontWeight.Bold,
            color = accentColor
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
        )
    }
}

@Composable
private fun QuickStatsSection(
    counter: Counter,
    modifier: Modifier = Modifier
) {
    val now = System.currentTimeMillis()
    val totalDuration = counter.targetDateTime - counter.createdAt
    val remainingDuration = counter.targetDateTime - now
    val daysPassed = ((now - counter.createdAt) / (1000 * 60 * 60 * 24)).coerceAtLeast(0)
    val daysRemaining = (remainingDuration / (1000 * 60 * 60 * 24)).coerceAtLeast(0)

    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        QuickStatCard(
            emoji = "📅",
            value = daysPassed.toString(),
            label = "Days Passed",
            modifier = Modifier.weight(1f)
        )
        QuickStatCard(
            emoji = "⏳",
            value = daysRemaining.toString(),
            label = "Days Left",
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
private fun QuickStatCard(
    emoji: String,
    value: String,
    label: String,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = emoji, fontSize = 24.sp)
            Text(
                text = value,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun ProgressCard(
    counter: Counter,
    modifier: Modifier = Modifier
) {
    val formatter = remember { SimpleDateFormat("MMM dd, yyyy", Locale.getDefault()) }
    val totalDuration = (counter.targetDateTime - counter.createdAt).coerceAtLeast(1L)
    val elapsed = (System.currentTimeMillis() - counter.createdAt).coerceIn(0L, totalDuration)
    val progress = elapsed.toFloat() / totalDuration.toFloat()

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "📊 Progress",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = "${(progress * 100).toInt()}%",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
            }

            LinearProgressIndicator(
                progress = { progress },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(10.dp)
                    .clip(RoundedCornerShape(5.dp)),
                color = MaterialTheme.colorScheme.primary,
                trackColor = MaterialTheme.colorScheme.surfaceVariant
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = "Created",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = formatter.format(Date(counter.createdAt)),
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Medium
                    )
                }
                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = "Target",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = formatter.format(Date(counter.targetDateTime)),
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }
    }
}

@Composable
private fun NoteCard(
    remark: String,
    modifier: Modifier = Modifier,
    onDelete: (() -> Unit)? = null
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.Top
        ) {
            Text(
                text = remark,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier
                    .weight(1f)
                    .padding(end = 8.dp),
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            if (onDelete != null) {
                Icon(
                    imageVector = Icons.Filled.Close,
                    contentDescription = "Delete note",
                    modifier = Modifier
                        .size(20.dp)
                        .clip(CircleShape)
                        .clickable { onDelete() },
                    tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                )
            }
        }
    }
}

@Composable
private fun EmptyNotesCard(
    onAddNote: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onAddNote() },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(text = "📝", fontSize = 32.sp)
            Text(
                text = "No notes yet",
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Medium
            )
            Text(
                text = "Tap to add your first note",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun AddNoteDialog(
    remarkText: String,
    onTextChange: (String) -> Unit,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(
                onClick = onConfirm,
                enabled = remarkText.isNotBlank()
            ) {
                Text("Save")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        },
        title = {
            Text(text = "Add Note")
        },
        text = {
            OutlinedTextField(
                value = remarkText,
                onValueChange = onTextChange,
                placeholder = { Text("Write your note...") },
                minLines = 3,
                maxLines = 5,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)
                )
            )
        },
        shape = RoundedCornerShape(20.dp)
    )
}

