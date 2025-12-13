package com.example.counturu.ui.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.counturu.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AuthScreen(
    onLoginSuccess: (String, String) -> Unit
) {
    var isSignUp by remember { mutableStateOf(false) }
    var username by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    val focusManager = LocalFocusManager.current

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.linearGradient(
                    colors = listOf(
                        GradientStart.copy(alpha = 0.3f),
                        GradientMiddle.copy(alpha = 0.2f),
                        GradientEnd.copy(alpha = 0.3f)
                    )
                )
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Logo and Title
            Text(
                text = "Counturu",
                fontSize = 48.sp,
                fontWeight = FontWeight.Bold,
                color = VibrantPurple,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            Text(
                text = "Count Your Special Moments",
                fontSize = 16.sp,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(bottom = 48.dp)
            )

            // Glass Card
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(
                        width = 1.dp,
                        color = GlassBorderLight,
                        shape = RoundedCornerShape(32.dp)
                    ),
                shape = RoundedCornerShape(32.dp),
                colors = CardDefaults.cardColors(
                    containerColor = GlassCardLight
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 16.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(32.dp)
                ) {
                    // Toggle Buttons
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        TextButton(
                            onClick = { isSignUp = false },
                            modifier = Modifier.weight(1f)
                        ) {
                            Text(
                                text = "Sign In",
                                fontSize = 18.sp,
                                fontWeight = if (!isSignUp) FontWeight.Bold else FontWeight.Normal,
                                color = if (!isSignUp) VibrantBlue else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                            )
                        }

                        TextButton(
                            onClick = { isSignUp = true },
                            modifier = Modifier.weight(1f)
                        ) {
                            Text(
                                text = "Sign Up",
                                fontSize = 18.sp,
                                fontWeight = if (isSignUp) FontWeight.Bold else FontWeight.Normal,
                                color = if (isSignUp) VibrantPurple else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    // Username Field (Sign Up only)
                    AnimatedVisibility(
                        visible = isSignUp,
                        enter = fadeIn() + expandVertically(),
                        exit = fadeOut() + shrinkVertically()
                    ) {
                        Column {
                            OutlinedTextField(
                                value = username,
                                onValueChange = { username = it },
                                label = { Text("Username") },
                                leadingIcon = {
                                    Icon(Icons.Filled.Person, "Username")
                                },
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(16.dp),
                                singleLine = true,
                                keyboardOptions = KeyboardOptions(
                                    imeAction = ImeAction.Next
                                ),
                                keyboardActions = KeyboardActions(
                                    onNext = { focusManager.moveFocus(FocusDirection.Down) }
                                )
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                        }
                    }

                    // Email Field
                    OutlinedTextField(
                        value = email,
                        onValueChange = { email = it },
                        label = { Text("Email") },
                        leadingIcon = {
                            Icon(Icons.Filled.Email, "Email")
                        },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Email,
                            imeAction = ImeAction.Next
                        ),
                        keyboardActions = KeyboardActions(
                            onNext = { focusManager.moveFocus(FocusDirection.Down) }
                        )
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Password Field
                    OutlinedTextField(
                        value = password,
                        onValueChange = { password = it },
                        label = { Text("Password") },
                        leadingIcon = {
                            Icon(Icons.Filled.Lock, "Password")
                        },
                        trailingIcon = {
                            IconButton(onClick = { passwordVisible = !passwordVisible }) {
                                Text(
                                    text = if (passwordVisible) "👁️" else "🔒",
                                    style = MaterialTheme.typography.titleMedium
                                )
                            }
                        },
                        visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Password,
                            imeAction = if (isSignUp) ImeAction.Next else ImeAction.Done
                        ),
                        keyboardActions = KeyboardActions(
                            onNext = { focusManager.moveFocus(FocusDirection.Down) },
                            onDone = { focusManager.clearFocus() }
                        )
                    )

                    // Confirm Password (Sign Up only)
                    AnimatedVisibility(
                        visible = isSignUp,
                        enter = fadeIn() + expandVertically(),
                        exit = fadeOut() + shrinkVertically()
                    ) {
                        Column {
                            Spacer(modifier = Modifier.height(16.dp))
                            OutlinedTextField(
                                value = confirmPassword,
                                onValueChange = { confirmPassword = it },
                                label = { Text("Confirm Password") },
                                leadingIcon = {
                                    Icon(Icons.Filled.Lock, "Confirm Password")
                                },
                                visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(16.dp),
                                singleLine = true,
                                keyboardOptions = KeyboardOptions(
                                    keyboardType = KeyboardType.Password,
                                    imeAction = ImeAction.Done
                                ),
                                keyboardActions = KeyboardActions(
                                    onDone = { focusManager.clearFocus() }
                                )
                            )
                        }
                    }

                    // Error Message
                    errorMessage?.let { error ->
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = error,
                            color = AppleRed,
                            fontSize = 14.sp,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }

                    Spacer(modifier = Modifier.height(32.dp))

                    // Submit Button
                    Button(
                        onClick = {
                            errorMessage = when {
                                email.isBlank() -> "Please enter your email"
                                password.isBlank() -> "Please enter your password"
                                isSignUp && username.isBlank() -> "Please enter a username"
                                isSignUp && password != confirmPassword -> "Passwords don't match"
                                isSignUp && password.length < 6 -> "Password must be at least 6 characters"
                                else -> {
                                    onLoginSuccess(email, if (isSignUp) username else email)
                                    null
                                }
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp),
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (isSignUp) VibrantPurple else VibrantBlue
                        )
                    ) {
                        Text(
                            text = if (isSignUp) "Create Account" else "Sign In",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}

