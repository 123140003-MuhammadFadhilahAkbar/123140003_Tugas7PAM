package org.example.project.data

// ================================================================
// NoteLocalDataSource.kt
// Lokasi: src/commonMain/kotlin/org/example/project/data/
//
// Single Source of Truth: semua CRUD via SQLDelight.
// UI selalu observe Flow → auto-update tanpa manual refresh.
// ================================================================

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import app.cash.sqldelight.coroutines.mapToOneOrNull
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import kotlinx.datetime.Clock
import org.example.project.db.NoteEntity
import org.example.project.db.NotesDatabase
import org.example.project.model.Note
import org.example.project.model.NoteColor

class NoteLocalDataSource(database: NotesDatabase) {

    private val q = database.noteQueries

    // ── Observe semua catatan (diurutkan terbaru) ────────────────
    fun observeAll(): Flow<List<Note>> =
        q.selectAll().asFlow().mapToList(Dispatchers.IO).map { it.map(NoteEntity::toDomain) }

    // ── Observe diurutkan berdasarkan judul ──────────────────────
    fun observeByTitle(): Flow<List<Note>> =
        q.selectAllByTitle().asFlow().mapToList(Dispatchers.IO).map { it.map(NoteEntity::toDomain) }

    // ── Observe diurutkan terlama ────────────────────────────────
    fun observeOldest(): Flow<List<Note>> =
        q.selectAllOldest().asFlow().mapToList(Dispatchers.IO).map { it.map(NoteEntity::toDomain) }

    // ── Observe favorit ──────────────────────────────────────────
    fun observeFavorites(): Flow<List<Note>> =
        q.selectFavorites().asFlow().mapToList(Dispatchers.IO).map { it.map(NoteEntity::toDomain) }

    // ── Search catatan ───────────────────────────────────────────
    fun search(query: String): Flow<List<Note>> {
        val pattern = "%$query%"
        return q.search(pattern, pattern).asFlow().mapToList(Dispatchers.IO)
            .map { it.map(NoteEntity::toDomain) }
    }

    // ── Ambil satu catatan ───────────────────────────────────────
    suspend fun getById(id: Long): Note? =
        withContext(Dispatchers.IO) {
            q.selectById(id).executeAsOneOrNull()?.toDomain()
        }

    // ── Tambah catatan baru ──────────────────────────────────────
    suspend fun insert(title: String, content: String, color: NoteColor) {
        val now = Clock.System.now().toEpochMilliseconds()
        withContext(Dispatchers.IO) {
            q.insert(
                title       = title.trim(),
                content     = content.trim(),
                is_favorite = 0L,
                color_name  = color.name,
                created_at  = now,
                updated_at  = now
            )
        }
    }

    // ── Update catatan ───────────────────────────────────────────
    suspend fun update(id: Long, title: String, content: String, color: NoteColor) {
        val now = Clock.System.now().toEpochMilliseconds()
        withContext(Dispatchers.IO) {
            q.update(
                title      = title.trim(),
                content    = content.trim(),
                color_name = color.name,
                updated_at = now,
                id         = id
            )
        }
    }

    // ── Toggle favorit ───────────────────────────────────────────
    suspend fun toggleFavorite(id: Long) =
        withContext(Dispatchers.IO) { q.toggleFavorite(id) }

    // ── Hapus catatan ────────────────────────────────────────────
    suspend fun delete(id: Long) =
        withContext(Dispatchers.IO) { q.delete(id) }
}

// ── Mapper entity DB → domain model ─────────────────────────────
private fun NoteEntity.toDomain() = Note(
    id         = id.toInt(),
    title      = title,
    content    = content,
    isFavorite = is_favorite != 0L,
    color      = try { NoteColor.valueOf(color_name) } catch (_: Exception) { NoteColor.DEFAULT },
    timestamp  = updated_at
)
