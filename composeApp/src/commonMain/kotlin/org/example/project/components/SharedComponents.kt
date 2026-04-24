package org.example.project.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.BrokenImage
import androidx.compose.material.icons.filled.Image
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.example.project.model.Article

import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import coil3.compose.LocalPlatformContext
import coil3.request.ImageRequest
import coil3.request.crossfade
import kotlin.collections.forEach
import kotlin.text.isNotBlank
import kotlin.text.lowercase
import kotlin.text.replaceFirstChar
import kotlin.text.substringAfter
import kotlin.text.substringBefore
import kotlin.text.take
import kotlin.text.uppercaseChar
import kotlin.to

@Composable
fun LoadingScreen(message: String = "Memuat berita...") {
    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            CircularProgressIndicator(
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(52.dp),
                strokeWidth = 4.dp
            )
            Text(
                message, fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
fun ErrorScreen(
    message  : String,
    canRetry : Boolean = true,
    onRetry  : () -> Unit = {}
) {
    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.padding(32.dp)
        ) {
            Text("📡", fontSize = 56.sp)
            Text(
                "Gagal Memuat Berita",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.errorContainer
                ),
                shape = androidx.compose.foundation.shape.RoundedCornerShape(12.dp)
            ) {
                Text(
                    message,
                    fontSize = 13.sp,
                    color = MaterialTheme.colorScheme.onErrorContainer,
                    modifier = Modifier.padding(12.dp),
                    lineHeight = 20.sp
                )
            }
            if (canRetry) {
                Button(
                    onClick = onRetry,
                    shape = androidx.compose.foundation.shape.RoundedCornerShape(12.dp)
                ) {
                    Text("🔄  Coba Lagi", fontWeight = FontWeight.SemiBold)
                }
            }
        }
    }
}

@Composable
fun CategoryChip(category: String, small: Boolean = false) {
    val (bgColor, textColor) = when (category.lowercase()) {
        "technology", "teknologi" -> Color(0xFFEFF6FF) to Color(0xFF1D4ED8)
        "science", "sains"        -> Color(0xFFF0FDF4) to Color(0xFF15803D)
        "business", "bisnis"      -> Color(0xFFFFFBEB) to Color(0xFFB45309)
        "health", "kesehatan"     -> Color(0xFFFFF1F2) to Color(0xFFBE123C)
        "sports", "olahraga"      -> Color(0xFFECFDF5) to Color(0xFF047857)
        "entertainment"           -> Color(0xFFFDF4FF) to Color(0xFF7E22CE)
        else                      -> Color(0xFFF5F3FF) to Color(0xFF6D28D9)
    }
    val label = when (category.lowercase()) {
        "technology"    -> "Teknologi"
        "science"       -> "Sains"
        "business"      -> "Bisnis"
        "health"        -> "Kesehatan"
        "sports"        -> "Olahraga"
        "entertainment" -> "Hiburan"
        "top headlines" -> "Top Headlines"
        else            -> category.replaceFirstChar { it.uppercaseChar() }
    }
    Surface(shape = androidx.compose.foundation.shape.RoundedCornerShape(20.dp), color = bgColor) {
        Text(
            text = label,
            fontSize = if (small) 10.sp else 11.sp,
            fontWeight = FontWeight.SemiBold,
            color = textColor,
            modifier = Modifier.padding(
                horizontal = if (small) 8.dp else 10.dp,
                vertical = if (small) 3.dp else 4.dp
            )
        )
    }
}

@Composable
fun CategoryFilterRow(
    categories       : List<String>,
    selectedCategory : String,
    onCategoryClick  : (String) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState())
            .padding(horizontal = 16.dp, vertical = 6.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        categories.forEach { category ->
            val isSelected = category == selectedCategory
            val label = when (category.lowercase()) {
                "top headlines" -> "🔥 Top"
                "technology" -> "💻 Teknologi"
                "business" -> "💼 Bisnis"
                "health" -> "🏥 Kesehatan"
                "science" -> "🔬 Sains"
                "sports" -> "⚽ Olahraga"
                "entertainment" -> "🎬 Hiburan"
                else -> category
            }
            FilterChip(
                selected = isSelected,
                onClick = { onCategoryClick(category) },
                label = { Text(label, fontSize = 12.sp) },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = MaterialTheme.colorScheme.primary,
                    selectedLabelColor = MaterialTheme.colorScheme.onPrimary,
                    containerColor = MaterialTheme.colorScheme.surfaceVariant,
                    labelColor = MaterialTheme.colorScheme.onSurfaceVariant
                )
            )
        }
    }
}

@Composable
fun ArticleImagePlaceholder(
    article  : Article,
    modifier : Modifier = Modifier,
    height   : Int = 180
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(height.dp)
            .clip(RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp))
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color(article.avatarColor),
                        Color(article.avatarColor).copy(alpha = 0.7f)
                    )
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Tunjukkan bahwa ada gambar dari URL
            if (article.urlToImage != null) {
                Icon(
                    imageVector = Icons.Default.Image,
                    contentDescription = null,
                    tint = Color.White.copy(alpha = 0.8f),
                    modifier = Modifier.size(32.dp)
                )
                Text(
                    text = article.sourceName,
                    color = Color.White,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold
                )
                // Tampilkan domain gambar sebagai info
                val imageDomain = try {
                    article.urlToImage
                        .substringAfter("://")
                        .substringBefore("/")
                        .take(30)
                } catch (e: Exception) {
                    ""
                }
                if (imageDomain.isNotBlank()) {
                    Text(
                        text = "📷 $imageDomain",
                        color = Color.White.copy(alpha = 0.7f),
                        fontSize = 10.sp
                    )
                }
            } else {
                Icon(
                    imageVector = Icons.Default.BrokenImage,
                    contentDescription = null,
                    tint = Color.White.copy(alpha = 0.5f),
                    modifier = Modifier.size(28.dp)
                )
                Text(
                    "Tidak ada gambar",
                    color = Color.White.copy(alpha = 0.5f), fontSize = 11.sp
                )
            }
        }
    }
}

@Composable
fun ArticleCard(
    article        : Article,
    isBookmarked   : Boolean = false,
    onClick        : () -> Unit,
    onBookmarkClick: () -> Unit = {}
) {
    Card(
        modifier = Modifier.fillMaxWidth().clickable { onClick() },
        shape = androidx.compose.foundation.shape.RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column {
            AsyncImage(
                model = ImageRequest.Builder(LocalPlatformContext.current)
                    .data(article.urlToImage)
                    .crossfade(true)
                    .build(),
                contentDescription = article.safeTitle,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )

            Column(modifier = Modifier.padding(14.dp)) {

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        CategoryChip(category = article.category, small = true)
                        Text(
                            text = article.sourceName,
                            fontSize = 10.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            fontWeight = FontWeight.Medium
                        )
                    }
                    IconButton(
                        onClick = onBookmarkClick,
                        modifier = Modifier.size(28.dp)
                    ) {
                        Icon(
                            imageVector = if (isBookmarked) Icons.Filled.Bookmark
                            else Icons.Default.BookmarkBorder,
                            contentDescription = "Simpan",
                            tint = if (isBookmarked) MaterialTheme.colorScheme.primary
                            else MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }

                Spacer(Modifier.height(6.dp))

                Text(
                    text = article.safeTitle,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    lineHeight = 21.sp
                )

                Spacer(Modifier.height(4.dp))
                
                Text(
                    text = article.preview,
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    lineHeight = 17.sp
                )

                Spacer(Modifier.height(10.dp))
                HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f))
                Spacer(Modifier.height(8.dp))

                // ── Footer: tanggal + waktu baca ─────────────────
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        "🗓 ${article.formattedDate}",
                        fontSize = 10.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        article.readTime,
                        fontSize = 10.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}
