package com.example.gustavo_final_project

import android.content.Intent
import android.content.res.Resources
import android.os.Bundle
import android.util.Log
import android.view.ContextThemeWrapper
import android.view.View
import android.view.ViewGroup
import android.widget.CalendarView
import android.widget.TextView
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Color.Companion.White
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import com.example.gustavo_final_project.AppColours.gradientBrush
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
                    onItemClick = { selectedEntry ->
                        // Launch NewEntryActivity with the selected entry
                        val intent = Intent(this@CalendarActivity, NewEntryActivity::class.java)
                        intent.putExtra("entry", selectedEntry)
                        startActivity(intent)
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
    onItemClick: (Entry) -> Unit
) {
    val selectedDateInMillis = remember { mutableStateOf(0L) }
    val selectedEntries = remember { mutableStateOf<List<Entry>?>(null) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(brush = gradientBrush)
    ) {
        // CalendarBox
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .padding(top = 10.dp)
                .border(1.dp, Color.Gray, shape = RoundedCornerShape(8.dp))
        ) {
            CalendarBox(entries = entries) { dateInMillis, filteredEntries ->
                selectedDateInMillis.value = dateInMillis
                selectedEntries.value = filteredEntries
            }
        }

        // DisplayEntries
        selectedEntries.value?.let { entries ->
            DisplayEntries(entries, onItemClick)
        }
    }
}

@Composable
fun CalendarBox(entries: List<Entry>, onDateSelected: (Long, List<Entry>) -> Unit) {
    val context = LocalContext.current

    val calendarView = remember { CalendarView(context) }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White.copy(alpha = 0.8f), shape = RoundedCornerShape(8.dp)),
    ) {
        AndroidView(
            factory = { context ->
                calendarView.apply {
                    layoutParams = ViewGroup.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT
                    )

                    setOnDateChangeListener { _, year, month, dayOfMonth ->
                        val calendar = Calendar.getInstance()
                        calendar.set(year, month, dayOfMonth)
                        val selectedDate = calendar.timeInMillis
                        val filteredEntries = filterEntriesForDate(selectedDate, entries)
                        onDateSelected(selectedDate, filteredEntries)
                    }

                    // Get the day number TextView and set its text color
                    val dayNumbersId = Resources.getSystem().getIdentifier("numberpicker_input", "id", "android")
                    val dayNumbersTextView = findViewById<TextView>(dayNumbersId)
                    dayNumbersTextView?.setTextColor(Color.White.toArgb())
                }
            }
        )
    }
}

private fun traverseCalendarView(viewGroup: ViewGroup, action: (View) -> Unit) {
    for (index in 0 until viewGroup.childCount) {
        val child = viewGroup.getChildAt(index)
        action(child)
        if (child is ViewGroup) {
            traverseCalendarView(child, action)
        }
    }
}

fun filterEntriesForDate(selectedDate: Long, entries: List<Entry>): List<Entry> {
    val formattedSelectedDate = SimpleDateFormat("dd MMMM yyyy", Locale.getDefault()).format(Date(selectedDate))
    return entries.filter { entry ->
        entry.date == formattedSelectedDate
    }
}

@Composable
fun DisplayEntries(entries: List<Entry>, onItemClick: (Entry) -> Unit) {
    if (entries.isNotEmpty()) {
        Text(
            text = "Entries on this day:",
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            fontSize = 20.sp,
            color = White,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 32.dp, bottom = 16.dp) // Added more space between title and entries
        )
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
        ) {
            items(entries) { entry ->
                Box(
                    modifier = Modifier.padding(horizontal = 16.dp)
                ) {
                    EntryCard(
                        entry = entry,
                        onItemClick = onItemClick
                    )
                }
                Spacer(modifier = Modifier.height(16.dp)) // Add space between EntryCards
            }
        }
    } else {
        // Show message when no entries are available
        Text(
            text = "No entries added to this day",
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            fontSize = 20.sp,
            color = White,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp)
        )
    }
}