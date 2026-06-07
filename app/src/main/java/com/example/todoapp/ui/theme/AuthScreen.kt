package com.example.todoapp.ui

import android.util.Patterns
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.todoapp.auth.AuthViewModel

@Composable
fun AuthScreen(
    viewModel: AuthViewModel = hiltViewModel()
) {
    var isLogin by remember { mutableStateOf(true) }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    val authError by viewModel.authError.collectAsStateWithLifecycle()

    // 1. Trim the email first to ignore accidental leading/trailing spaces
    val trimmedEmail = email.trim()

    // 2. Validate the trimmed email
    val isEmailValid = Patterns.EMAIL_ADDRESS.matcher(trimmedEmail).matches()

    // 3. Form is valid ONLY if email is valid AND password is >= 6 chars
    val isFormValid = isEmailValid && password.length >= 6

    // Clear backend errors when user starts typing or switches modes
    val clearError = {
        if (authError != null) viewModel.clearError()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = if (isLogin) "Welcome Back" else "Create Account",
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.primary
        )

        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = if (isLogin) "Sign in to continue to your todos" else "Register to start managing your tasks",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(32.dp))

        // Email Input
        OutlinedTextField(
            value = email,
            onValueChange = {
                email = it
                clearError()
            },
            label = { Text("Email") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            // Show red border ONLY if it's not empty AND not a valid email
            isError = trimmedEmail.isNotBlank() && !isEmailValid,
            supportingText = {
                if (trimmedEmail.isNotBlank() && !isEmailValid) {
                    Text(
                        text = "Please enter a valid email (e.g., user@example.com)",
                        color = MaterialTheme.colorScheme.error
                    )
                }
            },
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Email,
                imeAction = ImeAction.Next
            )
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Password Input
        OutlinedTextField(
            value = password,
            onValueChange = {
                password = it
                clearError()
            },
            label = { Text("Password (min. 6 characters)") }, // <-- Added hint
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            visualTransformation = PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Password,
                imeAction = ImeAction.Done
            )
        )

        // Auth Error Message (e.g., "Email already registered" or "Invalid password")
        if (authError != null) {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = authError!!,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Submit Button
        Button(
            onClick = {
                if (isFormValid) {
                    if (isLogin) {
                        viewModel.login(trimmedEmail, password)
                    } else {
                        viewModel.register(trimmedEmail, password)
                    }
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),
            enabled = isFormValid // Stays disabled until BOTH email and password are valid
        ) {
            Text(
                text = if (isLogin) "Sign In" else "Register",
                style = MaterialTheme.typography.titleMedium
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Toggle Login/Register
        TextButton(
            onClick = {
                isLogin = !isLogin
                clearError()
                password = "" // Clear password on mode switch for security
            }
        ) {
            Text(
                text = if (isLogin) "Don't have an account? Register" else "Already have an account? Sign In",
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}