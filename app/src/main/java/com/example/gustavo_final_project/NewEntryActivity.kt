package com.example.gustavo_final_project

import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.speech.RecognizerIntent
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color.Companion.White
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.window.Popup
import coil.annotation.ExperimentalCoilApi
import coil.compose.rememberImagePainter

@Suppress("DEPRECATION")
class NewEntryActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val entry = intent.getParcelableExtra<Entry>("entry")

            // Retrieve picture URIs from intent extras
            val pictureUris = intent.getParcelableArrayListExtra<Uri>("pictureUris")
            Log.d("NewEntryActivity", "Picture URIs: $pictureUris")

            NewEntryScreen(this@NewEntryActivity, entry, pictureUris) { entryText, currentDate, selectedMood, selectedImages ->
                val intent = Intent().apply {
                    putExtra("entryText", entryText)
                    putExtra("date", currentDate)
                    putExtra("mood", selectedMood)
                    putParcelableArrayListExtra("images", ArrayList(selectedImages)) // Pass the selected images
                }
                setResult(Activity.RESULT_OK, intent)
                finish()
            }
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class, ExperimentalCoilApi::class)
@Composable
fun NewEntryScreen(
    activity: Activity,
    entry: Entry?,
    pictureUris: List<Uri>?, // Accept picture URIs as parameter
    onEntryAdded: (String, String, String?, List<Uri>) -> Unit
) {
    Box(
        modifier = Modifier
            .background(brush = AppColours.gradientBrush) // Change the color as per your requirement
    ) {
        var textState by remember {
            mutableStateOf(
                entry?.text ?: ""
            )
        } // Set initial text to entry's text if available
        val currentDate = remember {
            SimpleDateFormat("dd MMMM yyyy", Locale.getDefault()).format(Date())
        }

        var shareMenuExpanded by remember { mutableStateOf(false) }
        var moodMenuExpanded by remember { mutableStateOf(false) }
        var selectedMood by remember { mutableStateOf<String?>(entry?.mood) } // Store the selected mood
        var selectedImages by remember {
            mutableStateOf<List<Uri>>(
                entry?.pictureUris ?: emptyList()
            )
        }

        val launcher =
            rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
                uri?.let {
                    // Handle the selected image URI here
                    selectedImages = selectedImages + it
                }
            }


        val speechLauncher =
            rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                if (result.resultCode == Activity.RESULT_OK) {
                    val data: Intent? = result.data
                    val matches = data?.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)
                    if (!matches.isNullOrEmpty()) {
                        textState += matches[0] // Add the recognized text to the existing text
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
                .padding(16.dp)
                .background(brush = AppColours.gradientBrush),
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
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
                Row {
                    // Share button with dropdown menu
                    TextButton(onClick = { shareMenuExpanded = !shareMenuExpanded }) {
                        Text(
                            "Share",
                            color = Color.White,
                            fontSize = 16.sp
                        )
                    }
                    TextButton(onClick = { activity.finish() }) {
                        Text(
                            text = "Cancel",
                            color = Color.White,
                            fontSize = 16.sp
                        )
                    }
                    if (entry == null) { // Render "Done" button only for new entries
                        TextButton(onClick = {
                            val entryText = textState
                            val currentDate =
                                SimpleDateFormat("dd MMMM yyyy", Locale.getDefault()).format(Date())
                            onEntryAdded(entryText, currentDate, selectedMood, selectedImages)

                            val intent = Intent().apply {
                                putExtra("entryText", entryText)
                                putExtra("date", currentDate)
                            }

                            activity.setResult(Activity.RESULT_OK, intent)
                            activity.finish()
                        }) {
                            Text(
                                text = "Done",
                                color = Color.White,
                                fontSize = 16.sp
                            )
                        }
                    }
                }
            }

            // Mood selection section
            selectedMood?.let { mood ->
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color.White.copy(alpha = 0.10f))
                        .border(1.dp, Color.White, shape = RoundedCornerShape(8.dp))
                ) {
                    Text(
                        text = "Today I'm feeling: $mood",
                        fontSize = 22.sp,
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(16.dp)
                    )
                }
            }

            // flag to determine if text editing is enabled
            val isEditingEnabled = entry == null // Disable editing if an entry already exists

            // TextField for entering thoughts
            TextField(
                value = textState,
                onValueChange = { textState = it },
                modifier = Modifier
                    .fillMaxSize()
                    .weight(1f) // Take up the entire available space
                    .background(White.copy(alpha = 0.10f), shape = RoundedCornerShape(8.dp)),
                placeholder = { Text(text = "Write your thoughts here...") },
                enabled = isEditingEnabled // Disable editing if an entry already exists

            )

            // Populate selectedImages with picture URIs from the Entry object
            if (entry != null && (pictureUris.isNullOrEmpty() || pictureUris.isEmpty())) {
                selectedImages = entry.pictureUris // Use the picture URIs from the Entry object
            } else {
                pictureUris?.let {
                    selectedImages = it // Use the passed picture URIs
                }
            }

            // Log the picture URIs to check if they are valid
            Log.d("PictureURIs", "Picture URIs: $selectedImages")

            // Selected images preview
            selectedImages.takeIf { it.isNotEmpty() }?.let { images ->
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f) // Take half of the available space
                ) {
                    items(images) { uri ->
                        Image(
                            painter = rememberImagePainter(uri),
                            contentDescription = null,
                            modifier = Modifier
                                .fillMaxWidth() // Clip the image to fill the entire width of the screen
                                .height(400.dp) // Set a fixed height for the image
                                .clip(shape = RoundedCornerShape(8.dp)) // Apply rounded corners to the image
                                .padding(bottom = 16.dp), // Add space between each picture
                            contentScale = ContentScale.Crop // Crop the image to fit the specified dimensions
                        )
                    }
                }
            }

            // Bottom menu with options
            if (entry == null) { // Render bottom menu only for new entries
                BottomNavigation(
                    backgroundColor = Color.Black, // Set background color to white
                    contentColor = Color.White, // Set content color to black
                    elevation = 0.dp // Remove drop shadow
                ) {
                    BottomNavigationItem(
                        selected = false,
                        onClick = { launcher.launch("image/*") }, // Launch the image picker
                        icon = {
                            Icon(Icons.Default.AddAPhoto, contentDescription = "Add Pictures", tint = Color.White)
                        },
                    )
                    BottomNavigationItem(
                        selected = false,
                        onClick = { moodMenuExpanded = true },
                        icon = {
                            Icon(Icons.Default.AddReaction, contentDescription = "Mood", tint = Color.White)
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
                            Icon(Icons.Default.Mic, contentDescription = "Voice", tint = Color.White)
                        },
                    )
                }
            }
            // Dropdown menu for mood selection
            if (entry == null && moodMenuExpanded) { // Render mood dropdown only for new entries when expanded
                // Popup to close when clicking outside
                val onDismissRequest = { moodMenuExpanded = false }
                Popup(
                    // alignment = Alignment.TopCenter, // Align to the top center
                    offset = IntOffset(50, 1600), // Offset upward by 50 pixels
                    onDismissRequest = onDismissRequest
                ) {
                    Card(
                        modifier = Modifier.padding(16.dp), // Add padding to the Card
                        elevation = 8.dp,
                        backgroundColor = Color(android.graphics.Color.parseColor("#240743"))
                    ) {
                        Column(
                            modifier = Modifier.padding(vertical = 16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "How are you feeling today?",
                                fontSize = 20.sp,
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.padding(bottom = 8.dp)
                            )
                            Row(
                                modifier = Modifier
                                    .horizontalScroll(rememberScrollState())
                                    .padding(horizontal= 16.dp),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                moods.forEach { (emoji, mood) ->
                                    IconButton(
                                        onClick = {
                                            selectedMood = mood // Set the selected mood
                                            moodMenuExpanded = false
                                        }
                                    ) {
                                        Text(
                                            text = emoji,
                                            fontSize = 36.sp,
                                            modifier = Modifier
                                                .size(72.dp),
                                            textAlign = TextAlign.Center,
                                            color = Color.Black,
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
            // Dropdown menu for sharing options
            if (shareMenuExpanded) { // Render share dropdown only when expanded
                ShareOptionsDropdown(
                    activity = activity,
                    text = textState,
                    expanded = shareMenuExpanded,
                    onDismissRequest = { shareMenuExpanded = false }
                )
            }
        }
    }
}
@Composable
fun ShareOptionsDropdown(
    activity: Activity,
    text: String,
    expanded: Boolean,
    onDismissRequest: () -> Unit
) {
    val shareOptions = listOf("Facebook", "Twitter", "WhatsApp")
    // Popup to close when clicking outside
    Popup(
        alignment = Alignment.TopStart,
        offset = IntOffset(420, 70),
        onDismissRequest = onDismissRequest
    ) {
        Card(
            modifier = Modifier.padding(16.dp),
            elevation = 8.dp,
            backgroundColor = Color(android.graphics.Color.parseColor("#240743"))
        ) {
            Column(
                modifier = Modifier.width(IntrinsicSize.Max)
            ) {
                shareOptions.forEach { option ->
                    DropdownMenuItem(
                        onClick = {
                            onDismissRequest() // Dismiss the dropdown menu
                            handleShareOption(activity, option, text)
                        }
                    ) {
                        Text(
                            text = option,
                            style = TextStyle(color = White), // Set text color to white
                            fontSize = 16.sp,
                        )
                    }
                }
            }
        }
    }
}


fun handleShareOption(activity: Activity, option: String, text: String) {
    when (option) {
        "Facebook" -> shareOnFacebook(activity, text)
        "Twitter" -> shareOnTwitter(activity, text)
        "WhatsApp" -> shareOnWhatsApp(activity, text)
    }
}

fun shareOnFacebook(activity: Activity, text: String) {
    val shareIntent = Intent(Intent.ACTION_SEND)
    shareIntent.type = "text/plain"
    shareIntent.putExtra(Intent.EXTRA_TEXT, text)
    shareIntent.`package` = "com.facebook.katana" // Package name for Facebook
    try {
        activity.startActivity(shareIntent)
    } catch (e: ActivityNotFoundException) {
        Toast.makeText(activity, "Facebook app not installed.", Toast.LENGTH_SHORT).show()
    }
}

fun shareOnTwitter(activity: Activity, text: String) {
    val shareIntent = Intent(Intent.ACTION_SEND)
    shareIntent.type = "text/plain"
    shareIntent.putExtra(Intent.EXTRA_TEXT, text)
    shareIntent.`package` = "com.twitter.android" // Package name for Twitter
    try {
        activity.startActivity(shareIntent)
    } catch (e: ActivityNotFoundException) {
        Toast.makeText(activity, "Twitter app not installed.", Toast.LENGTH_SHORT).show()
    }
}

fun shareOnWhatsApp(activity: Activity, text: String) {
    val shareIntent = Intent(Intent.ACTION_SEND)
    shareIntent.type = "text/plain"
    shareIntent.putExtra(Intent.EXTRA_TEXT, text)
    shareIntent.`package` = "com.whatsapp" // Package name for WhatsApp
    try {
        activity.startActivity(shareIntent)
    } catch (e: ActivityNotFoundException) {
        Toast.makeText(activity, "WhatsApp app not installed.", Toast.LENGTH_SHORT).show()
    }
}