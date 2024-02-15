package com.example.gustavo_final_project

import android.annotation.SuppressLint
import android.app.Activity
import android.content.ContentResolver
import android.content.Intent
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.speech.RecognizerIntent
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.text.SimpleDateFormat
import java.util.*
import androidx.compose.material.icons.extended.*
import androidx.compose.material.icons.filled.AddAPhoto
import androidx.compose.material.icons.filled.AddPhotoAlternate
import androidx.compose.material.icons.filled.AddReaction
import androidx.compose.material.icons.filled.KeyboardVoice
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Mood
import androidx.compose.material.icons.filled.MoodBad
import androidx.compose.material.icons.filled.SentimentSatisfiedAlt
import androidx.compose.material.icons.filled.VoiceChat
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.text.style.TextAlign
import java.io.InputStream



class NewEntryActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            NewEntryScreen(this@NewEntryActivity)
        }
    }
}

@OptIn(ExperimentalComposeUiApi::class, ExperimentalMaterial3Api::class)
@Composable
fun NewEntryScreen(activity: Activity) {
    var textState by remember { mutableStateOf("") }
    val keyboardController = LocalSoftwareKeyboardController.current
    val currentDate = remember {
        SimpleDateFormat("dd MMMM yyyy", Locale.getDefault()).format(Date())
    }

    var expanded by remember { mutableStateOf(false) }

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
        Pair("\uD83D\uDE00", Color.Green),     // 😄
        Pair("\uD83D\uDE42", Color.Yellow),    // 🙂
        Pair("\uD83D\uDE10", Color.Red),       // 😐
        Pair("\uD83D\uDE1E", Color.Green),     // 😞
        Pair("\uD83D\uDE22", Color.Red),       // 😢
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
                TextButton(onClick = {activity.finish()}) {
                    Text(
                        text = "Cancel",
                        color = Color.Black,
                        fontSize = 16.sp
                    )
                }
                TextButton(onClick = {
                    val entryText = textState // Get the text from the text field
                    val currentDate = SimpleDateFormat("dd MMMM yyyy", Locale.getDefault()).format(Date())

                    val intent = Intent().apply {
                        putExtra("entryText", entryText)
                        putExtra("date", currentDate)
                    }

                    activity.setResult(Activity.RESULT_OK, intent) // Set the result to be sent back to the HomePageActivity
                    activity.finish()
                }) {
                    Text(
                        text = "Done",
                        color = Color.Black,
                        fontSize = 16.sp
                    )
                }
            }
        }
        TextField(
            value = textState,
            onValueChange = { textState = it },
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            keyboardOptions = KeyboardOptions.Default.copy(
                keyboardType = KeyboardType.Text,
                imeAction = ImeAction.Done
            ),
            keyboardActions = KeyboardActions(
                onDone = {
                    keyboardController?.hide()
                }
            ),
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
                    moods.forEach { (emoji, color) ->
                        IconButton(
                            onClick = {
                                textState += emoji // Append the selected emoji to the existing text
                                expanded = false
                            }
                        ) {
                            Text(
                                text = emoji,
                                fontSize = 36.sp,
                                modifier = Modifier.size(72.dp),
                                textAlign = TextAlign.Center,
                                color = color
                            )
                        }
                    }
                }
            }
        }
    }
}