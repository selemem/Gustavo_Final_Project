package com.example.gustavo_final_project
import android.content.Intent
import android.os.Bundle

import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.gustavo_final_project.User.Companion.registeredUsers

class CreateAccountActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            CreateAccountContent(onAccountCreated = { navigateToLoginPage() })
        }
    }

    private fun navigateToLoginPage() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
    }

}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateAccountContent(onAccountCreated: () -> Unit) {
    var firstName by remember { mutableStateOf("") }
    var lastName by remember { mutableStateOf("") }
    var dateOfBirth by remember { mutableStateOf("") }
    var country by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var isEmailValid by remember { mutableStateOf(true) }
    var isPasswordValid by remember { mutableStateOf(true) }
    var passwordErrorMessage by remember { mutableStateOf("") } // Track password error message
    var emailErrorMessage by remember { mutableStateOf("") } // Track email error message

    Surface(color = Color.White) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = "Create an Account")
            Spacer(modifier = Modifier.height(16.dp))
            TextField(
                value = firstName,
                onValueChange = { firstName = it },
                label = { Text("First Name") }
            )
            Spacer(modifier = Modifier.height(8.dp))
            TextField(
                value = lastName,
                onValueChange = { lastName = it },
                label = { Text("Last Name") }
            )
            Spacer(modifier = Modifier.height(8.dp))
            TextField(
                value = dateOfBirth,
                onValueChange = { dateOfBirth = it },
                label = { Text("Date of Birth") }
            )
            Spacer(modifier = Modifier.height(8.dp))
            TextField(
                value = country,
                onValueChange = { country = it },
                label = { Text("Country") }
            )
            Spacer(modifier = Modifier.height(8.dp))
            TextField(
                value = email,
                onValueChange = {
                    email = it
                    isEmailValid = android.util.Patterns.EMAIL_ADDRESS.matcher(it).matches()
                    emailErrorMessage = if (!isEmailValid) {
                        "Please enter a valid email address"
                    } else {
                        ""
                    }
                },
                label = { Text("Email") },
                isError = !isEmailValid,
                singleLine = true
            )
            if (emailErrorMessage.isNotEmpty()) {
                Text(
                    text = emailErrorMessage,
                    color = Color.Red,
                    fontSize = 12.sp,
                    modifier = Modifier.padding(start = 8.dp) // Adjust padding as needed
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            TextField(
                value = password,
                onValueChange = {
                    password = it
                    val isValid = isPasswordValid(it)
                    isPasswordValid = isValid
                    passwordErrorMessage = if (!isValid) {
                        "Password must be at least $MIN_PASSWORD_LENGTH characters long and contain at least one number and one special character"
                    } else {
                        ""
                    }
                },
                label = { Text("Password") },
                visualTransformation = PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                isError = !isPasswordValid,
                singleLine = true
            )
            if (passwordErrorMessage.isNotEmpty()) {
                Text(
                    text = passwordErrorMessage,
                    color = Color.Red,
                    fontSize = 12.sp,
                    modifier = Modifier.padding(start = 8.dp) // Adjust padding as needed
                )
            }
            Spacer(modifier = Modifier.height(16.dp))
            Row(
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Button(onClick = {
                    if (isEmailValid && isPasswordValid) {
                        val newUser = User(firstName, lastName, dateOfBirth, country, email, password)
                        registeredUsers.add(newUser)
                        onAccountCreated()
                    }
                }) {
                    Text("Create Account")
                }
                Spacer(modifier = Modifier.width(8.dp))
                Button(onClick = onAccountCreated) {
                    Text("Cancel")
                }
            }
        }
    }
}


private const val MIN_PASSWORD_LENGTH = 8

private fun isPasswordValid(password: String): Boolean {
    if (password.length < MIN_PASSWORD_LENGTH) return false

    val containsNumber = password.any { it.isDigit() }
    val containsSpecialChar = password.any { !it.isLetterOrDigit() }

    return containsNumber && containsSpecialChar
}




@Preview
@Composable
fun PreviewCreateAccountContent() {
    CreateAccountContent(
        onAccountCreated = {}
    )
}


