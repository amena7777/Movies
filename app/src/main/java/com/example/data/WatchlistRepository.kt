package com.example.data

import kotlinx.coroutines.flow.Flow

class WatchlistRepository(private val watchlistDao: WatchlistDao) {
    val allItems: Flow<List<WatchlistItem>> = watchlistDao.getAllItems()

    suspend fun insertItem(item: WatchlistItem) {
        watchlistDao.insertItem(item)
    }

    suspend fun removeItem(id: String) {
        watchlistDao.deleteItemById(id)
    }

    fun isSavedFlow(id: String): Flow<Boolean> {
        return watchlistDao.existsFlow(id)
    }
}
