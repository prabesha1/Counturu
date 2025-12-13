package com.example.counturu.ui.components

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateCounterDialog(
    isVisible: Boolean,
    onDismiss: () -> Unit,
    onSave: (title: String, targetDateTime: Long, imageUri: String?, hasReminder: Boolean, isFavorite: Boolean, icon: String, backgroundColor: Long?) -> Unit
) {
    if (!isVisible) return

    var title by remember { mutableStateOf("") }
    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }
    var hasReminder by remember { mutableStateOf(false) }
    var isFavorite by remember { mutableStateOf(false) }
    var selectedDate by remember { mutableStateOf<Date?>(null) }
    var selectedTime by remember { mutableStateOf(12 to 0) }
    var selectedIcon by remember { mutableStateOf("⏱️") }
    var selectedBackgroundColor by remember { mutableStateOf<Long?>(null) }
    var showIconPicker by remember { mutableStateOf(false) }
    var showColorPicker by remember { mutableStateOf(false) }

    val context = LocalContext.current
    val calendar = Calendar.getInstance()

    val dateFormat = remember { SimpleDateFormat("MMM dd, yyyy", Locale.getDefault()) }
    val timeFormat = remember { SimpleDateFormat("hh:mm a", Locale.getDefault()) }

    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        selectedImageUri = uri
    }

    val datePickerDialog = DatePickerDialog(
        context,
        { _, year, month, dayOfMonth ->
            calendar.set(year, month, dayOfMonth)
            selectedDate = calendar.time
        },
        calendar.get(Calendar.YEAR),
        calendar.get(Calendar.MONTH),
        calendar.get(Calendar.DAY_OF_MONTH)
    ).apply {
        datePicker.minDate = System.currentTimeMillis()
    }

    val timePickerDialog = TimePickerDialog(
        context,
        { _, hourOfDay, minute ->
            selectedTime = hourOfDay to minute
        },
        selectedTime.first,
        selectedTime.second,
        false
    )

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = MaterialTheme.colorScheme.surface,
        shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
                .padding(bottom = 32.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "New Counter",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Create a countdown for your event",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    IconButton(onClick = { isFavorite = !isFavorite }) {
                        Icon(
                            imageVector = if (isFavorite) Icons.Filled.Favorite else Icons.Filled.FavoriteBorder,
                            contentDescription = "Favorite",
                            tint = if (isFavorite) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    IconButton(onClick = onDismiss) {
                        Icon(
                            imageVector = Icons.Filled.Close,
                            contentDescription = "Close"
                        )
                    }
                }
            }

            // Event Title Section
            Text(
                text = "Event Details",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.primary
            )

            OutlinedTextField(
                value = title,
                onValueChange = { title = it },
                label = { Text("Event Title") },
                placeholder = { Text("e.g., Birthday Party") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                shape = RoundedCornerShape(12.dp)
            )

            // Date & Time Section
            Text(
                text = "Date & Time",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.primary
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Date Picker Card
                Card(
                    modifier = Modifier
                        .weight(1f)
                        .clickable { datePickerDialog.show() },
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = if (selectedDate != null)
                            MaterialTheme.colorScheme.primaryContainer
                        else
                            MaterialTheme.colorScheme.surfaceVariant
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Filled.DateRange,
                                contentDescription = null,
                                modifier = Modifier.size(20.dp),
                                tint = if (selectedDate != null)
                                    MaterialTheme.colorScheme.onPrimaryContainer
                                else
                                    MaterialTheme.colorScheme.primary
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Date",
                                style = MaterialTheme.typography.labelMedium,
                                color = if (selectedDate != null)
                                    MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f)
                                else
                                    MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = selectedDate?.let { dateFormat.format(it) } ?: "Select date",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold,
                            color = if (selectedDate != null)
                                MaterialTheme.colorScheme.onPrimaryContainer
                            else
                                MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                // Time Picker Card
                Card(
                    modifier = Modifier
                        .weight(1f)
                        .clickable { timePickerDialog.show() },
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(text = "🕒", modifier = Modifier.size(20.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Time",
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = run {
                                val tempCal = Calendar.getInstance()
                                tempCal.set(Calendar.HOUR_OF_DAY, selectedTime.first)
                                tempCal.set(Calendar.MINUTE, selectedTime.second)
                                timeFormat.format(tempCal.time)
                            },
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }

            // Cover Image Section
            Text(
                text = "Cover Image (Optional)",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.primary
            )

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { imagePickerLauncher.launch("image/*") },
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                )
            ) {
                if (selectedImageUri != null) {
                    AsyncImage(
                        model = selectedImageUri,
                        contentDescription = "Event cover",
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(150.dp)
                            .clip(RoundedCornerShape(12.dp)),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Text(text = "🖼️", modifier = Modifier.size(28.dp))
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = "Add Cover Image",
                                style = MaterialTheme.typography.bodyLarge,
                                fontWeight = FontWeight.Medium
                            )
                            Text(
                                text = "Tap to select from gallery",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }

            // Icon & Color Section
            Text(
                text = "Icon & Theme",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.primary
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Icon Picker Card
                Card(
                    modifier = Modifier
                        .weight(1f)
                        .clickable { showIconPicker = true },
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant
                    )
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = selectedIcon,
                            fontSize = 32.sp
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Icon",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                // Color Picker Card
                Card(
                    modifier = Modifier
                        .weight(1f)
                        .clickable { showColorPicker = true },
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = if (selectedBackgroundColor != null)
                            Color(selectedBackgroundColor!!)
                        else
                            MaterialTheme.colorScheme.surfaceVariant
                    )
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "🎨",
                            fontSize = 32.sp
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = if (selectedBackgroundColor != null) "Custom" else "Auto",
                            style = MaterialTheme.typography.labelMedium,
                            color = if (selectedBackgroundColor != null)
                                Color.White
                            else
                                MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            // Reminder Section
            Text(
                text = "Settings",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.primary
            )

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Outlined.Notifications,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = "Enable Reminder",
                                style = MaterialTheme.typography.bodyLarge,
                                fontWeight = FontWeight.Medium
                            )
                            Text(
                                text = if (hasReminder) "You'll be notified" else "Get notified before event",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                    Switch(
                        checked = hasReminder,
                        onCheckedChange = { hasReminder = it }
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Create Button
            Button(
                onClick = {
                    val targetCalendar = Calendar.getInstance()
                    selectedDate?.let { targetCalendar.time = it }
                    targetCalendar.set(Calendar.HOUR_OF_DAY, selectedTime.first)
                    targetCalendar.set(Calendar.MINUTE, selectedTime.second)
                    targetCalendar.set(Calendar.SECOND, 0)

                    onSave(
                        title.trim(),
                        targetCalendar.timeInMillis,
                        selectedImageUri?.toString(),
                        hasReminder,
                        isFavorite,
                        selectedIcon,
                        selectedBackgroundColor
                    )

                    // Reset state
                    title = ""
                    selectedImageUri = null
                    hasReminder = false
                    isFavorite = false
                    selectedDate = null
                    selectedTime = 12 to 0
                    selectedIcon = "⏱️"
                    selectedBackgroundColor = null
                    onDismiss()
                },
                enabled = title.isNotBlank() && selectedDate != null,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(16.dp)
            ) {
                Text(
                    text = "Create Counter",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }

    // Icon Picker Dialog
    if (showIconPicker) {
        IconPickerDialog(
            selectedIcon = selectedIcon,
            onIconSelected = { selectedIcon = it },
            onDismiss = { showIconPicker = false }
        )
    }

    // Color Picker Dialog
    if (showColorPicker) {
        ColorPickerDialog(
            selectedColor = selectedBackgroundColor,
            onColorSelected = { selectedBackgroundColor = it },
            onDismiss = { showColorPicker = false }
        )
    }
}

