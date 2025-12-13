package com.example.counturu.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.counturu.data.Counter
import com.example.counturu.utils.calculateTimeRemaining
import com.example.counturu.viewmodel.CounterViewModel
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FavsScreen(
    viewModel: CounterViewModel,
    onCounterClick: (Counter) -> Unit
) {
    val favoriteCounters by viewModel.favoriteCounters.collectAsState()

    // Sort favorites: upcoming first, then by date
    val sortedFavorites = remember(favoriteCounters) {
        favoriteCounters.sortedBy { it.targetDateTime }
    }

    // Get the next upcoming favorite
    val nextUpcoming = sortedFavorites.firstOrNull {
        it.targetDateTime > System.currentTimeMillis()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Filled.Favorite,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.error,
                            modifier = Modifier.size(28.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Favorites",
                            fontWeight = FontWeight.Bold
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        }
    ) { paddingValues ->
        if (favoriteCounters.isEmpty()) {
            // Empty State
            EmptyFavoritesState(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            )
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentPadding = PaddingValues(bottom = 16.dp)
            ) {
                // Stats Header
                item {
                    FavoritesStatsCard(
                        totalFavorites = favoriteCounters.size,
                        upcomingCount = sortedFavorites.count { it.targetDateTime > System.currentTimeMillis() },
                        completedCount = sortedFavorites.count { it.targetDateTime <= System.currentTimeMillis() }
                    )
                }

                // Next Upcoming Highlight
                nextUpcoming?.let { counter ->
                    item {
                        Text(
                            text = "Next Upcoming",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold,
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                        )
                        FeaturedFavoriteCard(
                            counter = counter,
                            onClick = { onCounterClick(counter) },
                            onRemoveFavorite = { viewModel.toggleFavorite(counter) }
                        )
                    }
                }

                // Quick Access Row
                if (sortedFavorites.size > 1) {
                    item {
                        Text(
                            text = "Quick Access",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold,
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                        )
                        LazyRow(
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                            contentPadding = PaddingValues(horizontal = 16.dp)
                        ) {
                            items(
                                items = sortedFavorites.take(5),
                                key = { it.id }
                            ) { counter ->
                                QuickAccessCard(
                                    counter = counter,
                                    onClick = { onCounterClick(counter) }
                                )
                            }
                        }
                    }
                }

                // All Favorites Section
                item {
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "All Favorites (${favoriteCounters.size})",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                    )
                }

                itemsIndexed(
                    items = sortedFavorites,
                    key = { _, counter -> counter.id }
                ) { index, counter ->
                    AnimatedVisibility(
                        visible = true,
                        enter = fadeIn() + slideInVertically(
                            initialOffsetY = { it / 2 },
                            animationSpec = spring(
                                dampingRatio = Spring.DampingRatioMediumBouncy,
                                stiffness = Spring.StiffnessLow
                            )
                        )
                    ) {
                        FavoriteListItem(
                            counter = counter,
                            index = index + 1,
                            onClick = { onCounterClick(counter) },
                            onRemoveFavorite = { viewModel.toggleFavorite(counter) },
                            onDelete = { viewModel.deleteCounter(counter) }
                        )
                    }
                }

                item {
                    Spacer(modifier = Modifier.height(80.dp))
                }
            }
        }
    }
}

@Composable
private fun EmptyFavoritesState(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "💔",
            fontSize = 64.sp
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "No Favorites Yet",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Tap the ⭐ on any counter to add it here",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = 48.dp)
        )
    }
}

@Composable
private fun FavoritesStatsCard(
    totalFavorites: Int,
    upcomingCount: Int,
    completedCount: Int
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            StatItem(value = totalFavorites.toString(), label = "Total")
            StatItem(value = upcomingCount.toString(), label = "Upcoming")
            StatItem(value = completedCount.toString(), label = "Completed")
        }
    }
}

@Composable
private fun StatItem(value: String, label: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = value,
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onPrimaryContainer
        )
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f)
        )
    }
}

@Composable
private fun FeaturedFavoriteCard(
    counter: Counter,
    onClick: () -> Unit,
    onRemoveFavorite: () -> Unit
) {
    var timeRemaining by remember { mutableStateOf(calculateTimeRemaining(counter.targetDateTime)) }

    LaunchedEffect(counter.targetDateTime) {
        while (true) {
            timeRemaining = calculateTimeRemaining(counter.targetDateTime)
            delay(1000)
        }
    }

    val backgroundColor = if (counter.backgroundColor != null) {
        Color(counter.backgroundColor)
    } else {
        MaterialTheme.colorScheme.secondaryContainer
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = backgroundColor.copy(alpha = 0.3f)
        )
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    // Icon
                    Box(
                        modifier = Modifier
                            .size(56.dp)
                            .clip(CircleShape)
                            .background(backgroundColor.copy(alpha = 0.3f)),
                        contentAlignment = Alignment.Center
                    ) {
                        if (counter.imageUri != null) {
                            AsyncImage(
                                model = counter.imageUri,
                                contentDescription = null,
                                modifier = Modifier
                                    .size(56.dp)
                                    .clip(CircleShape),
                                contentScale = ContentScale.Crop
                            )
                        } else {
                            Text(text = counter.icon, fontSize = 28.sp)
                        }
                    }
                    Spacer(modifier = Modifier.width(16.dp))
                    Column {
                        Text(
                            text = counter.title,
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Text(
                            text = if (timeRemaining.isExpired) "Completed!" else "Coming up",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
                IconButton(onClick = onRemoveFavorite) {
                    Icon(
                        imageVector = Icons.Filled.Favorite,
                        contentDescription = "Remove from favorites",
                        tint = MaterialTheme.colorScheme.error
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Countdown
            if (!timeRemaining.isExpired) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    CountdownUnit(value = timeRemaining.days, label = "Days")
                    CountdownUnit(value = timeRemaining.hours, label = "Hrs")
                    CountdownUnit(value = timeRemaining.minutes, label = "Min")
                    CountdownUnit(value = timeRemaining.seconds, label = "Sec")
                }
            } else {
                Text(
                    text = "🎉 Event has passed",
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

@Composable
private fun CountdownUnit(value: Long, label: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = value.toString().padStart(2, '0'),
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun QuickAccessCard(
    counter: Counter,
    onClick: () -> Unit
) {
    val backgroundColor = if (counter.backgroundColor != null) {
        Color(counter.backgroundColor)
    } else {
        MaterialTheme.colorScheme.surfaceVariant
    }

    Card(
        modifier = Modifier
            .size(100.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = backgroundColor.copy(alpha = 0.3f)
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(text = counter.icon, fontSize = 32.sp)
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = counter.title,
                style = MaterialTheme.typography.labelSmall,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
private fun FavoriteListItem(
    counter: Counter,
    index: Int,
    onClick: () -> Unit,
    onRemoveFavorite: () -> Unit,
    onDelete: () -> Unit
) {
    var timeRemaining by remember { mutableStateOf(calculateTimeRemaining(counter.targetDateTime)) }

    LaunchedEffect(counter.targetDateTime) {
        while (true) {
            timeRemaining = calculateTimeRemaining(counter.targetDateTime)
            delay(1000)
        }
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Index number
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primaryContainer),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = index.toString(),
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            // Icon
            Text(text = counter.icon, fontSize = 28.sp)

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
                Text(
                    text = if (timeRemaining.isExpired) {
                        "Completed"
                    } else {
                        "${timeRemaining.days}d ${timeRemaining.hours}h ${timeRemaining.minutes}m"
                    },
                    style = MaterialTheme.typography.bodySmall,
                    color = if (timeRemaining.isExpired)
                        MaterialTheme.colorScheme.error
                    else
                        MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            // Actions
            IconButton(
                onClick = onRemoveFavorite,
                modifier = Modifier.size(36.dp)
            ) {
                Icon(
                    imageVector = Icons.Filled.Star,
                    contentDescription = "Remove favorite",
                    tint = Color(0xFFFFD700),
                    modifier = Modifier.size(20.dp)
                )
            }

            IconButton(
                onClick = onDelete,
                modifier = Modifier.size(36.dp)
            ) {
                Icon(
                    imageVector = Icons.Filled.Delete,
                    contentDescription = "Delete",
                    tint = MaterialTheme.colorScheme.error.copy(alpha = 0.7f),
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}

