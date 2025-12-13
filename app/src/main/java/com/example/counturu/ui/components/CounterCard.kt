package com.example.counturu.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.counturu.data.Counter
import com.example.counturu.utils.calculateTimeRemaining
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CounterCard(
    counter: Counter,
    onCardClick: (Counter) -> Unit,
    onFavoriteClick: (Counter) -> Unit,
    onDeleteClick: (Counter) -> Unit,
    modifier: Modifier = Modifier
) {
    var timeRemaining by remember { mutableStateOf(calculateTimeRemaining(counter.targetDateTime)) }

    // Update countdown every second
    LaunchedEffect(counter.targetDateTime) {
        while (true) {
            timeRemaining = calculateTimeRemaining(counter.targetDateTime)
            delay(1000)
        }
    }

    val swipeDismissState = rememberSwipeToDismissBoxState(
        confirmValueChange = { state ->
            when (state) {
                SwipeToDismissBoxValue.StartToEnd -> {
                    onFavoriteClick(counter)
                    false
                }
                SwipeToDismissBoxValue.EndToStart -> {
                    onDeleteClick(counter)
                    true
                }
                else -> false
            }
        }
    )

    AnimatedVisibility(
        visible = true,
        enter = fadeIn() + scaleIn(),
        exit = fadeOut() + scaleOut()
    ) {
        SwipeToDismissBox(
            state = swipeDismissState,
            backgroundContent = {
                SwipeBackground(dismissDirection = swipeDismissState.dismissDirection)
            },
            modifier = modifier
        ) {
            GlassmorphicCard(
                onClick = { onCardClick(counter) },
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    // Image if available
                    counter.imageUri?.let { uri ->
                        AsyncImage(
                            model = uri,
                            contentDescription = counter.title,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(150.dp)
                                .clip(RoundedCornerShape(12.dp)),
                            contentScale = ContentScale.Crop
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                    }

                    // Title
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = counter.title,
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.weight(1f)
                        )

                        // Clickable favorite icon - always visible
                        Icon(
                            imageVector = Icons.Filled.Star,
                            contentDescription = if (counter.isFavorite) "Remove from favorites" else "Add to favorites",
                            tint = if (counter.isFavorite) Color(0xFFFFD700) else Color.Gray.copy(alpha = 0.4f),
                            modifier = Modifier
                                .size(28.dp)
                                .clickable { onFavoriteClick(counter) }
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Countdown Display
                    if (timeRemaining.isExpired) {
                        Text(
                            text = "Event has passed",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.error
                        )
                    } else {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceEvenly
                        ) {
                            TimeUnit(value = timeRemaining.days, label = "Days")
                            TimeUnit(value = timeRemaining.hours, label = "Hours")
                            TimeUnit(value = timeRemaining.minutes, label = "Mins")
                            TimeUnit(value = timeRemaining.seconds, label = "Secs")
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun TimeUnit(value: Long, label: String) {
    val gradientColors = when (label) {
        "Days" -> listOf(Color(0xFFBF5AF2), Color(0xFF9D4EDD))
        "Hours" -> listOf(Color(0xFF0A84FF), Color(0xFF64D2FF))
        "Mins" -> listOf(Color(0xFF32ADE6), Color(0xFF64D2FF))
        "Secs" -> listOf(Color(0xFF5856D6), Color(0xFFBF5AF2))
        else -> listOf(Color(0xFF0A84FF), Color(0xFF64D2FF))
    }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .padding(6.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(
                brush = Brush.linearGradient(colors = gradientColors.map { it.copy(alpha = 0.15f) })
            )
            .padding(horizontal = 14.dp, vertical = 12.dp)
    ) {
        Text(
            text = value.toString().padStart(2, '0'),
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.ExtraBold,
            fontSize = 34.sp,
            color = MaterialTheme.colorScheme.onSurface
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.65f),
            fontSize = 11.sp
        )
    }
}

@Composable
private fun SwipeBackground(dismissDirection: SwipeToDismissBoxValue) {
    val color by animateColorAsState(
        targetValue = when (dismissDirection) {
            SwipeToDismissBoxValue.StartToEnd -> Color(0xFFFFD700).copy(alpha = 0.3f)
            SwipeToDismissBoxValue.EndToStart -> Color.Red.copy(alpha = 0.3f)
            else -> Color.Transparent
        },
        animationSpec = tween(300), label = "swipe_bg_color"
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(color)
            .padding(horizontal = 24.dp),
        contentAlignment = when (dismissDirection) {
            SwipeToDismissBoxValue.StartToEnd -> Alignment.CenterStart
            SwipeToDismissBoxValue.EndToStart -> Alignment.CenterEnd
            else -> Alignment.Center
        }
    ) {
        when (dismissDirection) {
            SwipeToDismissBoxValue.StartToEnd -> {
                Icon(
                    imageVector = Icons.Filled.Star,
                    contentDescription = "Favorite",
                    tint = Color(0xFFFFD700),
                    modifier = Modifier.size(32.dp)
                )
            }
            SwipeToDismissBoxValue.EndToStart -> {
                Icon(
                    imageVector = Icons.Filled.Delete,
                    contentDescription = "Delete",
                    tint = Color.Red,
                    modifier = Modifier.size(32.dp)
                )
            }
            else -> {}
        }
    }
}

@Composable
fun GlassmorphicCard(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    val isDark = MaterialTheme.colorScheme.background == Color(0xFF000000)

    Card(
        modifier = modifier
            .clickable(onClick = onClick)
            .border(
                width = 1.5.dp,
                brush = Brush.linearGradient(
                    colors = listOf(
                        Color.White.copy(alpha = if (isDark) 0.15f else 0.25f),
                        Color.White.copy(alpha = if (isDark) 0.05f else 0.15f)
                    )
                ),
                shape = RoundedCornerShape(28.dp)
            ),
        shape = RoundedCornerShape(28.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isDark) {
                Color(0xFF2C2C2E).copy(alpha = 0.88f)
            } else {
                Color.White.copy(alpha = 0.92f)
            }
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 12.dp,
            pressedElevation = 8.dp
        )
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.verticalGradient(
                        colors = if (isDark) {
                            listOf(
                                Color.White.copy(alpha = 0.08f),
                                Color.Transparent,
                                Color(0xFFBF5AF2).copy(alpha = 0.03f)
                            )
                        } else {
                            listOf(
                                Color.White.copy(alpha = 0.6f),
                                Color(0xFFBF5AF2).copy(alpha = 0.03f),
                                Color(0xFF0A84FF).copy(alpha = 0.03f)
                            )
                        }
                    )
                )
        ) {
            content()
        }
    }
}

