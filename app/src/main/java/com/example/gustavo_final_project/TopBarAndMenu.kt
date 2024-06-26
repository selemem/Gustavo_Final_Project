package com.example.gustavo_final_project

import android.R
import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.AppBarDefaults
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoStories
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Create
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Color.Companion.White
import androidx.compose.ui.res.fontResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.googlefonts.GoogleFont
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopBarAndMenu(
    title: String,
    onMenuClick: () -> Unit,
    showMenu: Boolean,
    onItemClick: (String) -> Unit
) {
    Scaffold(
        topBar = {
            Box(
                modifier = Modifier
                    .background(Color(android.graphics.Color.parseColor("#240743"))) // Change background color here
                    .padding(8.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
//                    Icon(
//                        imageVector = Icons.Default.AutoStories,
//                        contentDescription = null,
//                        modifier = Modifier.padding(start = 8.dp, end = 8.dp),
//                        tint = White
//                    )
                    Text(
                        modifier = Modifier.padding(start = 8.dp, top = 8.dp),
                        text = title,
                        fontSize = 22.sp,
                        color = White,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.weight(1f))
                    if (showMenu) {
                        IconButton(onClick = onMenuClick) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Close",
                                tint = White
                            )
                        }
                    } else {
                        IconButton(onClick = onMenuClick) {
                            Icon(
                                imageVector = Icons.Default.Menu,
                                contentDescription = "Menu",
                                tint = White
                            )
                        }
                    }
                }
            }
        },
        content = {
            if (showMenu) {
                SideBarMenu(onItemClick = onItemClick)
            }
        }
    )
}

@Composable
fun SideBarMenu(
    onItemClick: (String) -> Unit // Accepts the click events for each menu item
) {
    val options = listOf("Entries", "Calendar", "Mood")
    var selectedOption by remember { mutableStateOf(options.first()) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(brush = AppColours.gradientBrush)
            .padding(top = 75.dp, start = 0.dp, end = 0.dp, bottom = 0.dp)
    ) {
        options.forEach { option ->
            val isSelected = option == selectedOption
            TextButton(
                onClick = {
                    // Update the selected option
                    selectedOption = option
                    onItemClick(option) // Notify the click event for this menu item
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
                    .background(if (isSelected) Color.Transparent else Color.Transparent),
                colors = ButtonDefaults.textButtonColors(
                    contentColor = if (isSelected) Color.Black else LocalContentColor.current
                )
            ) {
                Text(
                    text = option,
                    fontSize = 22.sp,
                    textAlign = TextAlign.Start,
                    color = White
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}


