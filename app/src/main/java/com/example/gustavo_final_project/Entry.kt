package com.example.gustavo_final_project

import android.os.Parcel
import android.os.Parcelable
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import java.io.Serializable

data class Entry(
    val text: String,
    val date: String
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString() ?: "",
        parcel.readString() ?: ""
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(text)
        parcel.writeString(date)
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
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onItemClick(entry) }, // Handle click event
        shape = RoundedCornerShape(8.dp), // Adds rounded corners
        border = BorderStroke(1.dp, Color.Gray) // Light gray border
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = entry.date, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = entry.text,
                maxLines = 1, // Limit to one line
                overflow = TextOverflow.Ellipsis // Truncate overflowed text with ellipsis
            )
            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}

