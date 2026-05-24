package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "watchlist_items")
data class WatchlistItem(
    @PrimaryKey val id: String, // composite format: e.g. "movie_123", "tv_456", "anime_789"
    val apiId: Int,
    val title: String,
    val overview: String,
    val posterPath: String?,
    val mediaType: String, // "PELÍCULA", "SERIE", "ANIME"
    val savedAt: Long = System.currentTimeMillis()
)
