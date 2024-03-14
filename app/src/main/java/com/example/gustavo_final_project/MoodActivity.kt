package com.example.gustavo_final_project

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.border
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
import androidx.compose.material3.Divider
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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.text.font.FontWeight
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
                Column(modifier = Modifier.fillMaxWidth()) {
                    BarGraph(moodData)
                    Divider(modifier = Modifier.padding(top = 0.dp, bottom = 16.dp, start = 16.dp, end = 16.dp)) // Add a divider
                    LineChart(moodData)
                }
            }
        }
    } else {
        // Display an empty composable when the menu is open to hide other content
        Box(modifier = Modifier.fillMaxSize())
    }
}

@Composable
fun LineChart(moodData: MoodData) {
    // Convert mood data to list of pairs for plotting
    val entries = moodData.moodCounts.entries.toList()
    val maxValue = entries.maxByOrNull { it.value }?.value ?: 0

    Canvas(
        modifier = Modifier
            .fillMaxWidth()
            .height(260.dp)
            .padding(top = 20.dp, bottom = 16.dp, start = 20.dp, end = 20.dp) // Add padding
    ) {
        val graphWidth = size.width - 32 // Adjust width with padding
        val graphHeight = size.height

        val points = entries.mapIndexed { index, entry ->
            val x = index * (graphWidth / (entries.size - 1))
            val y = graphHeight - entry.value.toFloat() / maxValue * graphHeight
            Offset(x, y)
        }

        drawLine(
            color = Color.Gray,
            start = Offset(0f, graphHeight),
            end = Offset(graphWidth, graphHeight),
            strokeWidth = 2f
        )

        drawLine(
            color = Color.Gray,
            start = Offset(0f, 0f),
            end = Offset(0f, graphHeight),
            strokeWidth = 2f
        )

        points.forEachIndexed { index, point ->
            if (index < points.size - 1) {
                drawLine(
                    color = Color.Black,
                    start = point,
                    end = points[index + 1],
                    strokeWidth = 8f // Increase the thickness of the line
                )
            }

            // Draw emojis and counts
            val emoji = entries[index].key // Get the emoji corresponding to the mood
            val count = entries[index].value // Get the count of the mood

            // Draw a line from the point to the count at the bottom with bottom padding
            drawLine(
                color = Color.LightGray,
                start = Offset(point.x, point.y),
                end = Offset(point.x, graphHeight + 0f), // Add bottom padding of 20f
                strokeWidth = 2f
            )

            drawIntoCanvas { canvas ->
                val paint = android.graphics.Paint().apply { // Specify the Android Paint class
                    color = Color.Black.toArgb()
                    textSize = 70f // Adjust the size of the text
                }
                // Draw emoji
                canvas.nativeCanvas.drawText(
                    emoji,
                    point.x - 20f, // Adjust the x-coordinate for centering
                    point.y + 10f, // Adjust the y-coordinate for positioning below the line
                    paint
                )

                // Draw a white box
                canvas.nativeCanvas.drawRect(
                    point.x - 30f, // Adjust the x-coordinate for centering
                    size.height - 70f, // Adjust the y-coordinate for positioning above the x-axis
                    point.x + 30f, // Adjust the width of the rectangle
                    size.height - 10f, // Adjust the height of the rectangle
                    android.graphics.Paint().apply {
                        color = Color.White.toArgb()
                    }
                )

                // Draw count inside the white box
                paint.color = Color.Black.toArgb()
                canvas.nativeCanvas.drawText(
                    count.toString(),
                    point.x - 10f, // Adjust the x-coordinate for positioning inside the white box
                    size.height - 20f, // Adjust the y-coordinate for centering inside the white box
                    paint
                )
            }
        }
    }
}



@Composable
fun BarGraph(moodData: MoodData) {
    val entries = moodData.moodCounts.entries.toList()
    val totalCount = entries.sumBy { it.value } // Calculate the total count of moods
    val maxValue = entries.maxByOrNull { it.value }?.value ?: 0

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        // Display the total count of moods in a gray-bordered box at the top
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp)
                .border(1.dp, Color.Gray, shape = RoundedCornerShape(8.dp))
                .padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            Row (verticalAlignment = Alignment.CenterVertically){
                Text(
                    text = "Total reactions: ",
                    fontSize = 24.sp,
                    color = Color.Black,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "$totalCount",
                    fontSize = 20.sp,
                    color = Color.Black
                )
            }

        }
        entries.forEach { (mood, count) ->
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(vertical = 12.dp) // Add more space between bars
            ) {
                // Place the emoji in front of the bar
                Text(
                    text = mood,
                    modifier = Modifier.padding(end = 8.dp),
                    fontSize = 24.sp
                )
                // Create a thicker bar with rounded corners
                LinearProgressIndicator(
                    progress = count.toFloat() / maxValue,
                    modifier = Modifier
                        .weight(1f) // Ensure the bar takes the remaining space
                        .height(18.dp) // Adjust the height as needed for thicker bars
                        .clip(RoundedCornerShape(8.dp)) // Round the corners
                )
                // Display the count of occurrences
                Text(
                    text = count.toString(),
                    fontSize = 18.sp,
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


