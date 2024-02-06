package com.example.gustavo_final_project

import android.content.Context
import androidx.room.Room

class DatabaseHelper private constructor(context: Context) {

    private val database: AppDatabase = Room.databaseBuilder(
        context.applicationContext,
        AppDatabase::class.java, "app_database"
    ).build()

    companion object {
        @Volatile private var instance: DatabaseHelper? = null

        fun getInstance(context: Context) =
            instance ?: synchronized(this) {
                instance ?: DatabaseHelper(context).also { instance = it }
            }
    }

    suspend fun insertUserAccount(userAccount: UserAccount) {
        database.userAccountDao().insertUserAccount(userAccount)
    }
}