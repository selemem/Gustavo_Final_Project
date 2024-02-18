package com.example.gustavo_final_project

import android.content.Intent
import android.os.Bundle
import android.widget.CalendarView
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import java.util.Calendar

class CalendarActivity : ComponentActivity(), MenuItemClickListener {

    private var showMenu by mutableStateOf(false)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            TopBarAndMenu(
                title = "Calendar",
                onMenuClick = { showMenu = !showMenu },
                showMenu = showMenu,
                onItemClick = this@CalendarActivity::onItemClick
            )

            // Display the calendar
            CalendarView(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp) // Adjust padding as needed
            )
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
                // Define any customization or setup for the calendar view here
                setOnDateChangeListener { _, year, month, dayOfMonth ->
                    val calendar = Calendar.getInstance()
                    calendar.set(year, month, dayOfMonth)
                    val selectedDateInMillis = calendar.timeInMillis
                    onDateSelected(selectedDateInMillis)
                }
            }
        }
    ) { calendarView ->
        // Any additional configuration for the calendar view can be done here
        // For example, setting the first day of the week, etc.
    }
}

