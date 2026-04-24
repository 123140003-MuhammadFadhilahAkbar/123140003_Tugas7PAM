package org.example.project.screens

// ================================================================
// NoteListScreen.kt — GANTI YANG LAMA
// Lokasi: src/commonMain/kotlin/org/example/project/screens/
//
// Perubahan:
//  - Search bar berfungsi (real-time dengan debounce)
//  - UI States: Loading / Empty / Content
//  - Delete card dengan dialog konfirmasi
//  - Label hasil pencarian
// ================================================================

import androidx.compose.animation.*
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.example.project.model.Note
import org.example.project.viewmodel.NotesUiState
import org.example.project.viewmodel.NoteViewModel
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NoteListScreen(
    viewModel   : NoteViewModel,
    onNoteClick : (Int) -> Unit,
    onAddClick  : () -> Unit,
    onMenuClick : () -> Unit = {}
) {
    val uiState     by viewModel.notesUiState.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()
    var isSearching by remember { mutableStateOf(false) }
    val focusRequester = remember { FocusRequester() }

    val notesCount = (uiState as? NotesUiState.Content)?.notes?.size ?: 0

    Scaffold(
        topBar = {
            AnimatedContent(
                targetState = isSearching,
                transitionSpec = { fadeIn() togetherWith fadeOut() },
                label = "topbar_anim"
            ) { searching ->
                if (searching) {
                    TopAppBar(
                        navigationIcon = {
                            IconButton(onClick = {
                                isSearching = false
                                viewModel.clearSearch()
                            }) { Icon(Icons.Default.ArrowBack, "Kembali") }
                        },
                        title = {
                            TextField(
                                value         = searchQuery,
                                onValueChange = viewModel::setSearchQuery,
                                placeholder   = { Text("Cari judul atau isi catatan...") },
                                modifier      = Modifier.fillMaxWidth().focusRequester(focusRequester),
                                singleLine    = true,
                                colors        = TextFieldDefaults.colors(
                                    focusedContainerColor   = Color.Transparent,
                                    unfocusedContainerColor = Color.Transparent,
                                    focusedIndicatorColor   = Color.Transparent,
                                    unfocusedIndicatorColor = Color.Transparent
                                )
                            )
                            LaunchedEffect(Unit) { focusRequester.requestFocus() }
                        },
                        actions = {
                            if (searchQuery.isNotBlank()) {
                                IconButton(onClick = { viewModel.clearSearch() }) {
                                    Icon(Icons.Default.Clear, "Hapus")
                                }
                            }
                        },
                        colors = TopAppBarDefaults.topAppBarColors(
                            containerColor = MaterialTheme.colorScheme.surface
                        )
                    )
                } else {
                    TopAppBar(
                        navigationIcon = {
                            IconButton(onClick = onMenuClick) {
                                Icon(Icons.Default.Menu, "Menu",
                                    tint = MaterialTheme.colorScheme.onSurface)
                            }
                        },
                        title = {
                            Column {
                                Text("Catatan Saya", fontWeight = FontWeight.Bold)
                                Text(
                                    "$notesCount catatan",
                                    fontSize = 12.sp,
                                    color    = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        },
                        colors = TopAppBarDefaults.topAppBarColors(
                            containerColor = MaterialTheme.colorScheme.surface
                        ),
                        actions = {
                            IconButton(onClick = { isSearching = true }) {
                                Icon(Icons.Default.Search, "Cari",
                                    tint = MaterialTheme.colorScheme.primary)
                            }
                        }
                    )
                }
            }
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick        = onAddClick,
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor   = MaterialTheme.colorScheme.onPrimary,
                shape          = CircleShape,
                modifier       = Modifier.size(58.dp)
            ) {
                Icon(Icons.Default.Add, "Tambah", modifier = Modifier.size(26.dp))
            }
        }
    ) { padding ->
        when (val state = uiState) {
            is NotesUiState.Loading -> LoadingState(Modifier.padding(padding))
            is NotesUiState.Empty   -> EmptyState(
                isSearchMode = searchQuery.isNotBlank(),
                modifier     = Modifier.padding(padding)
            )
            is NotesUiState.Content -> {
                LazyColumn(
                    modifier            = Modifier.fillMaxSize().padding(padding),
                    contentPadding      = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    if (searchQuery.isNotBlank()) {
                        item {
                            Text(
                                "Hasil untuk \"$searchQuery\" — ${state.notes.size} ditemukan",
                                fontSize = 12.sp,
                                color    = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.padding(bottom = 2.dp)
                            )
                        }
                    }
                    items(state.notes, key = { it.id }) { note ->
                        NoteCard(
                            note            = note,
                            onClick         = { onNoteClick(note.id) },
                            onFavoriteClick = { viewModel.toggleFavorite(note.id) },
                            onDelete        = { viewModel.deleteNote(note.id) }
                        )
                    }
                    item { Spacer(Modifier.height(80.dp)) }
                }
            }
        }
    }
}

// ── NoteCard — dipakai juga di FavoritesScreen ────────────────────
@Composable
fun NoteCard(
    note            : Note,
    onClick         : () -> Unit,
    onFavoriteClick : () -> Unit,
    onDelete        : (() -> Unit)? = null
) {
    var showDialog by remember { mutableStateOf(false) }
    val cardBg = Color(note.color.hex)

    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title   = { Text("Hapus Catatan?", fontWeight = FontWeight.Bold) },
            text    = { Text("\"${note.title}\" akan dihapus permanen dan tidak bisa dikembalikan.") },
            confirmButton = {
                TextButton(onClick = { showDialog = false; onDelete?.invoke() }) {
                    Text("Hapus", color = MaterialTheme.colorScheme.error, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDialog = false }) { Text("Batal") }
            }
        )
    }

    Card(
        modifier  = Modifier.fillMaxWidth().clickable { onClick() },
        shape     = RoundedCornerShape(16.dp),
        colors    = CardDefaults.cardColors(containerColor = cardBg),
        elevation = CardDefaults.cardElevation(3.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Text(
                    text       = note.title,
                    color      = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize   = 15.sp,
                    maxLines   = 1,
                    overflow   = TextOverflow.Ellipsis,
                    modifier   = Modifier.weight(1f)
                )
                Row {
                    IconButton(onClick = onFavoriteClick, modifier = Modifier.size(30.dp)) {
                        Icon(
                            if (note.isFavorite) Icons.Filled.Favorite else Icons.Default.FavoriteBorder,
                            contentDescription = "Favorit",
                            tint     = if (note.isFavorite) Color(0xFFFF6B8A) else Color.White.copy(alpha = 0.6f),
                            modifier = Modifier.size(18.dp)
                        )
                    }
                    if (onDelete != null) {
                        IconButton(onClick = { showDialog = true }, modifier = Modifier.size(30.dp)) {
                            Icon(
                                Icons.Default.Delete, "Hapus",
                                tint     = Color.White.copy(alpha = 0.5f),
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                }
            }
            Spacer(Modifier.height(6.dp))
            Text(
                text       = note.content,
                color      = Color.White.copy(alpha = 0.8f),
                fontSize   = 13.sp,
                maxLines   = 2,
                overflow   = TextOverflow.Ellipsis,
                lineHeight = 18.sp
            )
            Spacer(Modifier.height(10.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    formatNoteDate(note.timestamp),
                    color    = Color.White.copy(alpha = 0.55f),
                    fontSize = 10.sp
                )
                if (note.isFavorite) {
                    Surface(
                        shape = RoundedCornerShape(20.dp),
                        color = Color(0xFFFF6B8A).copy(alpha = 0.25f)
                    ) {
                        Text(
                            "⭐ Favorit",
                            color      = Color(0xFFFF6B8A),
                            fontSize   = 9.sp,
                            fontWeight = FontWeight.Medium,
                            modifier   = Modifier.padding(horizontal = 7.dp, vertical = 2.dp)
                        )
                    }
                }
            }
        }
    }
}

// ── Shared State Composables ──────────────────────────────────────
@Composable
fun LoadingState(modifier: Modifier = Modifier) {
    Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
            Spacer(Modifier.height(16.dp))
            Text("Memuat catatan...", color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 14.sp)
        }
    }
}

@Composable
fun EmptyState(isSearchMode: Boolean = false, modifier: Modifier = Modifier) {
    Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(if (isSearchMode) "🔍" else "📭", fontSize = 60.sp)
            Spacer(Modifier.height(14.dp))
            Text(
                if (isSearchMode) "Tidak ada catatan ditemukan" else "Belum ada catatan",
                fontWeight = FontWeight.Bold, fontSize = 18.sp,
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(Modifier.height(6.dp))
            Text(
                if (isSearchMode) "Coba kata kunci yang berbeda" else "Tekan + untuk membuat catatan baru",
                color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 13.sp
            )
        }
    }
}

fun formatNoteDate(timestamp: Long): String =
    SimpleDateFormat("dd MMM yyyy, HH:mm", Locale.forLanguageTag("id")).format(Date(timestamp))
