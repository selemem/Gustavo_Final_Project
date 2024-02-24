package com.example.gustavo_final_project

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
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

class MoodActivity : ComponentActivity(), MenuItemClickListener {

    private var showMenu by mutableStateOf(false)
    private val entries = mutableStateListOf<Entry>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            TopBarAndMenu(
                title = "Mood Tracking",
                onMenuClick = { showMenu = !showMenu },
                showMenu = showMenu,
                onItemClick = this@MoodActivity::onItemClick
            )

            MoodContent(entries = entries, showMenu = showMenu)
        }
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

    private fun updateEntries(entryList: List<Entry>) {
        entries.clear()
        entries.addAll(entryList)
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
            Text(
                text = "$mood: $count",
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp)
            )
            LinearProgressIndicator(
                progress = count.toFloat() / maxValue,
                modifier = Modifier.fillMaxWidth()
            )
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


