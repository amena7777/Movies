package com.example.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.api.SearchResultItem
import com.example.api.SearchService
import com.example.data.WatchlistItem
import com.example.data.WatchlistRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class WatchlistViewModel(
    private val repository: WatchlistRepository,
    private val searchService: SearchService
) : ViewModel() {

    private val _searchQuery = MutableStateFlow("")
    val searchQuery = _searchQuery.asStateFlow()

    private val _selectedCategory = MutableStateFlow("ALL")
    val selectedCategory = _selectedCategory.asStateFlow()

    private val _isSearching = MutableStateFlow(false)
    val isSearching = _isSearching.asStateFlow()

    private val _searchResults = MutableStateFlow<List<SearchResultItem>>(emptyList())
    val searchResults = _searchResults.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage = _errorMessage.asStateFlow()

    val watchlistItems: StateFlow<List<WatchlistItem>> = repository.allItems
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    val savedIds: StateFlow<Set<String>> = repository.allItems
        .map { list -> list.map { it.id }.toSet() }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptySet()
        )

    private var searchJob: Job? = null

    init {
        // Trigger default popular suggestions on startup
        triggerSearch()
    }

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
        if (query.isBlank()) {
            triggerSearch()
        }
    }

    fun setCategory(category: String) {
        _selectedCategory.value = category
        triggerSearch()
    }

    fun triggerSearch() {
        searchJob?.cancel()
        searchJob = viewModelScope.launch {
            _isSearching.value = true
            _errorMessage.value = null
            try {
                // Add a debouncing delay if typing to prevent API spamming
                if (_searchQuery.value.isNotBlank()) {
                    delay(350)
                }
                val results = searchService.search(_searchQuery.value, _selectedCategory.value)
                _searchResults.value = results
            } catch (e: Exception) {
                _errorMessage.value = "Error al realizar la búsqueda: ${e.localizedMessage ?: "Consulte su conexión a Internet."}"
                _searchResults.value = emptyList()
            } finally {
                _isSearching.value = false
            }
        }
    }

    fun toggleWatchlist(item: SearchResultItem) {
        viewModelScope.launch {
            val isSaved = savedIds.value.contains(item.id)
            if (isSaved) {
                repository.removeItem(item.id)
            } else {
                val watchlistItem = WatchlistItem(
                    id = item.id,
                    apiId = item.apiId,
                    title = item.title,
                    overview = item.overview,
                    posterPath = item.posterUrl,
                    mediaType = item.mediaType.label
                )
                repository.insertItem(watchlistItem)
            }
        }
    }

    fun removeWatchlistItem(id: String) {
        viewModelScope.launch {
            repository.removeItem(id)
        }
    }

    fun clearError() {
        _errorMessage.value = null
    }

    class Factory(
        private val repository: WatchlistRepository,
        private val searchService: SearchService
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(WatchlistViewModel::class.java)) {
                return WatchlistViewModel(repository, searchService) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}
