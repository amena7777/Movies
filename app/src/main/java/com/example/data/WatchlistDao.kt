package com.example.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface WatchlistDao {
    @Query("SELECT * FROM watchlist_items ORDER BY savedAt DESC")
    fun getAllItems(): Flow<List<WatchlistItem>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertItem(item: WatchlistItem)

    @Query("DELETE FROM watchlist_items WHERE id = :id")
    suspend fun deleteItemById(id: String)

    @Query("SELECT EXISTS(SELECT 1 FROM watchlist_items WHERE id = :id LIMIT 1)")
    fun existsFlow(id: String): Flow<Boolean>
}
