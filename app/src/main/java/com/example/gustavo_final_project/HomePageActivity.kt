package com.example.gustavo_final_project

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview

class HomePageActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            CreateHomeContent()
        }
    }

    @Composable
    fun CreateHomeContent() {
        Column {
            Text(text = "Home Page")

        }
    }

    @Preview
    @Composable
    fun PreviewCreateHomeContent() {
        CreateHomeContent()
    }

}