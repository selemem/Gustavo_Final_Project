package com.example.gustavo_final_project

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.gustavo_final_project.User.Companion.registeredUsers
import com.example.gustavo_final_project.ui.theme.Gustavo_Final_ProjectTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val context = applicationContext // Use application context to ensure it's available everywhere
        User.registeredUsers =
            loadRegisteredUsers(context).toMutableList() // Load registered users when the app starts
        setContent {
            LoginPage(
                onLoginSuccess = { navigateToHomePage() },
                onCreateAccountClicked = { navigateToCreateAccount() }
            )
        }
    }

    private fun navigateToHomePage() {
        val intent = Intent(this, HomePageActivity::class.java)
        startActivity(intent)
    }

    private fun navigateToCreateAccount() {
        val intent = Intent(this, CreateAccountActivity::class.java)
        startActivity(intent)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginPage(onLoginSuccess: () -> Unit, onCreateAccountClicked: () -> Unit) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    Surface(color = Color.White) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = "Login with Email and Password")
            Spacer(modifier = Modifier.height(16.dp))
            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Email") }
            )
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Password") },
                visualTransformation = PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password)
            )
            Spacer(modifier = Modifier.height(16.dp))
            Button(onClick = {
                val user = registeredUsers.find { it.email == email && it.password == password }
                if (user != null) {
                    onLoginSuccess()
                } else {
                    // Handle login failure (e.g., display error message)
                }
            }) {
                Text("Login")
            }
            Spacer(modifier = Modifier.height(16.dp))
            TextButton(onClick = onCreateAccountClicked) {
                Text("Start a new account here")
            }
        }
    }
}

@Preview
@Composable
fun PreviewLoginPage() {
    LoginPage(onLoginSuccess = {}, onCreateAccountClicked = {})
}





