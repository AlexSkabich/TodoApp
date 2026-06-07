package com.example.todoapp.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val repository: AuthRepository
) : ViewModel() {

    private val _isLoggedIn = MutableStateFlow(false)
    val isLoggedIn: StateFlow<Boolean> = _isLoggedIn.asStateFlow()

    private val _authError = MutableStateFlow<String?>(null)
    val authError: StateFlow<String?> = _authError.asStateFlow()

    init {
        viewModelScope.launch {
            repository.currentUserId.collect { userId ->
                _isLoggedIn.value = userId != null
            }
        }
    }

    fun login(email: String, password: String) {
        viewModelScope.launch {
            repository.login(email, password).fold(
                onSuccess = { _authError.value = null },
                onFailure = { _authError.value = it.message ?: "Login failed" }
            )
        }
    }

    fun register(email: String, password: String) {
        viewModelScope.launch {
            repository.register(email, password).fold(
                onSuccess = { _authError.value = null },
                onFailure = { _authError.value = it.message ?: "Registration failed" }
            )
        }
    }

    fun logout() {
        viewModelScope.launch {
            repository.logout()
        }
    }

    fun clearError() {
        _authError.value = null
    }
}