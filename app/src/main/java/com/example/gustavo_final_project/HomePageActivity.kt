package com.example.gustavo_final_project

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.gustavo_final_project.AppColours.gradientBrush
import java.text.SimpleDateFormat
import java.util.Locale

@Suppress("DEPRECATION")
class HomePageActivity : ComponentActivity(), MenuItemClickListener {
    private var showMenu by mutableStateOf(false)
    private lateinit var entries: MutableList<Entry>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        entries = loadEntries(this).toMutableList()

        setContent {
            TopBarAndMenu(
                title = "Journal",
                onMenuClick = { showMenu = !showMenu },
                showMenu = showMenu,
                onItemClick = this@HomePageActivity::onItemClick
            )

            if (!showMenu) {
                HomeContent(
                    entries = entries,
                    onItemClick = this@HomePageActivity::onEntryClick,
                    onDeleteClick = { entry ->
                        // Define the behavior for onDeleteClick here
                        // For example, you can remove the entry from the list and update the UI
                        entries.remove(entry)
                        saveEntries(this@HomePageActivity, entries)
                    }
                )
                AddEntryButton(context = this)
            }
        }

    }

    private fun addEntry(entry: Entry) {
        entries.add(entry)
        saveEntries(this, entries)
        entry.mood?.let { mood ->
            updateMoodDataInMoodActivity(mood)
        }
    }

    private fun updateMoodDataInMoodActivity(mood: String) {
        if (mood.isNotEmpty()) {
            val intent = Intent(this, MoodActivity::class.java)
            intent.putExtra("updateEntries", true)
            intent.putExtra("mood", mood)
            startActivity(intent)
        }
    }

    private fun onEntryClick(entry: Entry) {
        val intent = Intent(this, NewEntryActivity::class.java)
        intent.putExtra("entry", entry)
        intent.putParcelableArrayListExtra("pictureUris", ArrayList(entry.pictureUris))
        startActivity(intent)
    }

    override fun onItemClick(item: String) {
        val intent = when (item) {
            "Entries" -> Intent(this, HomePageActivity::class.java)
            "Calendar" -> Intent(this, CalendarActivity::class.java)
            "Mood" -> Intent(this, MoodActivity::class.java)
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
            val mood = data?.getStringExtra("mood")
            val imageUris: List<Uri>? = data?.getParcelableArrayListExtra<Uri>("images")

            entryText?.let { text ->
                date?.let { dateStr ->
                    val entry = Entry(text, dateStr, mood)
                    addEntry(entry)
                }
            }
        }
    }

    private fun deleteEntry(entry: Entry) {
        entries.remove(entry)
        saveEntries(this, entries)
    }

    companion object {
        const val NEW_ENTRY_REQUEST_CODE = 1001
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
fun HomeContent(entries: List<Entry>, onItemClick: (Entry) -> Unit, onDeleteClick: (Entry) -> Unit) {
    Box(
        modifier = Modifier
            .padding(top = 64.dp, start = 0.dp, end = 0.dp, bottom = 0.dp)
            .background(brush = gradientBrush) // Change the color as per your requirement
    ) {
    if (entries.isEmpty()) {
        DefaultContent()
    } else {
        // Sort entries by date in descending order (newest to oldest)
        val sortedEntries = entries.sortedByDescending {
            SimpleDateFormat("dd MMMM yyyy", Locale.getDefault()).parse(it.date) // Combine date and time if available
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
        ) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                items(sortedEntries, key = { entry -> entry.hashCode() }) { entry ->
                    Log.d("HomeContent", "Entry Picture URIs: ${entry.pictureUris}")
                    EntryCard(entry, onItemClick) {
                        onDeleteClick(entry)
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                }
            }
        }
    }
}}



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
            fontSize = 20.sp,
            color = Color.White,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "Add new entries by tapping on the plus button below.",
            fontSize = 16.sp,
            color = Color.White,
            textAlign = TextAlign.Center
        )
    }
}

@Preview
@Composable
fun PreviewHomePage() {
    val dummyMenuItemClickListener = object : MenuItemClickListener {
        override fun onItemClick(item: String) {
        }
    }

    TopBarAndMenu(
        title = "Daily Journal",
        onMenuClick = { /* handle menu click for preview */ },
        showMenu = false, // Set to initial value for preview
        onItemClick = { /* handle item click for preview */ }
    )
}