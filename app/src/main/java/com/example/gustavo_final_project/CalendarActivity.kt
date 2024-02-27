package com.example.gustavo_final_project

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.ViewGroup
import android.widget.CalendarView
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import java.util.Calendar


class CalendarActivity : ComponentActivity(), MenuItemClickListener {

    private var showMenu by mutableStateOf(false)
    private var isContentVisible by mutableStateOf(true) // Control visibility of content

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
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
                val calendarView = remember { CalendarView(this) } // Remember the CalendarView instance
                CalendarView(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(top = 60.dp, start = 0.dp, end = 0.dp, bottom = 0.dp)
                ) { selectedDateInMillis ->
                    val entry = entriesByDate[selectedDateInMillis]
                    if (entry != null) {
                        val intent = Intent(this@CalendarActivity, NewEntryActivity::class.java).apply {
                            putExtra("entry", entry)
                        }
                        startActivity(intent)
                    }
                }

                // Update the calendar with entries
                updateCalendarWithEntries(calendarView)
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
    onDateSelected: (Long) -> Unit = {}
) {
    AndroidView(
        modifier = modifier,
        factory = { context ->
            CalendarView(context).apply {
                layoutParams = ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT
                )

                setOnDateChangeListener { _, year, month, dayOfMonth ->
                    val calendar = Calendar.getInstance()
                    calendar.set(year, month, dayOfMonth)
                    val selectedDateInMillis = calendar.timeInMillis
                    onDateSelected(selectedDateInMillis)
                }
            }
        }
    )
}

val entriesByDate = mutableMapOf<Long, Entry>()
fun updateCalendarWithEntries(calendarView: CalendarView) {
    for ((dateInMillis, _) in entriesByDate) {
        calendarView.setDate(dateInMillis, true, false)
    }
}


fun markDate(year: Int, month: Int, dayOfMonth: Int, calendarView: CalendarView) {
    // Get the current date in milliseconds
    val currentDate = Calendar.getInstance()
    val currentYear = currentDate.get(Calendar.YEAR)
    val currentMonth = currentDate.get(Calendar.MONTH)
    val currentDayOfMonth = currentDate.get(Calendar.DAY_OF_MONTH)

    // Set the date for marking
    val markedDate = Calendar.getInstance()
    markedDate.set(year, month, dayOfMonth)

    // Check if the date to mark is today
    val isToday = year == currentYear && month == currentMonth && dayOfMonth == currentDayOfMonth

    // Get the time in milliseconds for the marked date
    val markedDateInMillis = markedDate.timeInMillis

    // Mark the date on the calendar view
    calendarView.setDate(markedDateInMillis, true, isToday)
}

