package com.example.gustavo_final_project

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.ViewGroup
import android.widget.CalendarView
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Divider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.google.android.material.datepicker.DayViewDecorator
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale


class CalendarActivity : ComponentActivity(), MenuItemClickListener {
    private var showMenu by mutableStateOf(false)
    private var isContentVisible by mutableStateOf(true) // Control visibility of content
    private lateinit var entries: MutableList<Entry>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        entries = loadEntries(this).toMutableList()
        setContent {
            TopBarAndMenu(
                title = "Calendar",
                onMenuClick = {
                    showMenu = !showMenu
                    isContentVisible = !showMenu // Update content visibility
                },
                showMenu = showMenu,
                onItemClick = this@CalendarActivity::onItemClick
            )

            // Display the content only if isContentVisible is true
            if (isContentVisible) {
                // In the CalendarActivity or wherever you're using CalendarView
                CalendarView(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(top = 60.dp, start = 0.dp, end = 0.dp, bottom = 0.dp),
                    entries = entries,
                    onDateSelected = { selectedDate ->
                        val selectedDateString = SimpleDateFormat("dd MMMM yyyy", Locale.getDefault()).format(Date(selectedDate))
                        val selectedEntries = entries.filter { it.date == selectedDateString }

                        // Now you can do something with the selected entries, such as displaying them or performing any other operation
                        // For example, you might want to update the UI to display the selected entries
                        // You can store the selected entries in a state variable and use it to update the UI
                        // For simplicity, I'll just print the entries to the log
                        selectedEntries.forEach { entry ->
                            Log.d("SelectedEntries", "Entry: ${entry.text}, Date: ${entry.date}")
                        }
                    }
                )
            }
        }
    }

    override fun onItemClick(item: String) {
        // Hide content when the menu is clicked
        isContentVisible = !showMenu

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
}


@Composable
fun CalendarView(
    modifier: Modifier = Modifier,
    entries: List<Entry>,
    onDateSelected: (Long) -> Unit
) {
    val selectedDateInMillis = remember { mutableStateOf(0L) }
    val selectedEntries = remember { mutableStateOf<List<Entry>>(emptyList()) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        AndroidView(
            modifier = Modifier
                .fillMaxSize()
                .weight(1f),
            factory = { context ->
                CalendarView(context).apply {
                    layoutParams = ViewGroup.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT
                    )

                    setOnDateChangeListener { _, year, month, dayOfMonth ->
                        val calendar = Calendar.getInstance()
                        calendar.set(year, month, dayOfMonth)
                        val selectedDate = calendar.timeInMillis
                        selectedDateInMillis.value = selectedDate
                        val selectedDateString = SimpleDateFormat("dd MMMM yyyy", Locale.getDefault()).format(Date(selectedDate))
                        selectedEntries.value = entries.filter { it.date == selectedDateString }
                        onDateSelected(selectedDate) // Notify the parent about the selected date
                    }
                }
            }
        )

        Spacer(modifier = Modifier.height(16.dp))

        if (selectedEntries.value.isNotEmpty()) {
            Text("Entries for selected date:")
            selectedEntries.value.forEach { entry ->
                EntryCard(entry = entry, onItemClick = {})
            }
        } else if (selectedDateInMillis.value != 0L) {
            Text("No entries added to this day")
        }
    }
}

