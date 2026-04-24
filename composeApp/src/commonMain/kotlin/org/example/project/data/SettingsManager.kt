package org.example.project.data

import com.russhwolf.settings.Settings
import com.russhwolf.settings.get
import com.russhwolf.settings.set
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

enum class SortOrder(val label: String) {
    DATE_DESC ("Terbaru Dulu"),
    DATE_ASC  ("Terlama Dulu"),
    TITLE_AZ  ("Judul A–Z")
}

class SettingsManager(private val settings: Settings) {

    companion object {
        private const val KEY_DARK_MODE  = "is_dark_mode"
        private const val KEY_SORT_ORDER = "sort_order"
    }

    // ── Dark mode ─────────────────────────────────────────────────
    private val _isDarkMode = MutableStateFlow(
        settings.getBoolean(KEY_DARK_MODE, false)
    )
    val isDarkModeFlow: Flow<Boolean> = _isDarkMode.asStateFlow()

    suspend fun setDarkMode(enabled: Boolean) {
        settings[KEY_DARK_MODE] = enabled
        _isDarkMode.value = enabled
    }

    // ── Sort order ────────────────────────────────────────────────
    private val _sortOrder = MutableStateFlow(
        settings.getString(KEY_SORT_ORDER, SortOrder.DATE_DESC.name)
    )
    val sortOrderFlow: Flow<String> = _sortOrder.asStateFlow()

    suspend fun setSortOrder(order: SortOrder) {
        settings[KEY_SORT_ORDER] = order.name
        _sortOrder.value = order.name
    }
}