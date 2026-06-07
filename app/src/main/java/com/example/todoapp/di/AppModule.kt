package com.example.todoapp.di

import android.content.Context
import androidx.room.Room
import com.example.todoapp.data.AppDatabase
import com.example.todoapp.data.TodoDao
import com.example.todoapp.data.UserDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "todo_database"
        ).fallbackToDestructiveMigration() // Resets DB safely on schema change
            .build()
    }

    @Provides
    fun provideUserDao(database: AppDatabase): UserDao = database.userDao()

    @Provides
    fun provideTodoDao(database: AppDatabase): TodoDao = database.todoDao()
}