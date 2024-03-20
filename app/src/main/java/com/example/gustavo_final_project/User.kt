package com.example.gustavo_final_project

import android.content.Context
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.Serializable

data class User(
    val firstName: String,
    val lastName: String,
    val dateOfBirth: String,
    val country: String,
    val email: String,
    val password: String
) : Serializable {
    companion object {
        var registeredUsers = mutableListOf<User>()
    }
}


fun saveRegisteredUsers(context: Context, users: List<Any?>) {
    val sharedPreferences = context.getSharedPreferences("registered_users", Context.MODE_PRIVATE)
    val editor = sharedPreferences.edit()
    val gson = Gson()
    val json = gson.toJson(users)
    editor.putString("users", json)
    editor.apply()
}

fun loadRegisteredUsers(context: Context): List<User> {
    val sharedPreferences = context.getSharedPreferences("registered_users", Context.MODE_PRIVATE)
    val gson = Gson()
    val json = sharedPreferences.getString("users", "")
    val type = object : TypeToken<List<User>>() {}.type
    return gson.fromJson(json, type) ?: emptyList()
}


//fun printRegisteredUsers() {
//    println("List of Registered Users:")
//    User.registeredUsers.forEachIndexed { index, user ->
//        println("User ${index + 1}:")
//        println("First Name: ${user.firstName}")
//        println("Last Name: ${user.lastName}")
//        println("Date of Birth: ${user.dateOfBirth}")
//        println("Country: ${user.country}")
//        println("Email: ${user.email}")
//        println("Password: ${user.password}")
//        println()
//    }
//}
