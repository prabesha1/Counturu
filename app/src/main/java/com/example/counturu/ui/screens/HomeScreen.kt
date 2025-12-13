package com.example.counturu.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.DateRange
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.counturu.data.Counter
import com.example.counturu.ui.components.CreateCounterDialog
import com.example.counturu.utils.calculateTimeRemaining
import com.example.counturu.viewmodel.CounterViewModel
import kotlinx.coroutines.delay

enum class CounterFilter {
    ALL, UPCOMING, TODAY, THIS_WEEK, COMPLETED
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: CounterViewModel,
    onCounterClick: (Counter) -> Unit
) {
    val allCounters by viewModel.allCounters.collectAsState()
    var showCreateDialog by remember { mutableStateOf(false) }
    var searchQuery by remember { mutableStateOf("") }
    var selectedFilter by remember { mutableStateOf(CounterFilter.ALL) }

    // Filter and search logic
    val filteredCounters by remember(allCounters, searchQuery, selectedFilter) {
        derivedStateOf {
            val now = System.currentTimeMillis()
            val todayEnd = now + (24 * 60 * 60 * 1000)
            val weekEnd = now + (7 * 24 * 60 * 60 * 1000)

            allCounters
                .filter { counter ->
                    if (searchQuery.isBlank()) true
                    else counter.title.contains(searchQuery, ignoreCase = true)
                }
                .filter { counter ->
                    when (selectedFilter) {
                        CounterFilter.ALL -> true
                        CounterFilter.UPCOMING -> counter.targetDateTime > now
                        CounterFilter.TODAY -> counter.targetDateTime in now..todayEnd
                        CounterFilter.THIS_WEEK -> counter.targetDateTime in now..weekEnd
                        CounterFilter.COMPLETED -> counter.targetDateTime <= now
                    }
                }
                .sortedBy { it.targetDateTime }
        }
    }

    // Stats
    val upcomingCount = allCounters.count { it.targetDateTime > System.currentTimeMillis() }
    val completedCount = allCounters.count { it.targetDateTime <= System.currentTimeMillis() }
    val favoriteCount = allCounters.count { it.isFavorite }

    // Next upcoming counter
    val nextUpcoming = allCounters
        .filter { it.targetDateTime > System.currentTimeMillis() }
        .minByOrNull { it.targetDateTime }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Counturu",
                        fontWeight = FontWeight.Bold
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        },
        bottomBar = {
            BottomSearchBar(
                searchQuery = searchQuery,
                onSearchQueryChange = { searchQuery = it },
                onCreateClick = { showCreateDialog = true }
            )
        }
    ) { paddingValues ->
        if (allCounters.isEmpty()) {
            EmptyHomeState(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                onCreateClick = { showCreateDialog = true }
            )
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentPadding = PaddingValues(bottom = 16.dp)
            ) {

                // Quick Stats Row
                item {
                    QuickStatsRow(
                        total = allCounters.size,
                        upcoming = upcomingCount,
                        completed = completedCount,
                        favorites = favoriteCount
                    )
                }

                // Next Upcoming Highlight (if exists)
                nextUpcoming?.let { counter ->
                    item {
                        Text(
                            text = "⚡ Coming Up Next",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(start = 16.dp, top = 16.dp, bottom = 8.dp)
                        )
                        NextUpcomingCard(
                            counter = counter,
                            onClick = { onCounterClick(counter) }
                        )
                    }
                }

                // Filter Chips
                item {
                    Spacer(modifier = Modifier.height(16.dp))
                    FilterChipsRow(
                        selectedFilter = selectedFilter,
                        onFilterSelected = { selectedFilter = it },
                        counters = allCounters
                    )
                }

                // Section Header
                item {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = when (selectedFilter) {
                                CounterFilter.ALL -> "All Counters"
                                CounterFilter.UPCOMING -> "Upcoming"
                                CounterFilter.TODAY -> "Today"
                                CounterFilter.THIS_WEEK -> "This Week"
                                CounterFilter.COMPLETED -> "Completed"
                            },
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "${filteredCounters.size} items",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                // Counter List
                if (filteredCounters.isEmpty()) {
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(32.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = if (searchQuery.isNotEmpty())
                                    "No counters match \"$searchQuery\""
                                else
                                    "No counters in this category",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                } else {
                    items(
                        items = filteredCounters,
                        key = { it.id }
                    ) { counter ->
                        CounterListItem(
                            counter = counter,
                            onClick = { onCounterClick(counter) },
                            onFavoriteClick = { viewModel.toggleFavorite(counter) },
                            onDeleteClick = { viewModel.deleteCounter(counter) }
                        )
                    }
                }
            }
        }
    }

    // Create Counter Dialog
    CreateCounterDialog(
        isVisible = showCreateDialog,
        onDismiss = { showCreateDialog = false },
        onSave = { title, targetDateTime, imageUri, hasReminder, isFavorite, icon, backgroundColor ->
            viewModel.insertCounter(
                Counter(
                    title = title,
                    targetDateTime = targetDateTime,
                    imageUri = imageUri,
                    hasReminder = hasReminder,
                    isFavorite = isFavorite,
                    icon = icon,
                    backgroundColor = backgroundColor
                )
            )
        }
    )
}

@Composable
private fun EmptyHomeState(
    modifier: Modifier = Modifier,
    onCreateClick: () -> Unit
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "⏱️",
            fontSize = 80.sp
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "No Counters Yet",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Create your first countdown to track\nimportant events and dates",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(24.dp))
        Card(
            modifier = Modifier.clickable { onCreateClick() },
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer
            )
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 24.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Filled.Add,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onPrimaryContainer
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Create Counter",
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }
        }
    }
}

@Composable
private fun QuickStatsRow(
    total: Int,
    upcoming: Int,
    completed: Int,
    favorites: Int
) {
    LazyRow(
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
    ) {
        item {
            QuickStatCard(
                emoji = "📊",
                value = total.toString(),
                label = "Total",
                color = MaterialTheme.colorScheme.primaryContainer
            )
        }
        item {
            QuickStatCard(
                emoji = "⏳",
                value = upcoming.toString(),
                label = "Upcoming",
                color = MaterialTheme.colorScheme.secondaryContainer
            )
        }
        item {
            QuickStatCard(
                emoji = "✅",
                value = completed.toString(),
                label = "Completed",
                color = MaterialTheme.colorScheme.tertiaryContainer
            )
        }
        item {
            QuickStatCard(
                emoji = "⭐",
                value = favorites.toString(),
                label = "Favorites",
                color = Color(0xFFFFF3E0)
            )
        }
    }
}

@Composable
private fun QuickStatCard(
    emoji: String,
    value: String,
    label: String,
    color: Color
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = color)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
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
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
            )
        }
    }
}

@Composable
private fun NextUpcomingCard(
    counter: Counter,
    onClick: () -> Unit
) {
    var timeRemaining by remember { mutableStateOf(calculateTimeRemaining(counter.targetDateTime)) }

    LaunchedEffect(counter.targetDateTime) {
        while (true) {
            timeRemaining = calculateTimeRemaining(counter.targetDateTime)
            delay(1000)
        }
    }

    val bgColor = if (counter.backgroundColor != null) {
        Color(counter.backgroundColor)
    } else {
        MaterialTheme.colorScheme.primary
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = bgColor.copy(alpha = 0.15f)
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Icon
            Box(
                modifier = Modifier
                    .size(64.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(bgColor.copy(alpha = 0.2f)),
                contentAlignment = Alignment.Center
            ) {
                if (counter.imageUri != null) {
                    AsyncImage(
                        model = counter.imageUri,
                        contentDescription = null,
                        modifier = Modifier
                            .size(64.dp)
                            .clip(RoundedCornerShape(16.dp)),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Text(text = counter.icon, fontSize = 32.sp)
                }
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = counter.title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(4.dp))
                Row(
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    CountdownMini(value = timeRemaining.days, label = "d")
                    CountdownMini(value = timeRemaining.hours, label = "h")
                    CountdownMini(value = timeRemaining.minutes, label = "m")
                    CountdownMini(value = timeRemaining.seconds, label = "s")
                }
            }

            if (counter.isFavorite) {
                Icon(
                    imageVector = Icons.Filled.Star,
                    contentDescription = "Favorite",
                    tint = Color(0xFFFFD700),
                    modifier = Modifier.size(24.dp)
                )
            }
        }
    }
}

@Composable
private fun CountdownMini(value: Long, label: String) {
    Row(verticalAlignment = Alignment.Bottom) {
        Text(
            text = value.toString().padStart(2, '0'),
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(bottom = 2.dp)
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun FilterChipsRow(
    selectedFilter: CounterFilter,
    onFilterSelected: (CounterFilter) -> Unit,
    counters: List<Counter>
) {
    val now = System.currentTimeMillis()
    val todayEnd = now + (24 * 60 * 60 * 1000)
    val weekEnd = now + (7 * 24 * 60 * 60 * 1000)

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState())
            .padding(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        FilterChip(
            selected = selectedFilter == CounterFilter.ALL,
            onClick = { onFilterSelected(CounterFilter.ALL) },
            label = { Text("All (${counters.size})") },
            colors = FilterChipDefaults.filterChipColors(
                selectedContainerColor = MaterialTheme.colorScheme.primaryContainer
            )
        )
        FilterChip(
            selected = selectedFilter == CounterFilter.UPCOMING,
            onClick = { onFilterSelected(CounterFilter.UPCOMING) },
            label = { Text("Upcoming") }
        )
        FilterChip(
            selected = selectedFilter == CounterFilter.TODAY,
            onClick = { onFilterSelected(CounterFilter.TODAY) },
            label = { Text("Today") }
        )
        FilterChip(
            selected = selectedFilter == CounterFilter.THIS_WEEK,
            onClick = { onFilterSelected(CounterFilter.THIS_WEEK) },
            label = { Text("This Week") }
        )
        FilterChip(
            selected = selectedFilter == CounterFilter.COMPLETED,
            onClick = { onFilterSelected(CounterFilter.COMPLETED) },
            label = { Text("Completed") }
        )
    }
}

@Composable
private fun CounterListItem(
    counter: Counter,
    onClick: () -> Unit,
    onFavoriteClick: () -> Unit,
    onDeleteClick: () -> Unit
) {
    var timeRemaining by remember { mutableStateOf(calculateTimeRemaining(counter.targetDateTime)) }

    LaunchedEffect(counter.targetDateTime) {
        while (true) {
            timeRemaining = calculateTimeRemaining(counter.targetDateTime)
            delay(1000)
        }
    }

    val bgColor = if (counter.backgroundColor != null) {
        Color(counter.backgroundColor).copy(alpha = 0.1f)
    } else {
        MaterialTheme.colorScheme.surface
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = bgColor),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Icon
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(
                        if (counter.backgroundColor != null)
                            Color(counter.backgroundColor).copy(alpha = 0.2f)
                        else
                            MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f)
                    ),
                contentAlignment = Alignment.Center
            ) {
                if (counter.imageUri != null) {
                    AsyncImage(
                        model = counter.imageUri,
                        contentDescription = null,
                        modifier = Modifier
                            .size(48.dp)
                            .clip(RoundedCornerShape(12.dp)),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Text(text = counter.icon, fontSize = 24.sp)
                }
            }

            Spacer(modifier = Modifier.width(12.dp))

            // Title and time
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = counter.title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Outlined.DateRange,
                        contentDescription = null,
                        modifier = Modifier.size(14.dp),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = if (timeRemaining.isExpired) {
                            "Completed ✓"
                        } else {
                            "${timeRemaining.days}d ${timeRemaining.hours}h ${timeRemaining.minutes}m"
                        },
                        style = MaterialTheme.typography.bodySmall,
                        color = if (timeRemaining.isExpired)
                            MaterialTheme.colorScheme.primary
                        else
                            MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            // Actions
            IconButton(
                onClick = onFavoriteClick,
                modifier = Modifier.size(36.dp)
            ) {
                Icon(
                    imageVector = Icons.Filled.Star,
                    contentDescription = "Toggle favorite",
                    tint = if (counter.isFavorite) Color(0xFFFFD700) else Color.Gray.copy(alpha = 0.4f),
                    modifier = Modifier.size(20.dp)
                )
            }

            IconButton(
                onClick = onDeleteClick,
                modifier = Modifier.size(36.dp)
            ) {
                Icon(
                    imageVector = Icons.Filled.Delete,
                    contentDescription = "Delete",
                    tint = MaterialTheme.colorScheme.error.copy(alpha = 0.6f),
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}

@Composable
private fun BottomSearchBar(
    searchQuery: String,
    onSearchQueryChange: (String) -> Unit,
    onCreateClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        shape = RoundedCornerShape(28.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Search Icon
            Icon(
                imageVector = Icons.Filled.Search,
                contentDescription = "Search",
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(start = 8.dp)
            )

            // Search TextField
            androidx.compose.foundation.text.BasicTextField(
                value = searchQuery,
                onValueChange = onSearchQueryChange,
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 12.dp, vertical = 12.dp),
                textStyle = MaterialTheme.typography.bodyLarge.copy(
                    color = MaterialTheme.colorScheme.onSurface
                ),
                singleLine = true,
                decorationBox = { innerTextField ->
                    Box {
                        if (searchQuery.isEmpty()) {
                            Text(
                                text = "Search counters...",
                                style = MaterialTheme.typography.bodyLarge,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        innerTextField()
                    }
                }
            )

            // Clear button (shows when there's text)
            AnimatedVisibility(visible = searchQuery.isNotEmpty()) {
                IconButton(
                    onClick = { onSearchQueryChange("") },
                    modifier = Modifier.size(40.dp)
                ) {
                    Icon(
                        imageVector = Icons.Filled.Clear,
                        contentDescription = "Clear",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            // FAB Create Button
            FloatingActionButton(
                onClick = onCreateClick,
                modifier = Modifier.size(48.dp),
                containerColor = MaterialTheme.colorScheme.primary,
                shape = CircleShape,
                elevation = FloatingActionButtonDefaults.elevation(
                    defaultElevation = 0.dp,
                    pressedElevation = 0.dp
                )
            ) {
                Icon(
                    imageVector = Icons.Filled.Add,
                    contentDescription = "Create Counter",
                    tint = MaterialTheme.colorScheme.onPrimary
                )
            }
        }
    }
}

