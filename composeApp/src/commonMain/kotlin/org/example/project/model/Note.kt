package org.example.project.model

// ================================================================
// Note.kt — Model data catatan
// TIDAK ADA PERUBAHAN dari Tugas 5
// ================================================================

data class Note(
    val id        : Int,
    val title     : String,
    val content   : String,
    val isFavorite: Boolean   = false,
    val color     : NoteColor = NoteColor.DEFAULT,
    val timestamp : Long      = System.currentTimeMillis()
)

enum class NoteColor(val hex: Long) {
    DEFAULT (0xFF2C2C3E),
    PURPLE  (0xFF3D1A6E),
    TEAL    (0xFF0D4F4F),
    NAVY    (0xFF0D1B4F),
    ROSE    (0xFF4F1429)
}
