package com.example.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.CompassCalibration
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Movie
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.VideoLibrary
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import coil.compose.AsyncImage
import com.example.api.MediaCategory
import com.example.api.SearchResultItem
import com.example.data.WatchlistItem

// Beautiful Sleek Interface Theme
val ObsidianBg = Color(0xFF0A0A0A)
val ObsidianCard = Color(0xFF161618)
val ObsidianAccentTeal = Color(0xFFA5C9FF) // sleek light blue
val ObsidianAccentGreen = Color(0xFF00E676)
val TextPrimary = Color(0xFFF1F5F9) // slate-100
val TextSecondary = Color(0xFF94A3B8) // slate-400

// Category Badge Background & Text Colors
val BadgeAnimeBg = Color(0xFFA5C9FF)
val BadgeAnimeText = Color(0xFF00315C)
val BadgeMovieBg = Color(0xFFD9E2FF)
val BadgeMovieText = Color(0xFF001945)
val BadgeSerieBg = Color(0xFFF2B8B5)
val BadgeSerieText = Color(0xFF601410)

@OptIn(ExperimentalLayoutApi::class, ExperimentalMaterial3Api::class)
@Composable
fun WatchlistScreen(viewModel: WatchlistViewModel) {
    val searchQuery by viewModel.searchQuery.collectAsState()
    val selectedCategory by viewModel.selectedCategory.collectAsState()
    val isSearching by viewModel.isSearching.collectAsState()
    val searchResults by viewModel.searchResults.collectAsState()
    val watchlistItems by viewModel.watchlistItems.collectAsState()
    val savedIds by viewModel.savedIds.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()

    var activeTab by remember { mutableStateOf(0) } // 0 = explorar, 1 = mi watchlist
    var selectedItemForDetail by remember { mutableStateOf<SearchResultItem?>(null) }
    var selectedWatchlistItemForDetail by remember { mutableStateOf<WatchlistItem?>(null) }

    val focusManager = LocalFocusManager.current

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .background(ObsidianBg),
        containerColor = ObsidianBg
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            Column(
                modifier = Modifier.fillMaxSize()
            ) {
            // Header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.VideoLibrary,
                        contentDescription = "Watchlist Logo",
                        tint = ObsidianAccentTeal,
                        modifier = Modifier.size(32.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = "Watchlist",
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Text(
                            text = "Tu inventario visual de cine",
                            fontSize = 12.sp,
                            color = TextSecondary
                        )
                    }
                }

                // Profile JD Icon as specified in "Sleek Interface" Design
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(Color(0xFF1A1C1E))
                        .border(1.dp, Color(0x0EFFFFFF), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "JD",
                        color = Color(0xFFA5C9FF),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            // Tab layout (Explorar vs Mi Lista)
            TabRow(
                selectedTabIndex = activeTab,
                containerColor = ObsidianCard,
                contentColor = TextSecondary,
                indicator = { tabPositions ->
                    TabRowDefaults.SecondaryIndicator(
                        modifier = Modifier.tabIndicatorOffset(tabPositions[activeTab]),
                        color = ObsidianAccentTeal
                    )
                },
                modifier = Modifier
                    .padding(horizontal = 16.dp, vertical = 6.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .border(1.dp, Color(0x0EFFFFFF), RoundedCornerShape(12.dp))
            ) {
                Tab(
                    selected = activeTab == 0,
                    onClick = { activeTab = 0 },
                    modifier = Modifier.testTag("tab_explore"),
                    text = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.PlayArrow,
                                contentDescription = "Explorar",
                                modifier = Modifier.size(18.dp),
                                tint = if (activeTab == 0) ObsidianAccentTeal else TextSecondary
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                "Explorar",
                                fontWeight = if (activeTab == 0) FontWeight.Bold else FontWeight.Normal,
                                color = if (activeTab == 0) ObsidianAccentTeal else TextSecondary
                            )
                        }
                    }
                )
                Tab(
                    selected = activeTab == 1,
                    onClick = { activeTab = 1 },
                    modifier = Modifier.testTag("tab_watchlist"),
                    text = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Bookmark,
                                contentDescription = "Mi Lista",
                                modifier = Modifier.size(18.dp),
                                tint = if (activeTab == 1) ObsidianAccentTeal else TextSecondary
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                "Mi Lista (${watchlistItems.size})",
                                fontWeight = if (activeTab == 1) FontWeight.Bold else FontWeight.Normal,
                                color = if (activeTab == 1) ObsidianAccentTeal else TextSecondary
                            )
                        }
                    }
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Body
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            ) {
                if (activeTab == 0) {
                    // SEARCH & DISCOVER VIEW
                    Column(modifier = Modifier.fillMaxSize()) {
                        // Prominent Search Controller
                        OutlinedTextField(
                            value = searchQuery,
                            onValueChange = { viewModel.setSearchQuery(it) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp)
                                .testTag("search_input"),
                            placeholder = { Text("Buscar películas, series, anime...", color = Color.Gray) },
                            leadingIcon = {
                                Icon(
                                    imageVector = Icons.Default.Search,
                                    contentDescription = "Search Icon",
                                    tint = TextSecondary
                                )
                            },
                            trailingIcon = {
                                if (searchQuery.isNotEmpty()) {
                                    IconButton(
                                        onClick = { viewModel.setSearchQuery("") },
                                        modifier = Modifier.testTag("search_clear_button")
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Close,
                                            contentDescription = "Borrar búsqueda",
                                            tint = TextSecondary
                                        )
                                    }
                                }
                            },
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(
                                imeAction = ImeAction.Search
                            ),
                            keyboardActions = KeyboardActions(
                                onSearch = {
                                    viewModel.triggerSearch()
                                    focusManager.clearFocus()
                                }
                            ),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White,
                                focusedBorderColor = ObsidianAccentTeal,
                                unfocusedBorderColor = Color.Transparent,
                                focusedContainerColor = Color(0xFF1A1C1E),
                                unfocusedContainerColor = Color(0xFF1A1C1E),
                                cursorColor = ObsidianAccentTeal,
                                focusedPlaceholderColor = Color.LightGray,
                                unfocusedPlaceholderColor = Color.Gray
                            ),
                            shape = RoundedCornerShape(16.dp)
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        // Filter Chips Row
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            val categories = listOf(
                                "ALL" to "Todos",
                                MediaCategory.PELICULA.label to "Películas",
                                MediaCategory.SERIE.label to "Series",
                                MediaCategory.ANIME.label to "Animes"
                            )

                            categories.forEach { (key, label) ->
                                val isSelected = selectedCategory == key
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(20.dp))
                                        .background(if (isSelected) ObsidianAccentTeal else Color(0xFF1A1C1E))
                                        .border(
                                            width = 1.dp,
                                            color = if (isSelected) Color.Transparent else Color(0x0EFFFFFF),
                                            shape = RoundedCornerShape(20.dp)
                                        )
                                        .clickable { 
                                            viewModel.setCategory(key)
                                        }
                                        .padding(horizontal = 16.dp, vertical = 8.dp)
                                        .testTag("filter_$key"),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = label,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (isSelected) Color(0xFF00315C) else Color(0xFFCBD5E1)
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        // Results grid
                        if (isSearching) {
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(bottom = 60.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                CircularProgressIndicator(color = ObsidianAccentTeal)
                            }
                        } else if (searchResults.isEmpty()) {
                            // Empty State Search
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(horizontal = 32.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Movie,
                                        contentDescription = "No results",
                                        tint = TextSecondary.copy(alpha = 0.5f),
                                        modifier = Modifier.size(64.dp)
                                    )
                                    Spacer(modifier = Modifier.height(16.dp))
                                    Text(
                                        "No se encontraron títulos",
                                        fontSize = 18.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White,
                                        textAlign = TextAlign.Center
                                    )
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Text(
                                        "Intente con otras palabras clave o asegúrese de que su búsqueda tenga nombres bien deletreados.",
                                        fontSize = 13.sp,
                                        color = TextSecondary,
                                        textAlign = TextAlign.Center
                                    )
                                }
                            }
                        } else {
                            LazyVerticalGrid(
                                columns = GridCells.Fixed(2),
                                contentPadding = PaddingValues(12.dp),
                                horizontalArrangement = Arrangement.spacedBy(10.dp),
                                verticalArrangement = Arrangement.spacedBy(12.dp),
                                modifier = Modifier.fillMaxSize()
                            ) {
                                items(searchResults, key = { it.id }) { item ->
                                    val isSaved = savedIds.contains(item.id)
                                    ContentGridCard(
                                        title = item.title,
                                        posterUrl = item.posterUrl,
                                        mediaTypeLabel = item.mediaType.label,
                                        isSaved = isSaved,
                                        onToggleWatchlist = { viewModel.toggleWatchlist(item) },
                                        onCardClick = { selectedItemForDetail = item },
                                        modifier = Modifier.testTag("card_${item.id}")
                                    )
                                }
                            }
                        }
                    }
                } else {
                    // LOCAL WATCHLIST VIEW
                    if (watchlistItems.isEmpty()) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(horizontal = 32.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.BookmarkBorder,
                                    contentDescription = "Lista Vacía",
                                    tint = TextSecondary.copy(alpha = 0.4f),
                                    modifier = Modifier.size(72.dp)
                                )
                                Spacer(modifier = Modifier.height(16.dp))
                                Text(
                                    "Tu Lista de Seguimiento está vacía",
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White,
                                    textAlign = TextAlign.Center
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    "Pulsa sobre la pestaña 'Explorar' para buscar películas, series y anime, y añádelos a tu colección para recordarlos.",
                                    fontSize = 13.sp,
                                    color = TextSecondary,
                                    textAlign = TextAlign.Center
                                )
                            }
                        }
                    } else {
                        LazyVerticalGrid(
                            columns = GridCells.Fixed(2),
                            contentPadding = PaddingValues(12.dp),
                            horizontalArrangement = Arrangement.spacedBy(10.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp),
                            modifier = Modifier.fillMaxSize()
                        ) {
                            items(watchlistItems, key = { it.id }) { item ->
                                ContentGridCard(
                                    title = item.title,
                                    posterUrl = item.posterPath,
                                    mediaTypeLabel = item.mediaType,
                                    isSaved = true,
                                    onToggleWatchlist = { viewModel.removeWatchlistItem(item.id) },
                                    onCardClick = { 
                                        // Map watchlistItem to searchResultItem representation for the detail sheet
                                        val mappedItem = SearchResultItem(
                                            id = item.id,
                                            apiId = item.apiId,
                                            title = item.title,
                                            overview = item.overview,
                                            posterUrl = item.posterPath,
                                            mediaType = when (item.mediaType) {
                                                MediaCategory.PELICULA.label -> MediaCategory.PELICULA
                                                MediaCategory.SERIE.label -> MediaCategory.SERIE
                                                else -> MediaCategory.ANIME
                                            }
                                        )
                                        selectedItemForDetail = mappedItem
                                    },
                                    modifier = Modifier.testTag("watchlist_card_${item.id}")
                                )
                            }
                        }
                    }
                }
            }
        }

        // Floating Snackbar for Error Messages
        AnimatedVisibility(
            visible = errorMessage != null,
            enter = fadeIn() + slideInVertically(initialOffsetY = { it }),
            exit = fadeOut() + slideOutVertically(targetOffsetY = { it }),
            modifier = Modifier.align(Alignment.BottomCenter)
        ) {
            errorMessage?.let { msg ->
                Snackbar(
                    modifier = Modifier.padding(16.dp),
                    action = {
                        TextButton(
                            onClick = { viewModel.clearError() },
                            colors = ButtonDefaults.textButtonColors(contentColor = ObsidianAccentTeal)
                        ) {
                            Text("CERRAR")
                        }
                    },
                    containerColor = Color(0xFFDC2626),
                    contentColor = Color.White
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Warning, contentDescription = "Error", tint = Color.White)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(msg)
                    }
                }
            }
        }

        // Custom Modal Details Dialog
        selectedItemForDetail?.let { item ->
            val isSaved = savedIds.contains(item.id)
            DetailDialog(
                item = item,
                isSaved = isSaved,
                onToggleWatchlist = { 
                    viewModel.toggleWatchlist(item)
                },
                onDismiss = { selectedItemForDetail = null }
            )
        }
        }
    }
}

@Composable
fun ContentGridCard(
    title: String,
    posterUrl: String?,
    mediaTypeLabel: String,
    isSaved: Boolean,
    onToggleWatchlist: () -> Unit,
    onCardClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val (badgeBg, badgeText) = when (mediaTypeLabel.uppercase()) {
        "ANIME" -> Pair(BadgeAnimeBg, BadgeAnimeText)
        "SERIE" -> Pair(BadgeSerieBg, BadgeSerieText)
        else -> Pair(BadgeMovieBg, BadgeMovieText)
    }

    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = ObsidianCard),
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onCardClick)
            .border(
                width = 1.dp,
                color = if (isSaved) ObsidianAccentTeal.copy(alpha = 0.4f) else Color(0x0EFFFFFF),
                shape = RoundedCornerShape(16.dp)
            )
    ) {
        Column {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(0.7f)
                    .background(Color(0xFF1E242B))
            ) {
                if (!posterUrl.isNullOrBlank()) {
                    AsyncImage(
                        model = posterUrl,
                        contentDescription = "Poster de $title",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                } else {
                    // Elegant Monogram Art Fallback for unknown images
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                Brush.verticalGradient(
                                    listOf(Color(0xFF1A1C1E), Color(0xFF0A0A0A))
                                )
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(
                                imageVector = Icons.Default.Movie,
                                contentDescription = "Sin Imagen",
                                tint = ObsidianAccentTeal.copy(alpha = 0.4f),
                                modifier = Modifier.size(36.dp)
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = title.take(1).uppercase(),
                                fontSize = 48.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary.copy(alpha = 0.8f)
                            )
                        }
                    }
                }

                // Header Badges
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Category Badge
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(badgeBg)
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = mediaTypeLabel,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = badgeText
                        )
                    }

                    // Floating Saved Check
                    IconButton(
                        onClick = onToggleWatchlist,
                        modifier = Modifier
                            .size(28.dp)
                            .clip(CircleShape)
                            .background(Color.Black.copy(alpha = 0.7f))
                            .border(1.dp, Color(0x20FFFFFF), CircleShape)
                            .testTag("bookmark_toggle"),
                        colors = IconButtonDefaults.iconButtonColors(contentColor = Color.White)
                    ) {
                        Icon(
                            imageVector = if (isSaved) Icons.Default.Bookmark else Icons.Default.BookmarkBorder,
                            contentDescription = if (isSaved) "Quitar de lista" else "Agregar a lista",
                            tint = if (isSaved) ObsidianAccentTeal else Color.White,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }

            // Card Text Details
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp)
            ) {
                Text(
                    text = title,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(4.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Info,
                        contentDescription = "Ver detalles",
                        tint = ObsidianAccentTeal,
                        modifier = Modifier.size(12.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "Ver sinopsis",
                        fontSize = 11.sp,
                        color = TextSecondary,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }
    }
}

@Composable
fun DetailDialog(
    item: SearchResultItem,
    isSaved: Boolean,
    onToggleWatchlist: () -> Unit,
    onDismiss: () -> Unit
) {
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(
            usePlatformDefaultWidth = false
        )
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.85f))
                .clickable { onDismiss() },
            contentAlignment = Alignment.Center
        ) {
            Card(
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = ObsidianCard),
                modifier = Modifier
                    .fillMaxWidth(0.9f)
                    .fillMaxHeight(0.78f)
                    .clickable(enabled = false) { }
                    .border(1.dp, Color(0x0EFFFFFF), RoundedCornerShape(24.dp))
            ) {
                Column(
                    modifier = Modifier.fillMaxSize()
                ) {
                    // Poster Banner with Close Action
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(0.42f)
                            .background(Color(0xFF0F1216))
                    ) {
                        if (!item.posterUrl.isNullOrBlank()) {
                            AsyncImage(
                                model = item.posterUrl,
                                contentDescription = item.title,
                                contentScale = ContentScale.Crop,
                                modifier = Modifier.fillMaxSize()
                            )
                        } else {
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .background(
                                        Brush.verticalGradient(
                                            listOf(Color(0xFF1E2835), Color(0xFF0D131A))
                                        )
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = item.title,
                                    fontSize = 24.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White,
                                    textAlign = TextAlign.Center,
                                    modifier = Modifier.padding(16.dp)
                                )
                            }
                        }

                        // Gradient protection overlap
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(
                                    Brush.verticalGradient(
                                        colors = listOf(
                                            Color.Black.copy(alpha = 0.4f),
                                            Color.Transparent,
                                            ObsidianCard
                                        )
                                    )
                                )
                        )

                        // Close trigger floating
                        IconButton(
                            onClick = onDismiss,
                            modifier = Modifier
                                .align(Alignment.TopEnd)
                                .padding(16.dp)
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(Color.Black.copy(alpha = 0.6f))
                                .testTag("detail_close"),
                            colors = IconButtonDefaults.iconButtonColors(contentColor = Color.White)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Cerrar",
                                modifier = Modifier.size(20.dp)
                            )
                        }

                        // Badges aligned bottom of the banner
                        Row(
                            modifier = Modifier
                                .align(Alignment.BottomStart)
                                .padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            val (badgeBg, badgeText) = when (item.mediaType.label.uppercase()) {
                                "ANIME" -> Pair(BadgeAnimeBg, BadgeAnimeText)
                                "SERIE" -> Pair(BadgeSerieBg, BadgeSerieText)
                                else -> Pair(BadgeMovieBg, BadgeMovieText)
                            }
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(badgeBg)
                                    .padding(horizontal = 10.dp, vertical = 5.dp)
                            ) {
                                Text(
                                    text = item.mediaType.label,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = badgeText
                                )
                            }
                        }
                    }

                    // Bottom info block
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(0.58f)
                            .padding(20.dp)
                    ) {
                        Text(
                            text = item.title,
                            fontSize = 22.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = Color.White,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        // Scrollable synopsis block
                        Column(
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxWidth()
                        ) {
                            Text(
                                text = "Sinopsis Oficial",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = ObsidianAccentTeal,
                                modifier = Modifier.padding(bottom = 4.dp)
                            )
                            Text(
                                text = item.overview,
                                fontSize = 13.5.sp,
                                color = TextPrimary,
                                lineHeight = 19.sp,
                                maxLines = 7,
                                overflow = TextOverflow.Ellipsis
                            )
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // Dialog Action Buttons
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            // Close dialog action
                            OutlinedButtonCustom(
                                text = "Cerrar",
                                onClick = onDismiss,
                                modifier = Modifier.weight(0.35f)
                            )

                            // Save Toggle Action
                            Button(
                                onClick = {
                                    onToggleWatchlist()
                                    onDismiss()
                                },
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier
                                    .weight(0.65f)
                                    .height(48.dp)
                                    .testTag("detail_save_toggle"),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = if (isSaved) Color(0xFFEF4444) else ObsidianAccentTeal,
                                    contentColor = if (isSaved) Color.White else Color(0xFF00315C)
                                )
                            ) {
                                Icon(
                                    imageVector = if (isSaved) Icons.Default.Delete else Icons.Default.Bookmark,
                                    contentDescription = null,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = if (isSaved) "Remover de Lista" else "Añadir a Lista",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun OutlinedButtonCustom(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .height(48.dp)
            .border(1.dp, Color(0x0EFFFFFF), RoundedCornerShape(12.dp))
            .clip(RoundedCornerShape(12.dp))
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            color = Color.White,
            fontWeight = FontWeight.SemiBold,
            fontSize = 13.sp
        )
    }
}
