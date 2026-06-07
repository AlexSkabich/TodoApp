package com.example.todoapp.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogProperties
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.todoapp.data.TodoItem
import com.example.todoapp.viewmodel.TodoViewModel

@Composable
fun TodoScreen(
    viewModel: TodoViewModel = hiltViewModel(),
    onSignOut: () -> Unit,
    onSettingsClick: () -> Unit
) {
    val todos by viewModel.todos.collectAsStateWithLifecycle()
    var newTodoText by remember { mutableStateOf("") }
    val customTextColor = MaterialTheme.colorScheme.onSurface

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // 1. Header & Sign Out
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "My Todos",
                style = MaterialTheme.typography.headlineMedium,
                color = customTextColor
            )
            TextButton(onClick = onSignOut) {
                Text(
                    text = "Sign Out",
                    color = customTextColor,
                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold)
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // 2. Input Row
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                value = newTodoText,
                onValueChange = { newTodoText = it },
                label = { Text("Add a new task", color = customTextColor) },
                modifier = Modifier.weight(1f),
                singleLine = true,
                shape = MaterialTheme.shapes.small,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = customTextColor,
                    unfocusedTextColor = customTextColor,
                    focusedLabelColor = customTextColor,
                    unfocusedLabelColor = customTextColor.copy(alpha = 0.7f)
                )
            )

            Spacer(modifier = Modifier.width(8.dp))

            IconButton(
                onClick = {
                    if (newTodoText.isNotBlank()) {
                        viewModel.addTodo(newTodoText.trim())
                        newTodoText = ""
                    }
                },
                enabled = newTodoText.isNotBlank(),
                modifier = Modifier.size(48.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Add Todo",
                    tint = if (newTodoText.isNotBlank()) customTextColor else customTextColor.copy(alpha = 0.38f)
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // 3. Todo List
        if (todos.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxWidth().weight(1f),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "No tasks yet. Add one above!",
                    style = MaterialTheme.typography.bodyLarge,
                    color = customTextColor
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(
                    items = todos,
                    key = { it.id }
                ) { todo ->
                    TodoItemRow(
                        todo = todo,
                        onToggle = { viewModel.toggleTodo(todo) },
                        onDelete = { viewModel.deleteTodo(todo) },
                        onEdit = { newTitle -> viewModel.updateTodoTitle(todo, newTitle) },
                        customTextColor = customTextColor
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // 4. Settings Button
        OutlinedButton(
            onClick = onSettingsClick,
            modifier = Modifier.fillMaxWidth(),
            shape = MaterialTheme.shapes.small,
            colors = ButtonDefaults.outlinedButtonColors(contentColor = customTextColor)
        ) {
            Icon(
                imageVector = Icons.Default.Settings,
                contentDescription = null,
                modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text("Settings")
        }
    }
}

@Composable
fun TodoItemRow(
    todo: TodoItem,
    onToggle: () -> Unit,
    onDelete: () -> Unit,
    onEdit: (String) -> Unit,
    customTextColor: Color
) {
    var isEditing by remember { mutableStateOf(false) }

    // Show Dialog when editing is triggered
    if (isEditing) {
        EditTodoDialog(
            currentTitle = todo.title,
            onDismiss = { isEditing = false },
            onSave = { newTitle ->
                onEdit(newTitle)
                isEditing = false
            },
            customTextColor = customTextColor
        )
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
        ),
        shape = MaterialTheme.shapes.small
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Checkbox(
                checked = todo.isCompleted,
                onCheckedChange = { onToggle() },
                colors = CheckboxDefaults.colors(
                    checkedColor = customTextColor,
                    uncheckedColor = customTextColor.copy(alpha = 0.6f)
                )
            )

            Text(
                text = todo.title,
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 8.dp),
                textDecoration = if (todo.isCompleted) TextDecoration.LineThrough else TextDecoration.None,
                style = MaterialTheme.typography.bodyLarge,
                color = if (todo.isCompleted) customTextColor.copy(alpha = 0.5f) else customTextColor
            )

            // Edit Button
            IconButton(
                onClick = { isEditing = true },
                modifier = Modifier.size(36.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Edit,
                    contentDescription = "Edit Todo",
                    tint = customTextColor.copy(alpha = 0.7f)
                )
            }

            // Delete Button
            IconButton(
                onClick = onDelete,
                modifier = Modifier.size(36.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Delete Todo",
                    tint = MaterialTheme.colorScheme.error
                )
            }
        }
    }
}

@Composable
fun EditTodoDialog(
    currentTitle: String,
    onDismiss: () -> Unit,
    onSave: (String) -> Unit,
    customTextColor: Color
) {
    var newTitle by remember { mutableStateOf(currentTitle) }

    AlertDialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(dismissOnBackPress = true, dismissOnClickOutside = true),
        title = {
            Text("Edit Task", color = customTextColor, style = MaterialTheme.typography.titleLarge)
        },
        text = {
            OutlinedTextField(
                value = newTitle,
                onValueChange = { newTitle = it },
                label = { Text("Task title", color = customTextColor) },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = customTextColor,
                    unfocusedTextColor = customTextColor,
                    focusedLabelColor = customTextColor,
                    unfocusedLabelColor = customTextColor.copy(alpha = 0.7f),
                    focusedContainerColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent
                )
            )
        },
        confirmButton = {
            TextButton(
                onClick = { if (newTitle.isNotBlank()) onSave(newTitle.trim()) },
                enabled = newTitle.isNotBlank()
            ) {
                Text("Save", color = customTextColor, fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel", color = customTextColor)
            }
        }
    )
}