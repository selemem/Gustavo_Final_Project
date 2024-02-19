package com.example.gustavo_final_project

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Create
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.Scaffold
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
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Create,
                            contentDescription = null,
                            modifier = Modifier.padding(end = 8.dp)
                        )
                        Text(
                            text = title,
                            fontSize = 20.sp,
                            color = Color.Black
                        )
                    }
                },
                actions = {
                    if (showMenu) {
                        IconButton(onClick = onMenuClick) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Close"
                            )
                        }
                    } else {
                        IconButton(onClick = onMenuClick) {
                            Icon(
                                imageVector = Icons.Default.Menu,
                                contentDescription = "Menu"
                            )
                        }
                    }
                }
            )
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
    val options = listOf("Entries", "Calendar", "Mood", "Settings")
    var selectedOption by remember { mutableStateOf(options.first()) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(top = 68.dp, start = 0.dp, end = 0.dp, bottom = 0.dp)
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
                    fontSize = 20.sp,
                    textAlign = TextAlign.Start
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}


