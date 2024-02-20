package com.example.gustavo_final_project

import android.os.Parcel
import android.os.Parcelable
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.io.Serializable

data class Entry(
    val text: String,
    val date: String,
    val mood: String? = null // Nullable mood field
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString()
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(text)
        parcel.writeString(date)
        parcel.writeString(mood)
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
            .clickable { onItemClick(entry) } // Handle click event
            .border(1.dp, Color.Gray, shape = RoundedCornerShape(8.dp)),
    ){
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = entry.date, fontWeight = FontWeight.Bold, fontSize = 18.sp)
            Spacer(modifier = Modifier.height(8.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                entry.mood?.let { mood ->
                    Text(
                        text = mood,
                        fontSize = 24.sp,
                        modifier = Modifier.padding(end = 8.dp)
                    )
                }
                Text(
                    text = entry.text,
                    fontSize = 14.sp,
                    maxLines = 2, // Limit to one line
                    overflow = TextOverflow.Ellipsis // Truncate overflowed text with ellipsis
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}



