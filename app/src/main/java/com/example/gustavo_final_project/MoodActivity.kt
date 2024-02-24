package com.example.gustavo_final_project

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class MoodActivity : ComponentActivity(), MenuItemClickListener {

    private var showMenu by mutableStateOf(false)
    private val entries = mutableStateListOf<Entry>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Load entries from SharedPreferences when the activity is created
        entries.addAll(loadEntriesFromSharedPreferences())

        setContent {
            TopBarAndMenu(
                title = "Mood Tracking",
                onMenuClick = { showMenu = !showMenu },
                showMenu = showMenu,
                onItemClick = this@MoodActivity::onItemClick
            )

            val updateEntriesFlag = intent.getBooleanExtra("updateEntries", false)
            if (updateEntriesFlag) {
                val mood = intent.getStringExtra("mood")
                mood?.let { moodValue ->
                    updateMoodData()
                }
            }

            MoodContent(entries = entries, showMenu = showMenu)
        }
    }

    private fun updateMoodData() {
        // Clear existing entries before updating
        entries.clear()

        // Load entries from SharedPreferences
        val updatedEntries = loadEntries(this)

        // Add all loaded entries to the entries list
        entries.addAll(updatedEntries)

        // Save updated entries to SharedPreferences
        saveEntriesToSharedPreferences(entries)
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

    private fun saveEntriesToSharedPreferences(entries: List<Entry>) {
        val sharedPreferences = getSharedPreferences("MoodEntries", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        val gson = Gson()
        val json = gson.toJson(entries)
        editor.putString("entries", json)
        editor.apply()
    }

    private fun loadEntriesFromSharedPreferences(): List<Entry> {
        val sharedPreferences = getSharedPreferences("MoodEntries", Context.MODE_PRIVATE)
        val gson = Gson()
        val json = sharedPreferences.getString("entries", "")
        val type = object : TypeToken<List<Entry>>() {}.type
        return gson.fromJson(json, type) ?: emptyList()
    }
}


@Composable
fun MoodContent(entries: List<Entry>, showMenu: Boolean) {
    val moodData = collectMoodData(entries)

    if (!showMenu) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (entries.isEmpty()) {
                Text(
                    text = "No moods have been added yet",
                    fontSize = 18.sp,
                    color = Color.Black
                )
            } else {
                BarGraph(moodData)
            }
        }
    } else {
        // Display an empty composable when the menu is open to hide other content
        Box(modifier = Modifier.fillMaxSize())
    }
}

@Composable
fun BarGraph(moodData: MoodData) {
    val entries = moodData.moodCounts.entries.toList()
    val maxValue = entries.maxByOrNull { it.value }?.value ?: 0

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        entries.forEach { (mood, count) ->
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(vertical = 16.dp) // Add more space between bars
            ) {
                // Place the emoji in front of the bar
                Text(
                    text = mood,
                    modifier = Modifier.padding(end = 8.dp),
                    fontSize = 30.sp
                )
                // Create a thicker bar with rounded corners
                LinearProgressIndicator(
                    progress = count.toFloat() / maxValue,
                    modifier = Modifier
                        .weight(1f) // Ensure the bar takes the remaining space
                        .height(30.dp) // Adjust the height as needed for thicker bars
                        .clip(RoundedCornerShape(8.dp)) // Round the corners
                )
                // Display the count of occurrences
                Text(
                    text = count.toString(),
                    fontSize = 24.sp,
                    modifier = Modifier.padding(start = 8.dp) // Add padding between the bar and the count
                )
            }
        }
    }
}





// Helper function to collect mood data from entries
fun collectMoodData(entries: List<Entry>): MoodData {
    // Process the entries and extract mood data
    val moodCounts = mutableMapOf<String, Int>().withDefault { 0 }

    entries.forEach { entry ->
        entry.mood?.let { mood ->
            moodCounts[mood] = moodCounts.getValue(mood) + 1
        }
    }

    return MoodData(moodCounts)
}

// Define the MoodData class to hold mood data for the graph
data class MoodData(
    val moodCounts: Map<String, Int>
)


