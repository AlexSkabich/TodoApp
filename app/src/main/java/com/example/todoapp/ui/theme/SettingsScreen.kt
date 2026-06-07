package com.example.todoapp.ui

import android.graphics.Color as AndroidColor
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.todoapp.viewmodel.SettingsViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    viewModel: SettingsViewModel = hiltViewModel(),
    onBack: () -> Unit
) {
    val currentText by viewModel.currentTextColor.collectAsStateWithLifecycle()
    val currentBg by viewModel.currentBgColor.collectAsStateWithLifecycle()
    val currentFont by viewModel.currentFontFamily.collectAsStateWithLifecycle()

    var textColorHex by remember(currentText) { mutableStateOf(currentText) }
    var bgColorHex by remember(currentBg) { mutableStateOf(currentBg) }
    var selectedFont by remember(currentFont) { mutableStateOf(currentFont) }

    var expandedFont by remember { mutableStateOf(false) }

    // Sync state if reset is triggered
    LaunchedEffect(currentText, currentBg, currentFont) {
        textColorHex = currentText
        bgColorHex = currentBg
        selectedFont = currentFont
    }

    val isTextValid = viewModel.isValidHex(textColorHex)
    val isBgValid = viewModel.isValidHex(bgColorHex)
    val isSaveEnabled = isTextValid && isBgValid

    val fontOptions = listOf(
        "DEFAULT" to "Default",
        "MONOSPACE" to "Monospace",
        "SERIF" to "Serif",
        "SANS_SERIF_CONDENSED" to "Condensed"
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Settings", style = MaterialTheme.typography.headlineMedium)
            TextButton(onClick = onBack) { Text("Back") }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // 1. Text Color Hex Input
        OutlinedTextField(
            value = textColorHex,
            onValueChange = { textColorHex = it },
            label = { Text("Text Color Hex (e.g., #000000)") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            isError = !isTextValid && textColorHex.isNotEmpty(),
            supportingText = {
                if (!isTextValid && textColorHex.isNotEmpty()) {
                    Text("Invalid hex code. Use #RRGGBB or #AARRGGBB")
                }
            },
            trailingIcon = {
                // Color Preview Box
                val previewColor = try {
                    Color(AndroidColor.parseColor(if (textColorHex.startsWith("#")) textColorHex else "#$textColorHex"))
                } catch (e: Exception) { Color.Transparent }

                Box(
                    modifier = Modifier
                        .size(24.dp)
                        .background(previewColor, CircleShape)
                        .border(1.dp, MaterialTheme.colorScheme.onSurfaceVariant, CircleShape)
                )
            }
        )

        Spacer(modifier = Modifier.height(16.dp))

        // 2. Background Color Hex Input
        OutlinedTextField(
            value = bgColorHex,
            onValueChange = { bgColorHex = it },
            label = { Text("Background Color Hex (e.g., #FFFFFF)") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            isError = !isBgValid && bgColorHex.isNotEmpty(),
            supportingText = {
                if (!isBgValid && bgColorHex.isNotEmpty()) {
                    Text("Invalid hex code. Use #RRGGBB or #AARRGGBB")
                }
            },
            trailingIcon = {
                // Color Preview Box
                val previewColor = try {
                    Color(AndroidColor.parseColor(if (bgColorHex.startsWith("#")) bgColorHex else "#$bgColorHex"))
                } catch (e: Exception) { Color.Transparent }

                Box(
                    modifier = Modifier
                        .size(24.dp)
                        .background(previewColor, CircleShape)
                        .border(1.dp, MaterialTheme.colorScheme.onSurfaceVariant, CircleShape)
                )
            }
        )

        Spacer(modifier = Modifier.height(16.dp))

        // 3. Font Dropdown (Kept as dropdown for simplicity)
        ExposedDropdownMenuBox(
            expanded = expandedFont,
            onExpandedChange = { expandedFont = !expandedFont }
        ) {
            OutlinedTextField(
                value = fontOptions.find { it.first == selectedFont }?.second ?: "Default",
                onValueChange = {},
                readOnly = true,
                label = { Text("Font Style") },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedFont) },
                modifier = Modifier.fillMaxWidth().menuAnchor()
            )
            ExposedDropdownMenu(
                expanded = expandedFont,
                onDismissRequest = { expandedFont = false }
            ) {
                fontOptions.forEach { (value, label) ->
                    DropdownMenuItem(
                        text = { Text(label) },
                        onClick = {
                            selectedFont = value
                            expandedFont = false
                        }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.weight(1f))

        // Action Buttons
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            OutlinedButton(
                onClick = { viewModel.resetToDefault() },
                modifier = Modifier.weight(1f)
            ) {
                Text("Reset to Default")
            }

            Button(
                onClick = {
                    viewModel.saveSettings(textColorHex, bgColorHex, selectedFont)
                    onBack()
                },
                modifier = Modifier.weight(1f),
                enabled = isSaveEnabled // Disabled if hex codes are invalid
            ) {
                Text("Save Settings")
            }
        }
    }
}