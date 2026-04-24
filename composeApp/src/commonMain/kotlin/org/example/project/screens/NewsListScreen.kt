package org.example.project.screens

// ================================================================
// NewsListScreen.kt — Tab Berita NewsAPI (Gabungan Tugas 5+6)
//
// Perubahan dari versi Tugas 6:
//   + onMenuClick parameter untuk membuka Navigation Drawer
//   + Hamburger icon di TopAppBar
// ================================================================

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.example.project.components.ArticleCard
import org.example.project.components.CategoryFilterRow
import org.example.project.model.UiState
import org.example.project.viewmodel.NewsViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NewsListScreen(
    viewModel      : NewsViewModel,
    isDarkMode     : Boolean,
    onToggleDark   : () -> Unit,
    onArticleClick : (String) -> Unit,
    onMenuClick    : () -> Unit = {}   // ← untuk buka drawer
) {
    val uiState          by viewModel.uiState.collectAsState()
    val isRefreshing     by viewModel.isRefreshing.collectAsState()
    val bookmarkedIds    by viewModel.bookmarkedIds.collectAsState()
    val searchQuery       = viewModel.searchQuery
    val selectedCategory  = viewModel.selectedCategory

    Scaffold(
        topBar = {
            Column {
                TopAppBar(
                    navigationIcon = {
                        IconButton(onClick = onMenuClick) {
                            Icon(Icons.Default.Menu, "Menu",
                                tint = MaterialTheme.colorScheme.onSurface)
                        }
                    },
                    title = {
                        Column {
                            Text("📰 News Reader",
                                fontWeight = FontWeight.Bold, fontSize = 18.sp)
                            Text(
                                text = when (val s = uiState) {
                                    is UiState.Success -> "${s.data.size} artikel • NewsAPI"
                                    is UiState.Loading -> "Memuat dari NewsAPI..."
                                    is UiState.Error   -> "Gagal memuat"
                                },
                                fontSize = 11.sp,
                                color    = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    },
                    colors  = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    ),
                    actions = {
                        TextButton(onClick = onToggleDark) {
                            Text(if (isDarkMode) "☀️" else "🌙", fontSize = 20.sp)
                        }
                        TextButton(
                            onClick  = viewModel::refresh,
                            enabled  = !isRefreshing && uiState !is UiState.Loading
                        ) {
                            Text("🔄", fontSize = 18.sp)
                        }
                    }
                )

                // ── Search Bar ───────────────────────────────────
                OutlinedTextField(
                    value         = searchQuery,
                    onValueChange = viewModel::onSearchQueryChange,
                    modifier      = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 4.dp),
                    placeholder   = { Text("Cari berita...", fontSize = 13.sp) },
                    leadingIcon   = {
                        Icon(Icons.Default.Search, null,
                            tint = MaterialTheme.colorScheme.onSurfaceVariant)
                    },
                    trailingIcon  = {
                        AnimatedVisibility(
                            visible = searchQuery.isNotEmpty(),
                            enter = fadeIn(), exit = fadeOut()
                        ) {
                            IconButton(onClick = { viewModel.onSearchQueryChange("") }) {
                                Icon(Icons.Default.Clear, "Hapus",
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                        }
                    },
                    singleLine = true,
                    shape      = RoundedCornerShape(12.dp),
                    colors     = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor   = MaterialTheme.colorScheme.primary,
                        unfocusedBorderColor = MaterialTheme.colorScheme.outline
                    )
                )

                AnimatedVisibility(visible = searchQuery.isBlank()) {
                    CategoryFilterRow(
                        categories       = viewModel.categories,
                        selectedCategory = selectedCategory,
                        onCategoryClick  = viewModel::onCategorySelected
                    )
                }

                if (isRefreshing) {
                    LinearProgressIndicator(
                        modifier = Modifier.fillMaxWidth(),
                        color    = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }
    ) { padding ->

        when (val state = uiState) {

            is UiState.Loading -> {
                Box(
                    modifier         = Modifier.fillMaxSize().padding(padding),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        CircularProgressIndicator(
                            color       = MaterialTheme.colorScheme.primary,
                            modifier    = Modifier.size(52.dp),
                            strokeWidth = 4.dp
                        )
                        Text("Mengambil berita terkini...",
                            fontSize = 14.sp,
                            color    = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            }

            is UiState.Success -> {
                LazyColumn(
                    modifier            = Modifier.fillMaxSize().padding(padding),
                    contentPadding      = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    item {
                        Text(
                            text  = if (searchQuery.isNotBlank())
                                        "🔍 Hasil \"$searchQuery\" — ${state.data.size} artikel"
                                    else
                                        "📡 ${state.data.size} berita terkini",
                            fontSize = 12.sp,
                            color    = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    items(state.data, key = { it.id }) { article ->
                        ArticleCard(
                            article         = article,
                            isBookmarked    = article.id in bookmarkedIds,
                            onClick         = { onArticleClick(article.id) },
                            onBookmarkClick = { viewModel.toggleBookmark(article.id) }
                        )
                    }
                    item { Spacer(Modifier.height(80.dp)) }
                }
            }

            is UiState.Error -> {
                Box(
                    modifier         = Modifier.fillMaxSize().padding(padding),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                        modifier            = Modifier.padding(32.dp)
                    ) {
                        Text("📡", fontSize = 56.sp)
                        Text("Gagal Memuat Berita",
                            fontSize   = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color      = MaterialTheme.colorScheme.onSurface)
                        Card(
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.errorContainer),
                            shape  = RoundedCornerShape(12.dp)
                        ) {
                            Text(state.message,
                                fontSize   = 13.sp,
                                color      = MaterialTheme.colorScheme.onErrorContainer,
                                modifier   = Modifier.padding(12.dp),
                                lineHeight = 20.sp)
                        }
                        if (state.message.contains("API key", ignoreCase = true)) {
                            Card(
                                colors = CardDefaults.cardColors(
                                    containerColor = MaterialTheme.colorScheme.primaryContainer),
                                shape  = RoundedCornerShape(12.dp)
                            ) {
                                Text(
                                    "💡 Isi API key di:\nproject/network/NewsApiConfig.kt",
                                    fontSize   = 12.sp,
                                    color      = MaterialTheme.colorScheme.onPrimaryContainer,
                                    modifier   = Modifier.padding(12.dp),
                                    lineHeight = 18.sp
                                )
                            }
                        }
                        if (state.canRetry) {
                            Button(
                                onClick = viewModel::loadTopHeadlines,
                                shape   = RoundedCornerShape(12.dp)
                            ) {
                                Text("🔄  Coba Lagi", fontWeight = FontWeight.SemiBold)
                            }
                        }
                    }
                }
            }
        }
    }
}
