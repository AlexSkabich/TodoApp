package com.example.todoapp.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class User(
    @PrimaryKey val id: String = java.util.UUID.randomUUID().toString(),
    val email: String,
    val passwordHash: String // Never store plain text passwords
)