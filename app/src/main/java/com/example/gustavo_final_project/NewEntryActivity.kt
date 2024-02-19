package com.example.gustavo_final_project

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.speech.RecognizerIntent
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.border
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.text.SimpleDateFormat
import java.util.*
import androidx.compose.material.icons.extended.*
import androidx.compose.material.icons.filled.AddAPhoto
import androidx.compose.material.icons.filled.AddReaction
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.ui.text.style.TextAlign


@Suppress("DEPRECATION")
class NewEntryActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val entry = intent.getParcelableExtra<Entry>("entry") // Retrieve the entry from intent extras
            NewEntryScreen(this@NewEntryActivity, entry) { entryText, currentDate, selectedMood ->
                // Add the entry to the map with the current date
                val dateInMillis = Calendar.getInstance().timeInMillis
                entriesByDate[dateInMillis] = Entry(entryText, currentDate)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NewEntryScreen(
    activity: Activity,
    entry: Entry?,
    onEntryAdded: (String, String, String?) -> Unit // Add a parameter for mood
) {
    var textState by remember { mutableStateOf(entry?.text ?: "") } // Set initial text to entry's text if available
    val currentDate = remember {
        SimpleDateFormat("dd MMMM yyyy", Locale.getDefault()).format(Date())
    }

    var expanded by remember { mutableStateOf(false) }
    var selectedMood by remember { mutableStateOf<String?>(null) } // Store the selected mood

    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        // Handle the selected image URI here
        // You can use the URI to load the image into the text box or perform other operations
        if (uri != null) {
            // Handle the selected image URI
            // For example, you might want to display the image in an ImageView or add it to the text box
        }
    }

    val speechLauncher = rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val data: Intent? = result.data
            val matches = data?.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)
            if (!matches.isNullOrEmpty()) {
                textState += matches[0] // Append the recognized text to the existing text
            }
        }
    }

    val moods = listOf(
        Pair("\uD83D\uDE00", "😄"),     // 😄
        Pair("\uD83D\uDE42", "🙂"),    // 🙂
        Pair("\uD83D\uDE10", "😐"),       // 😐
        Pair("\uD83D\uDE1E", "😞"),     // 😞
        Pair("\uD83D\uDE22", "😢"),       // 😢
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = currentDate,
                fontSize = 20.sp,
                color = Color.Black,
                fontWeight = FontWeight.Bold
            )
            Row {
                TextButton(onClick = { activity.finish() }) {
                    Text(
                        text = "Cancel",
                        color = Color.Black,
                        fontSize = 16.sp
                    )
                }
                TextButton(onClick = {
                    // Handle "Done" button click
                    val entryText = textState // Get the text from the text field
                    val currentDate = SimpleDateFormat("dd MMMM yyyy", Locale.getDefault()).format(Date())

                    // Call the callback to add the entry to the map
                    onEntryAdded(entryText, currentDate, selectedMood) // Pass the selected mood

                    val intent = Intent().apply {
                        putExtra("entryText", entryText)
                        putExtra("date", currentDate)
                    }

                    activity.setResult(Activity.RESULT_OK, intent) // Set the result to be sent back to the HomePageActivity
                    activity.finish() // Finish the current activity to go back to the HomePageActivity
                }) {
                    Text(
                        text = "Done",
                        color = Color.Black,
                        fontSize = 16.sp
                    )
                }
            }
        }

        // Mood selection section
        selectedMood?.let { mood ->
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, Color.Gray, shape = RoundedCornerShape(8.dp))
            ) {
                Text(
                    text = "Today I'm feeling: $mood",
                    fontSize = 24.sp,
                    color = Color.Black,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(16.dp)
                )
            }
        }

        // TextField for entering thoughts
        TextField(
            value = textState,
            onValueChange = { textState = it },
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            placeholder = { Text(text = "Write your thoughts here...") }
        )

        // Bottom menu with options
        BottomNavigation(
            backgroundColor = Color.White, // Set background color to white
            contentColor = Color.Black, // Set content color to black
            elevation = 0.dp // Remove drop shadow
        ) {
            BottomNavigationItem(
                selected = false,
                onClick = { launcher.launch("image/*") }, // Launch the image picker
                icon = {
                    Icon(Icons.Default.AddAPhoto, contentDescription = "Add Pictures")
                },
            )
            BottomNavigationItem(
                selected = false,
                onClick = { expanded = true },
                icon = {
                    Icon(Icons.Default.AddReaction, contentDescription = "Mood")
                },
            )
            BottomNavigationItem(
                selected = false,
                onClick = {
                    // Start the Speech to Text recognition
                    val speechIntent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
                    speechIntent.putExtra(
                        RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                        RecognizerIntent.LANGUAGE_MODEL_FREE_FORM
                    )
                    speechLauncher.launch(speechIntent)
                },
                icon = {
                    Icon(Icons.Default.Mic, contentDescription = "Voice")
                },
            )
        }
        // Dropdown menu for mood selection
        Box(
            modifier = Modifier.fillMaxWidth(),
            contentAlignment = Alignment.Center
        ) {
            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false },
                modifier = Modifier.width(IntrinsicSize.Min)
            ) {
                Text(
                    text = "How are you feeling today?",
                    fontSize = 20.sp,
                    color = Color.Black,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
                Row(
                    modifier = Modifier.horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    moods.forEach { (emoji, mood) ->
                        IconButton(
                            onClick = {
                                selectedMood = mood // Set the selected mood
                                expanded = false
                            }
                        ) {
                            Text(
                                text = emoji,
                                fontSize = 36.sp,
                                modifier = Modifier.size(72.dp),
                                textAlign = TextAlign.Center,
                                color = Color.Black
                            )
                        }
                    }
                }
            }
        }
    }
}
