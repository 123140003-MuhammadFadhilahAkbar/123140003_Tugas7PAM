package org.example.project.navigation

// ================================================================
// Screen.kt — GANTI YANG LAMA
// Lokasi: src/commonMain/kotlin/org/example/project/navigation/
//
// Tambahan: route "settings"
// ================================================================

sealed class Screen(val route: String) {

    // ── Bottom Navigation Tabs ──────────────────────────────────
    object NoteList  : Screen("note_list")
    object Favorites : Screen("favorites")
    object Settings  : Screen("settings")
    object Profile   : Screen("profile")

    // ── Detail / Form Screens ────────────────────────────────────
    object NoteDetail : Screen("note_detail/{noteId}") {
        fun createRoute(noteId: Int) = "note_detail/$noteId"
    }
    object AddNote : Screen("add_note")
    object EditNote : Screen("edit_note/{noteId}") {
        fun createRoute(noteId: Int) = "edit_note/$noteId"
    }
}
