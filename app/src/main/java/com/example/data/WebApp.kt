package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "web_apps")
data class WebApp(
    @PrimaryKey(autoGenerate = true) val id: Long = 0L,
    val name: String,
    val url: String,
    val iconName: String = "language",
    val colorHex: String = "#03A9F4",
    val isPinned: Boolean = false,
    val notes: String = "",
    val desktopMode: Boolean = false,
    val textZoom: Int = 100,
    val usageCount: Int = 0,
    val lastUsedTimestamp: Long = 0L
)
