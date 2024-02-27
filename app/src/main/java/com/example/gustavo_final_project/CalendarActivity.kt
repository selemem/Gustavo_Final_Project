package com.example.gustavo_final_project

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.CalendarView
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.google.android.material.datepicker.DayViewDecorator
import org.intellij.lang.annotations.JdkConstants
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

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
                CalendarView(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(top = 60.dp, start = 0.dp, end = 0.dp, bottom = 0.dp)
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