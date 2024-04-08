package com.example.gustavo_final_project

import android.content.Context
import android.net.Uri
import android.os.Parcel
import android.os.Parcelable
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Color.Companion.White
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberImagePainter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.Serializable

data class Entry(
    val text: String,
    val date: String,
    val mood: String? = null,
    var pictureUris: List<Uri> = emptyList()
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString(),
        mutableListOf<Uri>().apply {
            parcel.readList(this, Uri::class.java.classLoader)
        }
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(text)
        parcel.writeString(date)
        parcel.writeString(mood)
        parcel.writeList(pictureUris)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Entry> {
        override fun createFromParcel(parcel: Parcel): Entry {
            return Entry(parcel)
        }

        override fun newArray(size: Int): Array<Entry?> {
            return arrayOfNulls(size)
        }
    }
}

@Composable
fun EntryCard(entry: Entry, onItemClick: (Entry) -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onItemClick(entry) } // Pass the entry object to onItemClick
            .border(1.dp, Color.White, shape = RoundedCornerShape(8.dp))
            .background(Color.White.copy(alpha = 0.10f), shape = RoundedCornerShape(8.dp)),
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = entry.date, fontWeight = FontWeight.Bold, fontSize = 20.sp, color = White,)
            Spacer(modifier = Modifier.height(8.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                entry.mood?.let { mood ->
                    Text(
                        text = mood,
                        fontSize = 24.sp,
                        modifier = Modifier.padding(end = 8.dp),
                    )
                }
                Text(
                    text = entry.text,
                    fontSize = 16.sp,
                    maxLines = 2, // Limit to one line
                    overflow = TextOverflow.Ellipsis, // Truncate overflowed text with ellipsis
                    color = White,)
            }
            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}

fun saveEntries(context: Context, entries: List<Entry>) {
    val sharedPreferences = context.getSharedPreferences("Entries", Context.MODE_PRIVATE)
    val editor = sharedPreferences.edit()
    val gson = Gson()
    val json = gson.toJson(entries)
    editor.putString("entries", json)

    // Save picture URIs for each entry
    entries.forEachIndexed { index, entry ->
        val pictureUrisJson = gson.toJson(entry.pictureUris)
        editor.putString("entry_${index}_picture_uris", pictureUrisJson) // Corrected string interpolation
    }

    editor.apply()
}

fun loadEntries(context: Context): List<Entry> {
    val sharedPreferences = context.getSharedPreferences("Entries", Context.MODE_PRIVATE)
    val gson = Gson()
    val json = sharedPreferences.getString("entries", "")
    val type = object : TypeToken<List<Entry>>() {}.type
    val entries = (gson.fromJson<List<Entry>>(json, type) ?: emptyList()).toMutableList()

    // Populate picture URIs for each entry
    entries.forEachIndexed { index, entry ->
        val pictureUrisJson = sharedPreferences.getString("entry_${index}_picture_uris", "")
        val type = object : TypeToken<List<Uri>>() {}.type
        val pictureUris = gson.fromJson<List<Uri>>(pictureUrisJson, type) ?: emptyList()
        entries[index] = entry.copy(pictureUris = pictureUris)
    }

    return entries
}
