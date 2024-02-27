package com.example.gustavo_final_project

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.firebase.crashlytics.buildtools.reloc.com.google.common.reflect.TypeToken
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class HomePageActivity : ComponentActivity(), MenuItemClickListener {
    private var showMenu by mutableStateOf(false)
    private lateinit var entries: MutableList<Entry>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        entries = loadEntries(this).toMutableList()

        setContent {
            TopBarAndMenu(
                title = "Daily Journal",
                onMenuClick = { showMenu = !showMenu },
                showMenu = showMenu,
                onItemClick = this@HomePageActivity::onItemClick
            )

            if (!showMenu) {
                HomeContent(entries = entries, onItemClick = this@HomePageActivity::onEntryClick)
                AddEntryButton(context = this)
            }
        }
    }

    // Method to add an entry
//    private fun addEntry(entry: Entry) {
//        entries.add(entry)
//        saveEntries(this, entries)
//
//        // Update the entriesByDate map with the new entry's date
//        val calendar = Calendar.getInstance()
//        calendar.timeInMillis = SimpleDateFormat("dd MMMM yyyy", Locale.getDefault()).parse(entry.date)!!.time
//        entriesByDate[calendar.timeInMillis] = entry
//    }

    private fun onEntryClick(entry: Entry) {
        val intent = Intent(this, NewEntryActivity::class.java)
        intent.putExtra("entry", entry)
        startActivity(intent)
    }

    override fun onItemClick(item: String) {
        val intent = when (item) {
            "Entries" -> Intent(this, HomePageActivity::class.java)
            "Calendar" -> Intent(this, CalendarActivity::class.java)
            "Mood" -> Intent(this, MoodActivity::class.java)
            "Settings" -> Intent(this, SettingsActivity::class.java)
            else -> null
        }
        intent?.let {
            it.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
            startActivity(it)
            finish()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == NEW_ENTRY_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            val entryText = data?.getStringExtra("entryText")
            val date = data?.getStringExtra("date")
            val mood = data?.getStringExtra("mood") // Retrieve mood information

            entryText?.let { text ->
                date?.let { dateStr ->
                    val entry = Entry(text, dateStr, mood) // Include mood in the Entry object
                    entries.add(entry) // Add the entry to the list of entries
                    saveEntries(this, entries) // Save the entries

                    // Update the entriesByDate map with the new entry's date
                    val calendar = Calendar.getInstance()
                    calendar.timeInMillis = SimpleDateFormat("dd MMMM yyyy", Locale.getDefault()).parse(dateStr)!!.time
                    entriesByDate[calendar.timeInMillis] = entry

                    // Update the UI to reflect the new entry
                    entries = loadEntries(this).toMutableList() // Reload entries from SharedPreferences
                    setContent {
                        TopBarAndMenu(
                            title = "Daily Journal",
                            onMenuClick = { showMenu = !showMenu },
                            showMenu = showMenu,
                            onItemClick = this@HomePageActivity::onItemClick
                        )

                        if (!showMenu) {
                            HomeContent(entries = entries, onItemClick = this@HomePageActivity::onEntryClick)
                            AddEntryButton(context = this)
                        }
                    }
                }
            }
        }
    }

    companion object {
        const val NEW_ENTRY_REQUEST_CODE = 1001 // Define your request code
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
                val intent = Intent(context, NewEntryActivity::class.java)
                (context as Activity).startActivityForResult(intent, HomePageActivity.NEW_ENTRY_REQUEST_CODE)
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
fun HomeContent(entries: List<Entry>, onItemClick: (Entry) -> Unit) {
    if (entries.isEmpty()) {
        DefaultContent()
    } else {
        // Sort entries by date in ascending order (oldest to newest)
        val sortedEntries = entries.sortedBy { SimpleDateFormat("dd MMMM yyyy", Locale.getDefault()).parse(it.date) }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 50.dp) // Adjust the top padding as needed
        ) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                items(sortedEntries) { entry ->
                    EntryCard(entry, onItemClick)
                    Spacer(modifier = Modifier.height(16.dp))
                }
            }
        }
    }
}

@Composable
fun DefaultContent() {
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
