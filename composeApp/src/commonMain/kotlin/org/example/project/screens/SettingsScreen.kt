package org.example.project.screens

// ================================================================
// SettingsScreen.kt — FILE BARU
// Lokasi: src/commonMain/kotlin/org/example/project/screens/
//
// Settings dengan DataStore:
//  - Toggle dark/light mode (persisten)
//  - Pilihan urutan catatan (persisten)
//  - Info aplikasi
// ================================================================

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.example.project.data.SortOrder
import org.example.project.viewmodel.SettingsViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    viewModel   : SettingsViewModel,
    onMenuClick : () -> Unit = {}
) {
    val isDarkMode by viewModel.isDarkMode.collectAsState()
    val sortOrder  by viewModel.sortOrder.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                navigationIcon = {
                    IconButton(onClick = onMenuClick) {
                        Icon(Icons.Default.Menu, "Menu",
                            tint = MaterialTheme.colorScheme.onSurface)
                    }
                },
                title = {
                    Column {
                        Text("Pengaturan", fontWeight = FontWeight.Bold)
                        Text("Preferensi aplikasi",
                            fontSize = 12.sp,
                            color    = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {

            // ── SEKSI: Tampilan ───────────────────────────────────
            SectionLabel("Tampilan")

            SettingsCard {
                SettingsRow(
                    icon        = if (isDarkMode) Icons.Default.DarkMode else Icons.Default.LightMode,
                    title       = "Mode Gelap",
                    subtitle    = if (isDarkMode) "Tema gelap aktif" else "Tema terang aktif",
                    trailing    = {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Text(if (isDarkMode) "🌙" else "☀️", fontSize = 16.sp)
                            Switch(
                                checked         = isDarkMode,
                                onCheckedChange = { viewModel.toggleDarkMode() },
                                modifier        = Modifier.scale(0.85f),
                                colors          = SwitchDefaults.colors(
                                    checkedThumbColor  = MaterialTheme.colorScheme.onPrimary,
                                    checkedTrackColor  = MaterialTheme.colorScheme.primary,
                                    uncheckedThumbColor = MaterialTheme.colorScheme.outline,
                                    uncheckedTrackColor = MaterialTheme.colorScheme.surfaceVariant
                                )
                            )
                        }
                    }
                )
            }

            // ── SEKSI: Urutan Catatan ─────────────────────────────
            SectionLabel("Urutan Catatan")

            SettingsCard {
                SortOrder.entries.forEachIndexed { index, order ->
                    SettingsRow(
                        icon  = when (order) {
                            SortOrder.DATE_DESC -> Icons.Default.KeyboardArrowDown
                            SortOrder.DATE_ASC  -> Icons.Default.KeyboardArrowUp
                            SortOrder.TITLE_AZ  -> Icons.Default.SortByAlpha
                        },
                        title    = order.label,
                        subtitle = when (order) {
                            SortOrder.DATE_DESC -> "Catatan terbaru muncul pertama"
                            SortOrder.DATE_ASC  -> "Catatan terlama muncul pertama"
                            SortOrder.TITLE_AZ  -> "Urutkan berdasarkan abjad judul"
                        },
                        iconTint = if (sortOrder == order)
                            MaterialTheme.colorScheme.primary
                        else
                            MaterialTheme.colorScheme.onSurfaceVariant,
                        trailing = {
                            RadioButton(
                                selected = sortOrder == order,
                                onClick  = { viewModel.changeSortOrder(order) },
                                colors   = RadioButtonDefaults.colors(
                                    selectedColor = MaterialTheme.colorScheme.primary
                                )
                            )
                        }
                    )
                    if (index < SortOrder.entries.size - 1) {
                        HorizontalDivider(
                            modifier = Modifier.padding(start = 56.dp),
                            color    = MaterialTheme.colorScheme.outline.copy(alpha = 0.25f)
                        )
                    }
                }
            }

            // ── SEKSI: Penyimpanan ────────────────────────────────
            SectionLabel("Penyimpanan")

            SettingsCard {
                SettingsRow(
                    icon     = Icons.Default.Storage,
                    title    = "Database",
                    subtitle = "SQLDelight · SQLite lokal"
                )
                HorizontalDivider(
                    modifier = Modifier.padding(start = 56.dp),
                    color    = MaterialTheme.colorScheme.outline.copy(alpha = 0.25f)
                )
                SettingsRow(
                    icon     = Icons.Default.Tune,
                    title    = "Preferensi",
                    subtitle = "DataStore · multiplatform-settings"
                )
                HorizontalDivider(
                    modifier = Modifier.padding(start = 56.dp),
                    color    = MaterialTheme.colorScheme.outline.copy(alpha = 0.25f)
                )
                SettingsRow(
                    icon     = Icons.Default.CloudOff,
                    title    = "Mode Offline",
                    subtitle = "Data tersimpan lokal, tidak butuh internet"
                )
            }

            // ── SEKSI: Tentang ────────────────────────────────────
            SectionLabel("Tentang Aplikasi")

            SettingsCard {
                SettingsRow(
                    icon     = Icons.Default.Info,
                    title    = "Versi",
                    subtitle = "2.0.0 (Tugas 7 PAM)"
                )
                HorizontalDivider(
                    modifier = Modifier.padding(start = 56.dp),
                    color    = MaterialTheme.colorScheme.outline.copy(alpha = 0.25f)
                )
                SettingsRow(
                    icon     = Icons.Default.School,
                    title    = "Mata Kuliah",
                    subtitle = "IF25-22017 · Pengembangan Aplikasi Mobile"
                )
                HorizontalDivider(
                    modifier = Modifier.padding(start = 56.dp),
                    color    = MaterialTheme.colorScheme.outline.copy(alpha = 0.25f)
                )
                SettingsRow(
                    icon     = Icons.Default.AccountBalance,
                    title    = "Institusi",
                    subtitle = "Institut Teknologi Sumatera · 2025/2026"
                )
            }

            Spacer(Modifier.height(16.dp))
        }
    }
}

// ── Reusable composables ──────────────────────────────────────────

@Composable
private fun SectionLabel(text: String) {
    Text(
        text          = text.uppercase(),
        fontSize      = 11.sp,
        fontWeight    = FontWeight.Bold,
        color         = MaterialTheme.colorScheme.primary,
        letterSpacing = 1.sp,
        modifier      = Modifier.padding(horizontal = 4.dp)
    )
}

@Composable
private fun SettingsCard(content: @Composable ColumnScope.() -> Unit) {
    Card(
        shape     = RoundedCornerShape(16.dp),
        colors    = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(2.dp),
        modifier  = Modifier.fillMaxWidth()
    ) {
        Column(content = content)
    }
}

@Composable
private fun SettingsRow(
    icon     : ImageVector,
    title    : String,
    subtitle : String,
    iconTint : androidx.compose.ui.graphics.Color = MaterialTheme.colorScheme.primary,
    trailing : (@Composable () -> Unit)? = null
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalAlignment     = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier          = Modifier.weight(1f)
        ) {
            Icon(icon, null, tint = iconTint, modifier = Modifier.size(22.dp))
            Spacer(Modifier.width(16.dp))
            Column {
                Text(
                    title,
                    fontWeight = FontWeight.Medium,
                    fontSize   = 15.sp,
                    color      = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    subtitle,
                    fontSize = 12.sp,
                    color    = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
        trailing?.invoke()
    }
}
