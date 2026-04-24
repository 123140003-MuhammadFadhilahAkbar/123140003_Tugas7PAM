package org.example.project.db

// ================================================================
// DatabaseDriverFactory.kt
// Lokasi: src/commonMain/kotlin/org/example/project/db/
//
// expect class → actual class di tiap platform
// ================================================================

import app.cash.sqldelight.db.SqlDriver

expect class DatabaseDriverFactory {
    fun createDriver(): SqlDriver
}
