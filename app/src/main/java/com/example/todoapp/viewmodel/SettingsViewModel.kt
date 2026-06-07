package com.example.todoapp.viewmodel

import android.graphics.Color as AndroidColor
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.todoapp.utils.SessionManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ThemeConfig(
    val textColor: Color,
    val backgroundColor: Color,
    val fontFamily: FontFamily
)

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val sessionManager: SessionManager
) : ViewModel() {

    val currentTextColor = sessionManager.textColorFlow
        .stateIn(viewModelScope, SharingStarted.Eagerly, "#000000")

    val currentBgColor = sessionManager.bgColorFlow
        .stateIn(viewModelScope, SharingStarted.Eagerly, "#F5F5F5")

    val currentFontFamily = sessionManager.fontFamilyFlow
        .stateIn(viewModelScope, SharingStarted.Eagerly, "DEFAULT")

    val themeConfig = combine(
        currentTextColor,
        currentBgColor,
        currentFontFamily
    ) { text, bg, font ->
        ThemeConfig(
            textColor = parseColor(text, default = Color.Black),
            backgroundColor = parseColor(bg, default = Color(0xFFF5F5F5)),
            fontFamily = getFont(font)
        )
    }.stateIn(viewModelScope, SharingStarted.Eagerly, ThemeConfig(Color.Black, Color(0xFFF5F5F5), FontFamily.Default))

    fun saveSettings(textColor: String, bgColor: String, fontFamily: String) {
        viewModelScope.launch {
            sessionManager.saveSettings(textColor, bgColor, fontFamily)
        }
    }

    fun resetToDefault() {
        viewModelScope.launch {
            sessionManager.saveSettings("#000000", "#F5F5F5", "DEFAULT")
        }
    }

    // Helper to validate hex color in the UI
    fun isValidHex(hex: String): Boolean {
        if (hex.isBlank()) return false
        val regex = "^#?([A-Fa-f0-9]{6}|[A-Fa-f0-9]{8})$".toRegex()
        return regex.matches(hex)
    }

    private fun parseColor(hex: String, default: Color): Color {
        return try {
            val cleanHex = if (hex.startsWith("#")) hex else "#$hex"
            Color(AndroidColor.parseColor(cleanHex))
        } catch (e: IllegalArgumentException) {
            default // Fallback if hex is invalid
        }
    }

    private fun getFont(value: String): FontFamily = when (value) {
        "DEFAULT" -> FontFamily.Default
        "MONOSPACE" -> FontFamily.Monospace
        "SERIF" -> FontFamily.Serif
        "SANS_SERIF_CONDENSED" -> FontFamily.SansSerif
        else -> FontFamily.Default
    }
}