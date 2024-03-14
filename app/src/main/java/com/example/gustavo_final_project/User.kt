package com.example.gustavo_final_project

data class User(
    val firstName: String,
    val lastName: String,
    val dateOfBirth: String,
    val country: String,
    val email: String,
    val password: String
) {
    companion object {
        val registeredUsers = mutableListOf<User>()
    }
}