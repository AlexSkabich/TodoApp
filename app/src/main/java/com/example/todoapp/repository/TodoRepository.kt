package com.example.todoapp.repository

import com.example.todoapp.data.TodoDao
import com.example.todoapp.data.TodoItem
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class TodoRepository @Inject constructor(
    private val todoDao: TodoDao
) {


    suspend fun insert(todo: TodoItem) = todoDao.insertTodo(todo)
    fun getAllTodos(userId: String): Flow<List<TodoItem>> = todoDao.getAllTodos(userId)
    suspend fun update(todo: TodoItem) = todoDao.updateTodo(todo)
    suspend fun delete(todo: TodoItem) = todoDao.deleteTodo(todo)
}