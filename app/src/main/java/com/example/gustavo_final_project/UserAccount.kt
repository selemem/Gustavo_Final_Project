package com.example.gustavo_final_project

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user_accounts")
data class UserAccount(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val firstName: String,
    val lastName: String,
    val dateOfBirth: String,
    val country: String,
    val email: String,
    val password: String
)
