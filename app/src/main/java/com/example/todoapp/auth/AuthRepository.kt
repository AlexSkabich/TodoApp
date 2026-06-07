package com.example.todoapp.auth

import com.example.todoapp.data.TodoDao
import com.example.todoapp.data.User
import com.example.todoapp.data.UserDao
import com.example.todoapp.utils.SessionManager 
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthRepository @Inject constructor(
    private val userDao: UserDao,
    private val todoDao: TodoDao,
    private val sessionManager: SessionManager 
)  {
    val currentUserId: Flow<String?> = sessionManager.userIdFlow
    val currentUserEmail: Flow<String?> = sessionManager.userEmailFlow

    suspend fun register(email: String, password: String): Result<Unit> {
        return try {
            if (userDao.getUserByEmail(email) != null) {
                return Result.failure(Exception("Email already registered"))
            }
            val user = User(email = email, passwordHash = PasswordUtils.hashPassword(password))
            userDao.insertUser(user)
            sessionManager.saveSession(user.id, user.email)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun login(email: String, password: String): Result<Unit> {
        return try {
            val user = userDao.getUserByEmail(email)
            val hashedInput = PasswordUtils.hashPassword(password)

            if (user == null || user.passwordHash != hashedInput) {
                return Result.failure(Exception("Invalid email or password"))
            }
            sessionManager.saveSession(user.id, user.email)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun logout() {
        // FIX: Just clear the session. DO NOT wipe the database, or notes will be lost!
        sessionManager.clearSession()
    }
}
