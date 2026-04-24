package org.example.project

import android.content.Context
import com.russhwolf.settings.SharedPreferencesSettings
import org.example.project.data.NoteLocalDataSource
import org.example.project.data.SettingsManager
import org.example.project.db.DatabaseDriverFactory
import org.example.project.db.NotesDatabase

object DatabaseModule {
    private var _dataSource: NoteLocalDataSource? = null
    private var _settingsManager: SettingsManager? = null

    fun init(context: Context) {
        if (_dataSource == null) {
            val driver   = DatabaseDriverFactory(context).createDriver()
            val database = NotesDatabase(driver)
            _dataSource  = NoteLocalDataSource(database)
        }
        if (_settingsManager == null) {
            val prefs    = context.getSharedPreferences("notes_prefs", Context.MODE_PRIVATE)
            val settings = SharedPreferencesSettings(prefs)
            _settingsManager = SettingsManager(settings)
        }
    }

    val dataSource: NoteLocalDataSource
        get() = _dataSource ?: error("DatabaseModule.init() belum dipanggil")

    val settingsManager: SettingsManager
        get() = _settingsManager ?: error("DatabaseModule.init() belum dipanggil")
}