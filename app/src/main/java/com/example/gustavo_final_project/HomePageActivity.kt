package com.example.gustavo_final_project

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Card
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

class HomePageActivity : ComponentActivity(), MenuItemClickListener {
    private var showMenu by mutableStateOf(false)
    private var entries = mutableListOf<Entry>() // Maintain a list of entries

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            TopBarAndMenu(
                title = "Daily Journal",
                onMenuClick = { showMenu = !showMenu },
                showMenu = showMenu,
                onItemClick = this@HomePageActivity::onItemClick
            )

            if (!showMenu) {
                HomeContent(entries = entries) // Pass the list of entries to HomeContent
                AddEntryButton(context = this)
            }
        }
    }

    override fun onItemClick(item: String) {
        val intent = when (item) {
            "Entries" -> Intent(this, HomePageActivity::class.java)
            "Calendar" -> Intent(this, CalendarActivity::class.java)
            "History" -> Intent(this, HistoryActivity::class.java)
            "Settings" -> Intent(this, SettingsActivity::class.java)
            else -> null
        }
        intent?.let {
            it.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
            startActivity(it)
            finish()
        }
    }

    // Function to add a new entry to the list of entries
    fun addEntry(entry: Entry) {
        entries.add(entry)
    }
}


@Composable
fun AddEntryButton(context: Context) {
    Box(
        modifier = Modifier
            .padding(24.dp)
            .fillMaxSize(),
        contentAlignment = Alignment.BottomEnd
    ) {
        FloatingActionButton(
            onClick = {
                // Handle button click, e.g., navigate to NewEntryActivity
                val intent = Intent(context, NewEntryActivity::class.java)
                context.startActivity(intent)
            },
            shape = CircleShape,
            contentColor = Color.Black
        ) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = "Add"
            )
        }
    }
}

@Composable
fun HomeContent(entries: List<Entry>) {
    if (entries.isEmpty()) {
        // Display the HomeContent if the list of entries is empty
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(100.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Start journaling",
                fontSize = 24.sp,
                color = Color.Black
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Add new entries by tapping on the plus button below.",
                fontSize = 16.sp,
                color = Color.Black,
                textAlign = TextAlign.Center
            )
        }
    } else {
        // Display the list of Entry cards if entries exist
        LazyColumn(
            modifier = Modifier.padding(8.dp)
        ) {
            items(entries) { entry ->
                EntryCard(entry = entry)
                Spacer(modifier = Modifier.height(8.dp))
            }
        }
    }
}


@Composable
fun EntryCard(entry: Entry) {
    Card(
        modifier = Modifier.fillMaxWidth(),
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = entry.date,
                fontSize = 16.sp,
                color = Color.Gray
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = entry.text,
                fontSize = 18.sp,
                color = Color.Black
            )
//            Spacer(modifier = Modifier.height(8.dp))
//            Text(
//                text = "Mood: ${entry.mood}",
//                fontSize = 14.sp,
//                color = Color.Gray
//            )
            // Render pictures here
        }
    }
}

// Function to add a new entry to the list of entries
fun addEntry(entry: Entry, entries: MutableList<Entry>, updateUI: () -> Unit) {
    entries.add(entry)
    updateUI()
}

@Preview
@Composable
fun PreviewHomePage() {
    val dummyMenuItemClickListener = object : MenuItemClickListener {
        override fun onItemClick(item: String) {
            // No action needed for preview
        }
    }

    TopBarAndMenu(
        title = "Daily Journal",
        onMenuClick = { /* handle menu click for preview */ },
        showMenu = false, // Set to initial value for preview
        onItemClick = { /* handle item click for preview */ }
    )
}
