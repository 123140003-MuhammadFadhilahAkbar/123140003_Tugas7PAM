package org.example.project.viewmodel

// ================================================================
// ProfileViewModel.kt — DIPERBARUI
// Lokasi: src/commonMain/kotlin/org/example/project/viewmodel/
//
// Dark mode dihapus dari sini karena sudah dikelola SettingsViewModel.
// ================================================================

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue

data class UserProfile(
    val name        : String = "Muhammad Fadhilah Akbar",
    val title       : String = "Mobile Developer · ITERA '23",
    val status      : String = "Aktif Belajar",
    val bio         : String = "Mahasiswa Teknik Informatika ITERA angkatan 2023. " +
            "Fokus pada Mobile Development dan UI/UX Design. " +
            "Passionate dalam membangun aplikasi multiplatform dengan Kotlin.",
    val email       : String = "muhammad.123140003@student.itera.ac.id",
    val phone       : String = "+62 853 4258 6196",
    val location    : String = "Lampung, Indonesia",
    val institution : String = "Institut Teknologi Sumatera",
    val angkatan    : String = "2023",
    val prodi       : String = "IF",
    val projects    : String = "3+"
)

data class ProfileUiState(
    val profile      : UserProfile = UserProfile(),
    val isEditing    : Boolean     = false,
    val editName     : String      = "",
    val editTitle    : String      = "",
    val editBio      : String      = "",
    val editEmail    : String      = "",
    val editPhone    : String      = "",
    val editLocation : String      = "",
    val errorMessage : String?     = null,
    val saveSuccess  : Boolean     = false,
    val showContact  : Boolean     = false
)

class ProfileViewModel {
    var uiState by mutableStateOf(ProfileUiState())
        private set

    fun toggleContact() {
        uiState = uiState.copy(showContact = !uiState.showContact)
    }

    fun startEditing() {
        val p = uiState.profile
        uiState = uiState.copy(
            isEditing    = true,
            editName     = p.name,
            editTitle    = p.title,
            editBio      = p.bio,
            editEmail    = p.email,
            editPhone    = p.phone,
            editLocation = p.location,
            saveSuccess  = false,
            errorMessage = null
        )
    }

    fun cancelEditing() {
        uiState = uiState.copy(
            isEditing    = false,
            editName     = "",
            editTitle    = "",
            editBio      = "",
            editEmail    = "",
            editPhone    = "",
            editLocation = "",
            errorMessage = null
        )
    }

    fun saveProfile() {
        if (uiState.editName.isBlank()) {
            uiState = uiState.copy(errorMessage = "Nama tidak boleh kosong!")
            return
        }
        val updated = uiState.profile.copy(
            name     = uiState.editName.trim(),
            title    = uiState.editTitle.trim(),
            bio      = uiState.editBio.trim(),
            email    = uiState.editEmail.trim(),
            phone    = uiState.editPhone.trim(),
            location = uiState.editLocation.trim()
        )
        uiState = uiState.copy(
            profile      = updated,
            isEditing    = false,
            saveSuccess  = true,
            errorMessage = null,
            editName     = "", editTitle    = "",
            editBio      = "", editEmail    = "",
            editPhone    = "", editLocation = ""
        )
    }

    fun onNameChange    (v: String) { uiState = uiState.copy(editName     = v, errorMessage = null) }
    fun onTitleChange   (v: String) { uiState = uiState.copy(editTitle    = v) }
    fun onBioChange     (v: String) { uiState = uiState.copy(editBio      = v) }
    fun onEmailChange   (v: String) { uiState = uiState.copy(editEmail    = v) }
    fun onPhoneChange   (v: String) { uiState = uiState.copy(editPhone    = v) }
    fun onLocationChange(v: String) { uiState = uiState.copy(editLocation = v) }

    fun clearSaveSuccess() { uiState = uiState.copy(saveSuccess = false) }
}
