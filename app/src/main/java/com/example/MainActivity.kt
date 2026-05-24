package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.example.api.SearchService
import com.example.data.WatchlistDatabase
import com.example.data.WatchlistRepository
import com.example.ui.WatchlistScreen
import com.example.ui.WatchlistViewModel
import com.example.ui.theme.MyApplicationTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Local Persistence and Search Service instantiation
        val database = WatchlistDatabase.getDatabase(applicationContext)
        val repository = WatchlistRepository(database.watchlistDao())
        val searchService = SearchService()

        // ViewModel Factory setup
        val factory = WatchlistViewModel.Factory(repository, searchService)
        val viewModel: WatchlistViewModel by viewModels { factory }

        setContent {
            MyApplicationTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialThemeColorsFallback.background() // fallback color representation safely handled by Theme.kt
                ) {
                    WatchlistScreen(viewModel = viewModel)
                }
            }
        }
    }
}

// Simple fallback color safe representation keeping dependencies stable
object MaterialThemeColorsFallback {
    @androidx.compose.runtime.Composable
    fun background() = androidx.compose.material3.MaterialTheme.colorScheme.background
}

