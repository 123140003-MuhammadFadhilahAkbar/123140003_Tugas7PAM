package org.example.project.screens

// ================================================================
// FavoritesScreen.kt — GANTI YANG LAMA
// Lokasi: src/commonMain/kotlin/org/example/project/screens/
// ================================================================

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.example.project.viewmodel.NotesUiState
import org.example.project.viewmodel.NoteViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FavoritesScreen(
    viewModel   : NoteViewModel,
    onNoteClick : (Int) -> Unit,
    onMenuClick : () -> Unit = {}
) {
    val uiState by viewModel.favoritesUiState.collectAsState()
    val count   = (uiState as? NotesUiState.Content)?.notes?.size ?: 0

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
                        Text("Favorit", fontWeight = FontWeight.Bold)
                        Text(
                            "$count catatan favorit",
                            fontSize = 12.sp,
                            color    = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        }
    ) { padding ->
        when (val state = uiState) {
            is NotesUiState.Loading -> LoadingState(Modifier.padding(padding))

            is NotesUiState.Empty -> {
                Box(
                    modifier         = Modifier.fillMaxSize().padding(padding),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            Icons.Default.Favorite, null,
                            tint     = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.35f),
                            modifier = Modifier.size(72.dp)
                        )
                        Spacer(Modifier.height(16.dp))
                        Text(
                            "Belum ada catatan favorit",
                            fontWeight = FontWeight.Bold,
                            fontSize   = 18.sp,
                            color      = MaterialTheme.colorScheme.onSurface
                        )
                        Spacer(Modifier.height(6.dp))
                        Text(
                            "Tekan ❤ pada catatan untuk menambahkannya",
                            color    = MaterialTheme.colorScheme.onSurfaceVariant,
                            fontSize = 13.sp
                        )
                    }
                }
            }

            is NotesUiState.Content -> {
                LazyColumn(
                    modifier            = Modifier.fillMaxSize().padding(padding),
                    contentPadding      = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(state.notes, key = { it.id }) { note ->
                        NoteCard(
                            note            = note,
                            onClick         = { onNoteClick(note.id) },
                            onFavoriteClick = { viewModel.toggleFavorite(note.id) }
                        )
                    }
                    item { Spacer(Modifier.height(80.dp)) }
                }
            }
        }
    }
}
