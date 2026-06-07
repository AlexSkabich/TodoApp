package com.example.todoapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.todoapp.auth.AuthRepository
import com.example.todoapp.data.TodoItem
import com.example.todoapp.repository.TodoRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class TodoViewModel @Inject constructor(
    private val repository: TodoRepository,
    private val authRepository: AuthRepository
) : ViewModel() {

    val todos = authRepository.currentUserId.flatMapLatest { userId ->
        if (userId != null) {
            repository.getAllTodos(userId)
        } else {
            flowOf(emptyList())
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    fun addTodo(title: String) {
        viewModelScope.launch {
            val userId = authRepository.currentUserId.first() ?: return@launch
            repository.insert(TodoItem(title = title, userId = userId))
        }
    }

    // NEW: Function to update an existing task's title
    fun updateTodoTitle(todo: TodoItem, newTitle: String) {
        if (newTitle.isNotBlank()) {
            viewModelScope.launch {
                repository.update(todo.copy(title = newTitle.trim()))
            }
        }
    }

    fun toggleTodo(todo: TodoItem) {
        viewModelScope.launch {
            repository.update(todo.copy(isCompleted = !todo.isCompleted))
        }
    }

    fun deleteTodo(todo: TodoItem) {
        viewModelScope.launch {
            repository.delete(todo)
        }
    }
}