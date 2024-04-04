package com.example.gustavo_final_project

import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

object AppColours {

    // Define the gradient colors
    val gradientColors = listOf(
        Color(android.graphics.Color.parseColor("#240743")),
        Color(android.graphics.Color.parseColor("#000000")))

    // Define the gradient brush
    val gradientBrush = Brush.linearGradient(
        colors = gradientColors
    )
}
