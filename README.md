# 📝 Notes App - Tugas 7 PAM

**Nama:** Muhammad Fadhilah Akbar

**NIM:** 123140003

**Mata Kuliah:** IF25-22017 Pengembangan Aplikasi Mobile

---

## 📋 Deskripsi

Notes App adalah aplikasi catatan berbasis **Kotlin Multiplatform (KMP)** dengan **Compose Multiplatform**. Tugas 7 berfokus pada implementasi **Local Data Storage** menggunakan **SQLDelight** untuk database SQLite yang persisten dan **multiplatform-settings (DataStore)** untuk menyimpan preferensi pengguna, lengkap dengan Repository Pattern, UI States (Loading/Empty/Content), fitur search, settings screen, serta arsitektur offline-first.

---

## 🗂️ Struktur Folder

```
composeApp/src/
├── commonMain/kotlin/org/example/project/
│   ├── App.kt                          # Entry point Composable
│   ├── AppDependencies.kt              # expect fun provideDataSource/Settings
│   ├── model/
│   │   └── Note.kt                     # Data class Note + enum NoteColor
│   ├── db/
│   │   └── DatabaseDriverFactory.kt    # expect class DatabaseDriverFactory
│   ├── data/
│   │   ├── NoteLocalDataSource.kt      # Repository SQLDelight (CRUD + search)
│   │   └── SettingsManager.kt          # DataStore preferences (dark mode, sort)
│   ├── viewmodel/
│   │   ├── NoteViewModel.kt            # StateFlow, search debounce, UI states
│   │   ├── SettingsViewModel.kt        # Toggle dark mode, sort order
│   │   └── ProfileViewModel.kt         # Edit profil mahasiswa
│   ├── components/
│   │   └── AppDrawer.kt                # Hamburger drawer navigasi
│   ├── screens/
│   │   ├── NoteListScreen.kt           # Daftar catatan + search + UI states
│   │   ├── FavoritesScreen.kt          # Catatan favorit + UI states
│   │   ├── SettingsScreen.kt           # Toggle tema + pilihan sort order
│   │   ├── NoteDetailScreen.kt         # Detail catatan + edit + delete
│   │   ├── NoteFormScreens.kt          # Form tambah & edit catatan
│   │   └── ProfileScreenWrapper.kt     # Profil mahasiswa + edit
│   └── navigation/
│       ├── Screen.kt                   # Sealed class semua routes
│       └── AppNavigation.kt            # NavHost, BottomNav, Drawer, Theme
│
├── commonMain/sqldelight/org/example/project/db/
│   └── Note.sq                         # SQL schema + semua queries
│
├── androidMain/kotlin/org/example/project/
│   ├── MainActivity.kt                 # Inisialisasi app
│   ├── DatabaseModule.kt               # Singleton DB + Settings inisialisasi
│   ├── AppDependencies.android.kt      # actual fun provideDataSource/Settings
│   └── db/
│       └── DatabaseDriverFactory.android.kt  # actual class AndroidSqliteDriver
```

---

## 🗃️ Database Schema

```sql
CREATE TABLE NoteEntity (
    id          INTEGER PRIMARY KEY AUTOINCREMENT,
    title       TEXT    NOT NULL,
    content     TEXT    NOT NULL,
    is_favorite INTEGER NOT NULL DEFAULT 0,
    color_name  TEXT    NOT NULL DEFAULT 'DEFAULT',
    created_at  INTEGER NOT NULL,
    updated_at  INTEGER NOT NULL
);
```

**Queries yang tersedia:**
| Query | Fungsi |
|---|---|
| `selectAll` | Ambil semua catatan (terbaru dulu) |
| `selectAllByTitle` | Ambil semua catatan (A–Z) |
| `selectAllOldest` | Ambil semua catatan (terlama dulu) |
| `selectFavorites` | Ambil catatan favorit saja |
| `selectById` | Ambil satu catatan by ID |
| `search` | Cari di judul dan isi catatan |
| `insert` | Tambah catatan baru |
| `update` | Perbarui catatan |
| `toggleFavorite` | Toggle status favorit |
| `delete` | Hapus catatan |

---

## ✅ Fitur yang Diimplementasikan

### 1. SQLDelight Database
- **SQLDelight 2.0.1** dikonfigurasi dengan plugin dan schema `.sq`
- `DatabaseDriverFactory` menggunakan `expect/actual` pattern untuk KMP
- `AndroidSqliteDriver` untuk Android — data persisten di file `notes_tugas7.db`
- Semua query type-safe, di-generate otomatis dari `Note.sq`
- Implementasi di: `Note.sq`, `DatabaseDriverFactory.kt`, `DatabaseDriverFactory.android.kt`

### 2. CRUD Operations
- **Create** — Tambah catatan baru dengan judul, isi, dan warna
- **Read** — Baca semua catatan sebagai `Flow<List<Note>>` → auto-update UI
- **Update** — Edit judul, isi, dan warna catatan yang sudah ada
- **Delete** — Hapus catatan dengan dialog konfirmasi
- **Toggle Favorite** — Tandai/hapus tanda favorit
- Implementasi di: `NoteLocalDataSource.kt`, `NoteViewModel.kt`

### 3. DataStore Settings
- **multiplatform-settings 1.1.1** untuk key-value storage yang persisten
- **Dark mode** tersimpan → berlaku saat app restart
- **Sort order** tersimpan → 3 pilihan: Terbaru, Terlama, Judul A–Z
- Settings screen dengan UI yang rapi (toggle, radio button)
- Implementasi di: `SettingsManager.kt`, `SettingsViewModel.kt`, `SettingsScreen.kt`

### 4. Search Feature
- Search bar real-time dengan **debounce 300ms** agar tidak lag
- Mencari di **judul DAN isi** catatan sekaligus
- Label hasil pencarian: `"Hasil untuk 'query' — X ditemukan"`
- Empty state khusus saat hasil pencarian kosong
- Implementasi di: `NoteListScreen.kt`, `NoteViewModel.kt`

### 5. UI/UX

| State | Tampilan | Trigger |
|---|---|---|
| **Loading** | `CircularProgressIndicator` + teks | Pertama kali load data |
| **Empty** | Emoji + pesan informatif | Tidak ada catatan / hasil search |
| **Content** | `LazyColumn` daftar catatan berwarna | Data tersedia |

Fitur UI tambahan:
- Hamburger menu (drawer) konsisten di **semua halaman utama**
- Bottom navigation bar dengan 4 tab: Catatan, Favorit, Pengaturan, Profil
- Delete confirmation dialog sebelum hapus
- Animasi transisi tema (fade in/out)
- Warna catatan: 5 pilihan (Default, Purple, Teal, Navy, Rose)

### 6. Code Quality
- **Single Source of Truth** — SQLDelight sebagai satu-satunya sumber data UI
- **Reactive updates** — `Flow` → `StateFlow` → `collectAsState()` otomatis update UI
- **Repository/DataSource pattern** — abstraksi layer data terpisah dari ViewModel
- **expect/actual pattern** — platform-specific code terpisah rapi
- Komentar dokumentasi di setiap file

---

## 7. Offline-First Architecture

Aplikasi menggunakan **offline-first** approach:

- Data **selalu dibaca dari SQLDelight** (local DB) sebagai primary source
- Tidak membutuhkan koneksi internet sama sekali untuk semua fitur
- Data tetap ada setelah app di-restart, HP di-reboot, bahkan setelah update app
- `DatabaseModule` singleton memastikan hanya ada satu instance database

**Arsitektur data flow:**
```
UI (Compose)
    ↓ collectAsState()
StateFlow (ViewModel)
    ↓ stateIn()
Flow (NoteLocalDataSource)
    ↓ asFlow() + mapToList()
SQLDelight Queries
    ↓
SQLite Database (notes_tugas7.db)
```

**DataStore persistence:**
- Dark mode dan sort order tersimpan di `SharedPreferences` via `multiplatform-settings`
- Setting tetap berlaku setelah app di-restart

---

## 📱 Screenshots

| Screen | Deskripsi |
|--------|-----------|
| <details><summary><code>screenshot_notelist.png</code></summary><br><img width="300" height="600" alt="Image" src="https://github.com/user-attachments/assets/46db7e7d-6abd-4676-b057-df06499abf5f" /></details> | Halaman utama- daftar catatan dengan warna berbeda |
| <details><summary><code>screenshot_search.png</code></summary><br><img width="300" height="600" alt="Image" src="https://github.com/user-attachments/assets/a5fdbeae-f831-424c-b0dd-6a955311de56" /></details> | Search aktif -real - time filter judul & isi |
| <details><summary><code>screenshot_search_empty.png</code></summary><br><img width="300" height="600" alt="Image" src="https://github.com/user-attachments/assets/fc1768a5-b0b0-48b5-a7cb-3964ac9f0072" /></details> | Search - hasil tidak ditemukan |
| <details><summary><code>screenshot_addnote.png</code></summary><br><img width="300" height="600" alt="Image" src="https://github.com/user-attachments/assets/d24b3156-9092-45ec-99d3-0da44dc1a32e" /></details> | Form tambah catatan - pilih warna, isi judul & isi |
| <details><summary><code>screenshot_editnote.png</code></summary><br><img width="300" height="600" alt="Image" src="https://github.com/user-attachments/assets/acf20e73-ba05-4d52-94c7-9894fd30fad8" /></details> | Form edit catatan |
| <details><summary><code>screenshot_detail.png</code></summary><br><img width="300" height="600" alt="Image" src="https://github.com/user-attachments/assets/e47184e4-0a87-4572-b2f9-bedb83e6b998" /></details> | Detail catatan - aksi edit, hapus, favorit |
| <details><summary><code>screenshot_favorites.png</code></summary><br><img width="300" height="600" alt="Image" src="https://github.com/user-attachments/assets/9cca875e-3ceb-4db8-a63a-07204fdb6f00" /></details> | Tab Favorit - catatan yang disukai |
| <details><summary><code>screenshot_settings.png</code></summary><br><img width="300" height="600" alt="Image" src="https://github.com/user-attachments/assets/d53c68fd-427e-468a-9949-b9b10ce92539" /></details> | Settings - toggle dark mode + pilih urutan |
| <details><summary><code>screenshot_drawer.png</code></summary><br><<img width="300" height="600" alt="Image" src="https://github.com/user-attachments/assets/b8595fda-85d0-4a6e-8c96-2d9172190137" /></details> | Hamburger drawer - navigasi konsisten |
| <details><summary><code>screenshot_profile.png</code></summary><br><img width="300" height="600" alt="Image" src="https://github.com/user-attachments/assets/66c260da-e6ad-4c75-b947-b007a8614c7c" /></details> | Halaman profil mahasiswa |
| <details><summary><code>screenshot_delete.png</code></summary><br><img width="300" height="600" alt="Image" src="https://github.com/user-attachments/assets/a2cf23e9-b048-4bf8-9d2b-58794045a9ea" /></details> | Dialog konfirmasi hapus catatan |

---

## 🎬 Video Demo

| Fitur | Preview | Keterangan |
| :--- | :--- | :--- |
| **Video Demo** | [▶️ Tonton Video Demo (Google Drive)](https://drive.google.com/file/d/1FxnQg6GW3ZixMW0y7Ed9OuOrwZzWyHyl/view?usp=drivesdk) | `demo_week7.mp4`  |


---

## 🛠️ Tech Stack

| Komponen | Teknologi |
|----------|-----------|
| Language | Kotlin Multiplatform 2.3.20 |
| UI | Compose Multiplatform 1.10.3 |
| Database | SQLDelight 2.0.1 (SQLite) |
| Preferences | multiplatform-settings 1.1.1 |
| DateTime | kotlinx-datetime 0.5.0 |
| Navigation | AndroidX Navigation Compose 2.9.0 |
| Image Loading | Coil3 3.1.0 |
| Architecture | KMP + Repository Pattern + MVVM + Offline-First |

---

## 📦 Dependencies Tambahan 

```toml
# libs.versions.toml — tidak ada perubahan versi existing
```

```kotlin
// build.gradle.kts 

plugins {
    id("app.cash.sqldelight") version "2.0.1"   // plugin SQLDelight
}

// commonMain.dependencies
implementation("app.cash.sqldelight:runtime:2.0.1")
implementation("app.cash.sqldelight:coroutines-extensions:2.0.1")
implementation("com.russhwolf:multiplatform-settings:1.1.1")
implementation("com.russhwolf:multiplatform-settings-coroutines:1.1.1")
implementation("org.jetbrains.kotlinx:kotlinx-datetime:0.5.0")

// androidMain.dependencies
implementation("app.cash.sqldelight:android-driver:2.0.1")

// konfigurasi SQLDelight
sqldelight {
    databases {
        create("NotesDatabase") {
            packageName.set("org.example.project.db")
        }
    }
}
```

---

## 📊 Pemenuhan Rubrik Penilaian

| Komponen | Implementasi |
|---|---|
| **SQLDelight Setup** | Plugin ditambahkan, `Note.sq` dengan 10+ queries, `DatabaseDriverFactory` expect/actual, `AndroidSqliteDriver` di androidMain |
| **CRUD Operations** | Create (`insert`), Read (`selectAll` sebagai Flow), Update (`update`), Delete (`delete`) — semua operasi berfungsi dan persisten |
| **DataStore Settings**  | `SettingsManager` dengan `multiplatform-settings`, dark mode + sort order tersimpan dan diterapkan, UI settings screen lengkap |
| **Search Feature** | Search real-time dengan debounce 300ms, mencari di judul DAN isi, label hasil, empty state khusus search |
| **UI/UX** | 3 UI states (Loading/Empty/Content) di semua list screen, hamburger konsisten, delete dialog, animasi tema, warna catatan |
| **Code Quality** | Single Source of Truth, reactive Flow, Repository pattern, expect/actual, komentar dokumentasi |
| **Offline-First** | Data 100% dari SQLite lokal, tidak butuh internet, persisten setelah restart, `DatabaseModule` singleton |

---

*© 2025 · IF25-22017 · Institut Teknologi Sumatera*
