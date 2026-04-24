package org.example.project.viewmodel

// ================================================================
// SettingsViewModel.kt — FILE BARU
// Lokasi: src/commonMain/kotlin/org/example/project/viewmodel/
// ================================================================

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import org.example.project.data.SettingsManager
import org.example.project.data.SortOrder

class SettingsViewModel(private val settingsManager: SettingsManager) : ViewModel() {

    val isDarkMode: StateFlow<Boolean> = settingsManager.isDarkModeFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), false)

    val sortOrder: StateFlow<SortOrder> = settingsManager.sortOrderFlow
        .map { name -> SortOrder.entries.firstOrNull { it.name == name } ?: SortOrder.DATE_DESC }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), SortOrder.DATE_DESC)

    fun toggleDarkMode() {
        viewModelScope.launch { settingsManager.setDarkMode(!isDarkMode.value) }
    }

    fun changeSortOrder(order: SortOrder) {
        viewModelScope.launch { settingsManager.setSortOrder(order) }
    }
}
