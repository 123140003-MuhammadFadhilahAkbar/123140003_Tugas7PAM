package org.example.project.viewmodel

// ================================================================
// NoteViewModel.kt — GANTI YANG LAMA
// Lokasi: src/commonMain/kotlin/org/example/project/viewmodel/
//
// Semua data dari SQLDelight. Search + sort reaktif via Flow.
// ================================================================

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import org.example.project.data.NoteLocalDataSource
import org.example.project.data.SettingsManager
import org.example.project.data.SortOrder
import org.example.project.model.Note
import org.example.project.model.NoteColor

// ── UI State ──────────────────────────────────────────────────────
sealed class NotesUiState {
    object Loading                          : NotesUiState()
    object Empty                            : NotesUiState()
    data class Content(val notes: List<Note>) : NotesUiState()
}

@OptIn(ExperimentalCoroutinesApi::class, FlowPreview::class)
class NoteViewModel(
    private val dataSource      : NoteLocalDataSource,
    private val settingsManager : SettingsManager
) : ViewModel() {

    // ── Search query ──────────────────────────────────────────────
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    // ── Sort order dari Settings ──────────────────────────────────
    val sortOrder: StateFlow<SortOrder> = settingsManager.sortOrderFlow
        .map { name -> SortOrder.entries.firstOrNull { it.name == name } ?: SortOrder.DATE_DESC }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), SortOrder.DATE_DESC)

    // ── Notes UI State reaktif ────────────────────────────────────
    val notesUiState: StateFlow<NotesUiState> =
        combine(_searchQuery.debounce(300), sortOrder) { q, s -> Pair(q, s) }
            .flatMapLatest { (query, sort) ->
                val flow: Flow<List<Note>> = when {
                    query.isNotBlank()       -> dataSource.search(query)
                    sort == SortOrder.TITLE_AZ -> dataSource.observeByTitle()
                    sort == SortOrder.DATE_ASC -> dataSource.observeOldest()
                    else                     -> dataSource.observeAll()
                }
                flow.map { notes ->
                    if (notes.isEmpty()) NotesUiState.Empty
                    else NotesUiState.Content(notes)
                }
            }
            .onStart { emit(NotesUiState.Loading) }
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), NotesUiState.Loading)

    // ── Favorit UI State ──────────────────────────────────────────
    val favoritesUiState: StateFlow<NotesUiState> = dataSource.observeFavorites()
        .map { notes ->
            if (notes.isEmpty()) NotesUiState.Empty else NotesUiState.Content(notes)
        }
        .onStart { emit(NotesUiState.Loading) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), NotesUiState.Loading)

    // ── Catatan yang sedang dibuka (detail/edit) ──────────────────
    private val _selectedNote = MutableStateFlow<Note?>(null)
    val selectedNote: StateFlow<Note?> = _selectedNote.asStateFlow()

    // ── CRUD ──────────────────────────────────────────────────────

    fun setSearchQuery(query: String) { _searchQuery.value = query }
    fun clearSearch()                 { _searchQuery.value = "" }

    fun addNote(title: String, content: String, color: NoteColor) {
        viewModelScope.launch { dataSource.insert(title, content, color) }
    }

    fun updateNote(id: Int, title: String, content: String, color: NoteColor) {
        viewModelScope.launch { dataSource.update(id.toLong(), title, content, color) }
    }

    fun deleteNote(id: Int) {
        viewModelScope.launch { dataSource.delete(id.toLong()) }
    }

    fun toggleFavorite(id: Int) {
        viewModelScope.launch { dataSource.toggleFavorite(id.toLong()) }
    }

    fun loadNoteById(id: Int) {
        viewModelScope.launch { _selectedNote.value = dataSource.getById(id.toLong()) }
    }

    /** Helper untuk screen yang membutuhkan Note secara sinkron (detail/edit) */
    fun getNoteById(id: Int): Note? =
        _selectedNote.value?.takeIf { it.id == id }
            ?: (notesUiState.value as? NotesUiState.Content)?.notes?.find { it.id == id }
            ?: (favoritesUiState.value as? NotesUiState.Content)?.notes?.find { it.id == id }
}
