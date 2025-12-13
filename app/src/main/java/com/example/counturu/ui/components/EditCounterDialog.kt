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
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.counturu.data.Counter
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

@Composable
fun EditCounterDialog(
    counter: Counter,
    onDismiss: () -> Unit,
    onSave: (Counter) -> Unit
) {
    var title by remember { mutableStateOf(counter.title) }
    var selectedImageUri by remember { mutableStateOf(counter.imageUri?.let { Uri.parse(it) }) }
    var hasReminder by remember { mutableStateOf(counter.hasReminder) }
    var isFavorite by remember { mutableStateOf(counter.isFavorite) }

    val calendar = remember {
        Calendar.getInstance().apply {
            timeInMillis = counter.targetDateTime
        }
    }
    var selectedDate by remember { mutableStateOf(Date(counter.targetDateTime)) }
    var selectedTime by remember {
        mutableStateOf(
            calendar.get(Calendar.HOUR_OF_DAY) to calendar.get(Calendar.MINUTE)
        )
    }

    val context = LocalContext.current
    val dateFormat = remember { SimpleDateFormat("MMM dd, yyyy", Locale.getDefault()) }
    val timeFormat = remember { SimpleDateFormat("hh:mm a", Locale.getDefault()) }

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
    )

    val timePickerDialog = TimePickerDialog(
        context,
        { _, hourOfDay, minute ->
            selectedTime = hourOfDay to minute
        },
        selectedTime.first,
        selectedTime.second,
        false
    )

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Edit Counter",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold
                )
                IconButton(onClick = { isFavorite = !isFavorite }) {
                    Icon(
                        imageVector = if (isFavorite) Icons.Filled.Favorite else Icons.Filled.FavoriteBorder,
                        contentDescription = "Toggle Favorite",
                        tint = if (isFavorite) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Title
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Event Title") },
                    placeholder = { Text("Enter event name") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp)
                )

                // Date & Time Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Date Picker
                    Card(
                        modifier = Modifier
                            .weight(1f)
                            .clickable { datePickerDialog.show() },
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surfaceVariant
                        )
                    ) {
                        Column(
                            modifier = Modifier.padding(12.dp)
                        ) {
                            Text(
                                text = "Date",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Row(
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.DateRange,
                                    contentDescription = null,
                                    modifier = Modifier.size(18.dp),
                                    tint = MaterialTheme.colorScheme.primary
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = dateFormat.format(selectedDate),
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }
                    }

                    // Time Picker
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
                            modifier = Modifier.padding(12.dp)
                        ) {
                            Text(
                                text = "Time",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Row(
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(text = "🕒", modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = run {
                                        val tempCal = Calendar.getInstance()
                                        tempCal.set(Calendar.HOUR_OF_DAY, selectedTime.first)
                                        tempCal.set(Calendar.MINUTE, selectedTime.second)
                                        timeFormat.format(tempCal.time)
                                    },
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }
                    }
                }

                // Image
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
                                .height(120.dp)
                                .clip(RoundedCornerShape(12.dp)),
                            contentScale = ContentScale.Crop
                        )
                    } else {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(text = "🖼️", modifier = Modifier.size(24.dp))
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = "Add Cover Image",
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.Medium
                                )
                                Text(
                                    text = "Tap to select an image",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }

                // Reminder Toggle
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
                            Text(
                                text = "Reminder",
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Medium
                            )
                        }
                        Switch(
                            checked = hasReminder,
                            onCheckedChange = { hasReminder = it }
                        )
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val targetCalendar = Calendar.getInstance()
                    targetCalendar.time = selectedDate
                    targetCalendar.set(Calendar.HOUR_OF_DAY, selectedTime.first)
                    targetCalendar.set(Calendar.MINUTE, selectedTime.second)
                    targetCalendar.set(Calendar.SECOND, 0)

                    onSave(
                        counter.copy(
                            title = title.trim(),
                            targetDateTime = targetCalendar.timeInMillis,
                            imageUri = selectedImageUri?.toString(),
                            hasReminder = hasReminder,
                            isFavorite = isFavorite
                        )
                    )
                },
                enabled = title.isNotBlank()
            ) {
                Text("Save Changes")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        },
        shape = RoundedCornerShape(24.dp)
    )
}

