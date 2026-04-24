package org.example.project.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.example.project.components.ArticleImagePlaceholder
import org.example.project.components.CategoryChip
import org.example.project.model.UiState
import org.example.project.viewmodel.NewsViewModel

@Suppress("DEPRECATION")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ArticleDetailScreen(
    articleId : String,
    viewModel : NewsViewModel,
    onBack    : () -> Unit
) {
    val uiState      by viewModel.uiState.collectAsState()
    val bookmarkedIds by viewModel.bookmarkedIds.collectAsState()

    val article      = (uiState as? UiState.Success)?.data?.find { it.id == articleId }
    val isBookmarked = articleId in bookmarkedIds

    Scaffold(
        topBar = {
            TopAppBar(
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, "Kembali")
                    }
                },
                title  = { Text("Detail Artikel", fontWeight = FontWeight.SemiBold) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                ),
                actions = {
                    IconButton(onClick = { viewModel.toggleBookmark(articleId) }) {
                        Icon(
                            imageVector        = if (isBookmarked) Icons.Filled.Bookmark
                            else Icons.Default.BookmarkBorder,
                            contentDescription = "Simpan",
                            tint               = if (isBookmarked)
                                MaterialTheme.colorScheme.primary
                            else
                                MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            )
        }
    ) { padding ->

        if (article == null) {
            Box(
                modifier         = Modifier.fillMaxSize().padding(padding),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text("❌", fontSize = 48.sp)
                    Text("Artikel tidak ditemukan",
                        color = MaterialTheme.colorScheme.onSurface, fontSize = 16.sp)
                    Button(onClick = onBack) { Text("Kembali") }
                }
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .verticalScroll(rememberScrollState())
            ) {
                ArticleImagePlaceholder(article = article, height = 220)

                Column(modifier = Modifier.padding(20.dp)) {

                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment     = Alignment.CenterVertically
                    ) {
                        CategoryChip(category = article.category)
                        Text(
                            text     = article.sourceName,
                            fontSize = 11.sp,
                            color    = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.SemiBold
                        )
                    }

                    Spacer(Modifier.height(10.dp))

                    Text(
                        text       = article.safeTitle,
                        fontSize   = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color      = MaterialTheme.colorScheme.onSurface,
                        lineHeight = 28.sp
                    )

                    Spacer(Modifier.height(8.dp))

                    Row(
                        modifier              = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment     = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text     = "✍️ ${article.author ?: "Unknown Author"}",
                                fontSize = 12.sp,
                                color    = MaterialTheme.colorScheme.onSurfaceVariant,
                                fontWeight = FontWeight.Medium
                            )
                            Text(
                                text     = "🗓 ${article.formattedDate}  •  ${article.readTime}",
                                fontSize = 11.sp,
                                color    = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    Spacer(Modifier.height(16.dp))
                    HorizontalDivider()
                    Spacer(Modifier.height(16.dp))

                    if (!article.description.isNullOrBlank()) {
                        Text("Ringkasan",
                            fontSize   = 13.sp,
                            fontWeight = FontWeight.SemiBold,
                            color      = MaterialTheme.colorScheme.primary)
                        Spacer(Modifier.height(6.dp))
                        Text(
                            text       = article.safeDescription,
                            fontSize   = 15.sp,
                            color      = MaterialTheme.colorScheme.onSurface,
                            lineHeight = 24.sp,
                            textAlign  = TextAlign.Justify
                        )
                        Spacer(Modifier.height(16.dp))
                    }

                    val content = article.content?.let {
                        // NewsAPI memotong content di 200 karakter + "[+N chars]"
                        // Hapus suffix itu
                        it.substringBefore(" [+").trim()
                    }

                    if (!content.isNullOrBlank()) {
                        Text("Konten",
                            fontSize   = 13.sp,
                            fontWeight = FontWeight.SemiBold,
                            color      = MaterialTheme.colorScheme.primary)
                        Spacer(Modifier.height(6.dp))
                        Text(
                            text       = content,
                            fontSize   = 15.sp,
                            color      = MaterialTheme.colorScheme.onSurface,
                            lineHeight = 24.sp,
                            textAlign  = TextAlign.Justify
                        )
                        Spacer(Modifier.height(8.dp))
                        Text(
                            "ℹ️ Konten dipotong oleh NewsAPI free plan. Baca selengkapnya di sumber asli.",
                            fontSize   = 11.sp,
                            color      = MaterialTheme.colorScheme.onSurfaceVariant,
                            lineHeight = 16.sp
                        )
                    }

                    Spacer(Modifier.height(16.dp))

                    Card(
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surfaceVariant),
                        shape  = RoundedCornerShape(12.dp)
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text("🔗 Sumber Asli",
                                fontSize   = 12.sp,
                                fontWeight = FontWeight.SemiBold,
                                color      = MaterialTheme.colorScheme.onSurfaceVariant)
                            Spacer(Modifier.height(4.dp))
                            Text(
                                text     = article.url,
                                fontSize = 11.sp,
                                color    = MaterialTheme.colorScheme.primary,
                                lineHeight = 16.sp
                            )
                        }
                    }

                    Spacer(Modifier.height(16.dp))

                    Button(
                        onClick  = { viewModel.toggleBookmark(articleId) },
                        modifier = Modifier.fillMaxWidth().height(50.dp),
                        shape    = RoundedCornerShape(12.dp),
                        colors   = ButtonDefaults.buttonColors(
                            containerColor = if (isBookmarked)
                                MaterialTheme.colorScheme.secondaryContainer
                            else
                                MaterialTheme.colorScheme.primary
                        )
                    ) {
                        Icon(
                            imageVector = if (isBookmarked) Icons.Filled.Bookmark
                            else Icons.Default.BookmarkBorder,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp),
                            tint     = if (isBookmarked)
                                MaterialTheme.colorScheme.onSecondaryContainer
                            else
                                MaterialTheme.colorScheme.onPrimary
                        )
                        Spacer(Modifier.width(8.dp))
                        Text(
                            text       = if (isBookmarked) "✓ Tersimpan" else "Simpan Artikel",
                            fontWeight = FontWeight.SemiBold,
                            color      = if (isBookmarked)
                                MaterialTheme.colorScheme.onSecondaryContainer
                            else
                                MaterialTheme.colorScheme.onPrimary
                        )
                    }

                    Spacer(Modifier.height(32.dp))
                }
            }
        }
    }
}
