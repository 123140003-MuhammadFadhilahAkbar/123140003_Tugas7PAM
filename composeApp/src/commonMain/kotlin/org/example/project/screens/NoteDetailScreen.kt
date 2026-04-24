package org.example.project.screens

// ================================================================
// NoteDetailScreen.kt — DIPERBARUI
// Lokasi: src/commonMain/kotlin/org/example/project/screens/
//
// Perubahan: menggunakan selectedNote StateFlow dari ViewModel
// agar data selalu fresh dari database.
// ================================================================

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.example.project.viewmodel.NoteViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NoteDetailScreen(
    noteId    : Int,
    viewModel : NoteViewModel,
    onBack    : () -> Unit,
    onEdit    : (Int) -> Unit
) {
    val selectedNote by viewModel.selectedNote.collectAsState()
    val note = selectedNote ?: viewModel.getNoteById(noteId)

    var showDeleteDialog by remember { mutableStateOf(false) }

    if (note == null) {
        Box(
            Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                CircularProgressIndicator()
                Spacer(Modifier.height(12.dp))
                Text("Memuat catatan...", color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
        return
    }

    val accentColor = Color(note.color.hex)

    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title   = { Text("Hapus Catatan?", fontWeight = FontWeight.Bold) },
            text    = { Text("\"${note.title}\" akan dihapus permanen.") },
            confirmButton = {
                TextButton(onClick = {
                    viewModel.deleteNote(noteId)
                    showDeleteDialog = false
                    onBack()
                }) {
                    Text("Hapus", color = MaterialTheme.colorScheme.error, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) { Text("Batal") }
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, "Kembali")
                    }
                },
                title  = { Text("Detail Catatan", fontWeight = FontWeight.Medium) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                ),
                actions = {
                    IconButton(onClick = { viewModel.toggleFavorite(noteId) }) {
                        Icon(
                            if (note.isFavorite) Icons.Filled.Favorite else Icons.Default.FavoriteBorder,
                            "Favorit",
                            tint = if (note.isFavorite) Color(0xFFFF6B8A)
                                   else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    IconButton(onClick = { onEdit(noteId) }) {
                        Icon(Icons.Default.Edit, "Edit", tint = MaterialTheme.colorScheme.primary)
                    }
                    IconButton(onClick = { showDeleteDialog = true }) {
                        Icon(Icons.Default.Delete, "Hapus", tint = MaterialTheme.colorScheme.error)
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(20.dp)
        ) {
            // Accent bar
            Box(
                modifier = Modifier
                    .fillMaxWidth().height(4.dp)
                    .clip(RoundedCornerShape(2.dp))
                    .background(accentColor)
            )
            Spacer(Modifier.height(18.dp))

            // Badge ID
            Surface(
                shape = RoundedCornerShape(8.dp),
                color = MaterialTheme.colorScheme.primaryContainer
            ) {
                Text(
                    "ID: #$noteId",
                    color      = MaterialTheme.colorScheme.primary,
                    fontSize   = 11.sp,
                    fontWeight = FontWeight.Medium,
                    modifier   = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                )
            }
            Spacer(Modifier.height(12.dp))

            // Judul
            Text(
                text       = note.title,
                fontWeight = FontWeight.Bold,
                fontSize   = 24.sp,
                color      = MaterialTheme.colorScheme.onBackground,
                lineHeight = 32.sp
            )
            Spacer(Modifier.height(6.dp))

            // Meta info
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.DateRange, null,
                    tint     = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(13.dp))
                Spacer(Modifier.width(4.dp))
                Text(
                    formatNoteDate(note.timestamp),
                    color    = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontSize = 11.sp
                )
                if (note.isFavorite) {
                    Spacer(Modifier.width(10.dp))
                    Text("⭐ Favorit",
                        color      = Color(0xFFFF6B8A),
                        fontSize   = 11.sp,
                        fontWeight = FontWeight.Medium)
                }
            }

            Spacer(Modifier.height(22.dp))
            HorizontalDivider()
            Spacer(Modifier.height(22.dp))

            // Isi catatan
            Text(
                text       = note.content,
                color      = MaterialTheme.colorScheme.onSurface,
                fontSize   = 15.sp,
                lineHeight = 24.sp
            )

            Spacer(Modifier.height(40.dp))

            // Edit button
            Button(
                onClick  = { onEdit(noteId) },
                modifier = Modifier.fillMaxWidth().height(50.dp),
                shape    = RoundedCornerShape(12.dp),
                colors   = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary
                )
            ) {
                Icon(Icons.Default.Edit, null, modifier = Modifier.size(16.dp))
                Spacer(Modifier.width(8.dp))
                Text("Edit Catatan", fontWeight = FontWeight.SemiBold)
            }
        }
    }
}
