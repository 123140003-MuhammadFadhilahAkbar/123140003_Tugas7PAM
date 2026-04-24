package org.example.project.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.example.project.components.ArticleCard
import org.example.project.model.UiState
import org.example.project.viewmodel.NewsViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BookmarksScreen(
    viewModel      : NewsViewModel,
    onArticleClick : (String) -> Unit,
    onMenuClick    : () -> Unit = {}
) {
    val uiState       by viewModel.uiState.collectAsState()
    val bookmarkedIds by viewModel.bookmarkedIds.collectAsState()

    val bookmarkedArticles = remember(uiState, bookmarkedIds) {
        (uiState as? UiState.Success)?.data
            ?.filter { it.id in bookmarkedIds }
            ?: emptyList()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                navigationIcon = {
                    IconButton(onClick = onMenuClick) {
                        Icon(Icons.Default.Menu, "Menu",
                            tint = MaterialTheme.colorScheme.onSurface)
                    }
                },
                title = {
                    Column {
                        Text("Tersimpan", fontWeight = FontWeight.Bold)
                        Text("${bookmarkedArticles.size} artikel disimpan",
                            fontSize = 11.sp,
                            color    = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface)
            )
        }
    ) { padding ->
        if (bookmarkedArticles.isEmpty()) {
            Box(
                modifier         = Modifier.fillMaxSize().padding(padding),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(Icons.Default.Bookmark, null,
                        tint     = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(64.dp))
                    Spacer(Modifier.height(8.dp))
                    Text("Belum Ada Artikel Tersimpan",
                        fontSize   = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color      = MaterialTheme.colorScheme.onSurface)
                    Text("Tekan ikon 🔖 pada artikel\nuntuk menyimpannya di sini",
                        fontSize  = 13.sp,
                        color     = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center)
                }
            }
        } else {
            LazyColumn(
                modifier            = Modifier.fillMaxSize().padding(padding),
                contentPadding      = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                item {
                    Text("📌 Artikel yang kamu simpan",
                        fontSize   = 12.sp,
                        fontWeight = FontWeight.Medium,
                        color      = MaterialTheme.colorScheme.onSurfaceVariant)
                }
                items(bookmarkedArticles, key = { it.id }) { article ->
                    ArticleCard(
                        article         = article,
                        isBookmarked    = true,
                        onClick         = { onArticleClick(article.id) },
                        onBookmarkClick = { viewModel.toggleBookmark(article.id) }
                    )
                }
                item { Spacer(Modifier.height(80.dp)) }
            }
        }
    }
}
