package com.example.counturu.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.counturu.ui.theme.*
import com.example.counturu.viewmodel.CounterViewModel

@Composable
fun SettingsScreen(
    viewModel: CounterViewModel,
    onAboutClick: () -> Unit
) {
    val isDarkMode by viewModel.isDarkMode.collectAsState()
    val currentUser by viewModel.currentUser.collectAsState()
    var showAuthDialog by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        MaterialTheme.colorScheme.background,
                        MaterialTheme.colorScheme.background,
                        VibrantPurple.copy(alpha = 0.05f)
                    )
                )
            )
            .padding(20.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Text(
            text = "Settings",
            style = MaterialTheme.typography.headlineLarge,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 24.dp)
        )

        // Dark Mode Setting
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .border(
                    width = 1.dp,
                    color = if (isDarkMode) VibrantIndigo.copy(alpha = 0.3f) else Color.Transparent,
                    shape = RoundedCornerShape(20.dp)
                ),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(
                containerColor = if (isDarkMode)
                    VibrantIndigo.copy(alpha = 0.12f)
                else
                    MaterialTheme.colorScheme.surface
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .clip(CircleShape)
                            .background(
                                brush = Brush.linearGradient(
                                    colors = listOf(VibrantIndigo, VibrantPurple)
                                )
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = if (isDarkMode) "🌙" else "☀️",
                            style = MaterialTheme.typography.headlineSmall
                        )
                    }
                    Spacer(modifier = Modifier.width(16.dp))
                    Column {
                        Text(
                            text = "Dark Mode",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold
                        )
                        Text(
                            text = if (isDarkMode) "Enabled" else "Disabled",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                        )
                    }
                }
                Switch(
                    checked = isDarkMode,
                    onCheckedChange = { viewModel.toggleDarkMode() },
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = Color.White,
                        checkedTrackColor = VibrantIndigo,
                        uncheckedThumbColor = Color.White,
                        uncheckedTrackColor = MaterialTheme.colorScheme.outline
                    )
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Account Section
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .border(
                    width = 1.5.dp,
                    brush = Brush.linearGradient(
                        colors = listOf(VibrantBlue.copy(alpha = 0.5f), VibrantPurple.copy(alpha = 0.5f))
                    ),
                    shape = RoundedCornerShape(20.dp)
                ),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(52.dp)
                            .clip(CircleShape)
                            .background(
                                brush = Brush.linearGradient(
                                    colors = listOf(VibrantBlue, VibrantPurple)
                                )
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Person,
                            contentDescription = "Account",
                            tint = Color.White,
                            modifier = Modifier.size(28.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(16.dp))
                    Column {
                        Text(
                            text = if (currentUser != null) "Account" else "Get Started",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = if (currentUser != null) currentUser!! else "Sign in to sync your counters",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                        )
                    }
                }

                if (currentUser == null) {
                    Spacer(modifier = Modifier.height(20.dp))

                    // Sign In Button
                    Button(
                        onClick = { showAuthDialog = true },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp),
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color.Transparent
                        ),
                        contentPadding = PaddingValues(0.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(
                                    brush = Brush.horizontalGradient(
                                        colors = listOf(VibrantBlue, VibrantPurple)
                                    )
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.Center
                            ) {
                                Text(
                                    text = "🔐",
                                    style = MaterialTheme.typography.titleLarge
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "Sign In / Sign Up",
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 16.sp
                                )
                            }
                        }
                    }
                } else {
                    Spacer(modifier = Modifier.height(16.dp))
                    OutlinedButton(
                        onClick = { viewModel.signOut() },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = AppleRed
                        )
                    ) {
                        Text("Sign Out", fontWeight = FontWeight.Medium)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // App Info - Clickable to navigate to About screen
        Card(
            onClick = onAboutClick,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.primary),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Filled.Info,
                        contentDescription = "App Info",
                        tint = MaterialTheme.colorScheme.onPrimary,
                        modifier = Modifier.size(24.dp)
                    )
                }
                Spacer(modifier = Modifier.width(16.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "About Counturu",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        text = "Version 1.0 • Tap to learn more",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }

    // Auth Dialog
    if (showAuthDialog) {
        AuthScreen(
            onLoginSuccess = { _, username ->
                viewModel.setCurrentUser(username)
                showAuthDialog = false
            }
        )
    }
}

