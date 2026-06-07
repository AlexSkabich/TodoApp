package com.example.todoapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.todoapp.auth.AuthViewModel
import com.example.todoapp.ui.AuthScreen
import com.example.todoapp.ui.SettingsScreen
import com.example.todoapp.ui.TodoScreen
import com.example.todoapp.viewmodel.SettingsViewModel
import dagger.hilt.android.AndroidEntryPoint

enum class AppScreen { TODO, SETTINGS }

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            // We inject SettingsViewModel at the root to apply theme globally
            val settingsViewModel: SettingsViewModel = hiltViewModel()
            val themeConfig by settingsViewModel.themeConfig.collectAsStateWithLifecycle()

            // Apply dynamic theme
            MaterialTheme(
                colorScheme = lightColorScheme(
                    primary = themeConfig.textColor,
                    onPrimary = themeConfig.backgroundColor,
                    background = themeConfig.backgroundColor,
                    surface = themeConfig.backgroundColor,
                    onSurface = themeConfig.textColor,
                    onBackground = themeConfig.textColor
                ),

            ) {
                Surface(modifier = Modifier.fillMaxSize()) {
                    AppRoot(settingsViewModel = settingsViewModel)
                }
            }
        }
    }
}

@Composable
fun AppRoot(
    authViewModel: AuthViewModel = hiltViewModel(),
    settingsViewModel: SettingsViewModel = hiltViewModel()
) {
    val isLoggedIn by authViewModel.isLoggedIn.collectAsStateWithLifecycle()
    var currentScreen by remember { mutableStateOf(AppScreen.TODO) }

    if (!isLoggedIn) {
        // Force back to TODO screen if logged out
        currentScreen = AppScreen.TODO
        AuthScreen(viewModel = authViewModel)
    } else {
        when (currentScreen) {
            AppScreen.TODO -> TodoScreen(
                onSignOut = { authViewModel.logout() },
                onSettingsClick = { currentScreen = AppScreen.SETTINGS }
            )
            AppScreen.SETTINGS -> SettingsScreen(
                onBack = { currentScreen = AppScreen.TODO }
            )
        }
    }
}