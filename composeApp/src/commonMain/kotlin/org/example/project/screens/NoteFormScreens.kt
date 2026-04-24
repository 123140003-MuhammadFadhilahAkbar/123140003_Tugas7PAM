package org.example.project.screens

// ================================================================
// NoteFormScreens.kt — TIDAK ADA PERUBAHAN SIGNIFIKAN
// Lokasi: src/commonMain/kotlin/org/example/project/screens/
// ================================================================

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.example.project.model.NoteColor
import org.example.project.viewmodel.NoteViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddNoteScreen(
    viewModel : NoteViewModel,
    onBack    : () -> Unit,
    onSaved   : () -> Unit
) {
    var title         by remember { mutableStateOf("") }
    var content       by remember { mutableStateOf("") }
    var selectedColor by remember { mutableStateOf(NoteColor.DEFAULT) }
    var showError     by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                navigationIcon = {
                    IconButton(onClick = onBack) { Icon(Icons.Default.Close, "Tutup") }
                },
                title  = { Text("Catatan Baru", fontWeight = FontWeight.Bold) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                ),
                actions = {
                    TextButton(onClick = {
                        if (title.isBlank()) showError = true
                        else {
                            viewModel.addNote(title, content, selectedColor)
                            onSaved()
                        }
                    }) {
                        Text("Simpan",
                            color      = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.Bold,
                            fontSize   = 15.sp)
                    }
                }
            )
        }
    ) { padding ->
        NoteFormContent(
            title           = title,
            content         = content,
            selectedColor   = selectedColor,
            showError       = showError,
            onTitleChange   = { title = it; showError = false },
            onContentChange = { content = it },
            onColorChange   = { selectedColor = it },
            modifier        = Modifier.padding(padding)
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditNoteScreen(
    noteId    : Int,
    viewModel : NoteViewModel,
    onBack    : () -> Unit,
    onSaved   : () -> Unit
) {
    val existing = remember(noteId) { viewModel.getNoteById(noteId) }
    if (existing == null) { onBack(); return }

    var title         by remember { mutableStateOf(existing.title) }
    var content       by remember { mutableStateOf(existing.content) }
    var selectedColor by remember { mutableStateOf(existing.color) }
    var showError     by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                navigationIcon = {
                    IconButton(onClick = onBack) { Icon(Icons.Default.ArrowBack, "Kembali") }
                },
                title = {
                    Column {
                        Text("Edit Catatan", fontWeight = FontWeight.Bold)
                        Text("ID: #$noteId",
                            fontSize = 11.sp,
                            color    = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                ),
                actions = {
                    TextButton(onClick = {
                        if (title.isBlank()) showError = true
                        else {
                            viewModel.updateNote(noteId, title, content, selectedColor)
                            onSaved()
                        }
                    }) {
                        Text("Perbarui",
                            color      = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.Bold,
                            fontSize   = 15.sp)
                    }
                }
            )
        }
    ) { padding ->
        NoteFormContent(
            title           = title,
            content         = content,
            selectedColor   = selectedColor,
            showError       = showError,
            onTitleChange   = { title = it; showError = false },
            onContentChange = { content = it },
            onColorChange   = { selectedColor = it },
            modifier        = Modifier.padding(padding)
        )
    }
}

@Composable
fun NoteFormContent(
    title           : String,
    content         : String,
    selectedColor   : NoteColor,
    showError       : Boolean,
    onTitleChange   : (String) -> Unit,
    onContentChange : (String) -> Unit,
    onColorChange   : (NoteColor) -> Unit,
    modifier        : Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text("Warna Catatan",
            fontSize   = 12.sp,
            fontWeight = FontWeight.Medium,
            color      = MaterialTheme.colorScheme.onSurfaceVariant)

        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            NoteColor.entries.forEach { colorOption ->
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(CircleShape)
                        .background(Color(colorOption.hex))
                        .border(
                            width = if (selectedColor == colorOption) 3.dp else 0.dp,
                            color = MaterialTheme.colorScheme.onBackground,
                            shape = CircleShape
                        )
                        .clickable { onColorChange(colorOption) }
                )
            }
        }

        OutlinedTextField(
            value         = title,
            onValueChange = onTitleChange,
            modifier      = Modifier.fillMaxWidth(),
            label         = { Text("Judul Catatan *") },
            isError       = showError,
            supportingText = if (showError) {
                { Text("Judul tidak boleh kosong", color = MaterialTheme.colorScheme.error) }
            } else null,
            singleLine = true,
            shape      = RoundedCornerShape(12.dp)
        )

        OutlinedTextField(
            value         = content,
            onValueChange = onContentChange,
            modifier      = Modifier.fillMaxWidth().defaultMinSize(minHeight = 200.dp),
            label         = { Text("Isi Catatan") },
            shape         = RoundedCornerShape(12.dp),
            maxLines      = 15
        )
    }
}
