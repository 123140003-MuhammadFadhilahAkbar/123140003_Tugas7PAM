package org.example.project.screens

// ================================================================
// ProfileScreenWrapper.kt — DIPERBARUI
// Lokasi: src/commonMain/kotlin/org/example/project/screens/
//
// Perubahan dari Tugas 5:
//  - Toggle dark mode DIHAPUS dari sini (sudah di SettingsScreen)
//  - Hamburger menu tetap ada (konsisten dengan screen lain)
// ================================================================

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import org.jetbrains.compose.resources.painterResource
import org.example.project.viewmodel.ProfileViewModel
import org.example.project.viewmodel.ProfileUiState
import org.example.project.viewmodel.UserProfile
import tugas7_pam.composeapp.generated.resources.Res
import tugas7_pam.composeapp.generated.resources.profile_pict

private val GreenOnline   = Color(0xFF22C55E)
private val GradientStart = Color(0xFF1A73E8)
private val GradientEnd   = Color(0xFF6C63FF)

@Composable
fun ProfileScreenWrapper(
    viewModel   : ProfileViewModel,
    onMenuClick : () -> Unit = {}
) {
    val uiState = viewModel.uiState

    if (uiState.isEditing) {
        EditProfileScreen(
            uiState          = uiState,
            onNameChange     = viewModel::onNameChange,
            onTitleChange    = viewModel::onTitleChange,
            onBioChange      = viewModel::onBioChange,
            onEmailChange    = viewModel::onEmailChange,
            onPhoneChange    = viewModel::onPhoneChange,
            onLocationChange = viewModel::onLocationChange,
            onSave           = viewModel::saveProfile,
            onCancel         = viewModel::cancelEditing
        )
    } else {
        ViewProfileScreen(
            uiState         = uiState,
            onEditClick     = viewModel::startEditing,
            onToggleContact = viewModel::toggleContact,
            onClearSuccess  = viewModel::clearSaveSuccess,
            onMenuClick     = onMenuClick
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ViewProfileScreen(
    uiState         : ProfileUiState,
    onEditClick     : () -> Unit,
    onToggleContact : () -> Unit,
    onClearSuccess  : () -> Unit,
    onMenuClick     : () -> Unit = {}
) {
    val p = uiState.profile

    LaunchedEffect(uiState.saveSuccess) {
        if (uiState.saveSuccess) {
            delay(3000)
            onClearSuccess()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                navigationIcon = {
                    IconButton(onClick = onMenuClick) {
                        Icon(
                            Icons.Default.Menu, "Menu",
                            tint = MaterialTheme.colorScheme.onSurface
                        )
                    }
                },
                title = {
                    Column {
                        Text("Profil", fontWeight = FontWeight.SemiBold)
                        Text(
                            "Informasi mahasiswa",
                            fontSize = 12.sp,
                            color    = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                },
                actions = {
                    TextButton(onClick = onEditClick) {
                        Text(
                            "✏ Edit",
                            color      = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        }
    ) { padding ->
        Column(
            modifier            = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(padding),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            HeroHeader(name = p.name, title = p.title, status = p.status)
            Spacer(Modifier.height(20.dp))

            Row(
                modifier              = Modifier.fillMaxWidth().padding(horizontal = 20.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                StatCard(value = p.angkatan, label = "Angkatan", modifier = Modifier.weight(1f))
                StatCard(value = p.prodi,    label = "Prodi",    modifier = Modifier.weight(1f))
                StatCard(value = p.projects, label = "Proyek",   modifier = Modifier.weight(1f))
            }

            Spacer(Modifier.height(20.dp))
            BioCard(text = p.bio, modifier = Modifier.padding(horizontal = 20.dp))
            Spacer(Modifier.height(16.dp))

            // Notifikasi simpan berhasil
            AnimatedVisibility(
                visible = uiState.saveSuccess,
                enter   = expandVertically() + fadeIn(),
                exit    = shrinkVertically() + fadeOut()
            ) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 4.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFD6F5E4)),
                    shape  = RoundedCornerShape(12.dp)
                ) {
                    Text(
                        "✅  Profil berhasil disimpan!",
                        modifier   = Modifier.padding(14.dp),
                        color      = Color(0xFF0A4A28),
                        fontWeight = FontWeight.Medium,
                        fontSize   = 14.sp
                    )
                }
            }

            Spacer(Modifier.height(4.dp))

            Button(
                onClick  = onToggleContact,
                modifier = Modifier.padding(horizontal = 20.dp).fillMaxWidth(),
                shape    = RoundedCornerShape(12.dp),
                colors   = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary
                )
            ) {
                Text(
                    if (uiState.showContact) "Sembunyikan Kontak" else "Tampilkan Kontak",
                    fontWeight = FontWeight.SemiBold
                )
            }

            AnimatedVisibility(
                visible = uiState.showContact,
                enter   = expandVertically() + fadeIn(),
                exit    = shrinkVertically() + fadeOut()
            ) {
                ContactCard(
                    profile  = p,
                    modifier = Modifier.padding(horizontal = 20.dp, vertical = 8.dp)
                )
            }

            Spacer(Modifier.height(32.dp))
            Text(
                "© 2025 · IF25-22017 · ITERA",
                fontSize  = 12.sp,
                color     = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )
            Spacer(Modifier.height(24.dp))
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun EditProfileScreen(
    uiState          : ProfileUiState,
    onNameChange     : (String) -> Unit,
    onTitleChange    : (String) -> Unit,
    onBioChange      : (String) -> Unit,
    onEmailChange    : (String) -> Unit,
    onPhoneChange    : (String) -> Unit,
    onLocationChange : (String) -> Unit,
    onSave           : () -> Unit,
    onCancel         : () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title          = { Text("Edit Profil", fontWeight = FontWeight.SemiBold) },
                navigationIcon = {
                    TextButton(onClick = onCancel) {
                        Text("Batal", color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                },
                actions = {
                    TextButton(onClick = onSave) {
                        Text("Simpan",
                            fontWeight = FontWeight.Bold,
                            color      = MaterialTheme.colorScheme.primary)
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
                .verticalScroll(rememberScrollState())
                .padding(padding)
                .padding(horizontal = 20.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Text("👤  Informasi Dasar",
                fontSize   = 13.sp,
                fontWeight = FontWeight.SemiBold,
                color      = MaterialTheme.colorScheme.primary)

            LabeledTextField(
                label         = "Nama *",
                value         = uiState.editName,
                onValueChange = onNameChange,
                placeholder   = "Nama lengkap",
                isError       = uiState.errorMessage != null && uiState.editName.isBlank(),
                supportingText = if (uiState.errorMessage != null && uiState.editName.isBlank())
                    uiState.errorMessage else null
            )
            LabeledTextField(
                label         = "Titel / Deskripsi Singkat",
                value         = uiState.editTitle,
                onValueChange = onTitleChange,
                placeholder   = "cth: Mobile Developer · ITERA '23"
            )
            LabeledTextField(
                label         = "Bio",
                value         = uiState.editBio,
                onValueChange = onBioChange,
                placeholder   = "Ceritakan tentang dirimu...",
                singleLine    = false,
                minLines      = 3,
                maxLines      = 5
            )

            HorizontalDivider()

            Text("📞  Kontak",
                fontSize   = 13.sp,
                fontWeight = FontWeight.SemiBold,
                color      = MaterialTheme.colorScheme.primary)

            LabeledTextField(
                label         = "Email",
                value         = uiState.editEmail,
                onValueChange = onEmailChange,
                placeholder   = "email@student.itera.ac.id"
            )
            LabeledTextField(
                label         = "Nomor Telepon",
                value         = uiState.editPhone,
                onValueChange = onPhoneChange,
                placeholder   = "+62 8xx-xxxx-xxxx"
            )
            LabeledTextField(
                label         = "Lokasi",
                value         = uiState.editLocation,
                onValueChange = onLocationChange,
                placeholder   = "Kota, Provinsi"
            )

            AnimatedVisibility(
                visible = uiState.errorMessage != null,
                enter   = fadeIn() + expandVertically(),
                exit    = fadeOut() + shrinkVertically()
            ) {
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.errorContainer),
                    shape  = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        "⚠️  ${uiState.errorMessage}",
                        modifier = Modifier.padding(12.dp),
                        color    = MaterialTheme.colorScheme.onErrorContainer,
                        fontSize = 13.sp
                    )
                }
            }

            Row(
                modifier              = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedButton(
                    onClick  = onCancel,
                    modifier = Modifier.weight(1f),
                    shape    = RoundedCornerShape(12.dp)
                ) { Text("Batal") }

                Button(
                    onClick  = onSave,
                    modifier = Modifier.weight(1f),
                    shape    = RoundedCornerShape(12.dp),
                    colors   = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary)
                ) { Text("Simpan Profil", fontWeight = FontWeight.SemiBold) }
            }
            Spacer(Modifier.height(16.dp))
        }
    }
}

// ── Reusable Components ───────────────────────────────────────────

@Composable
private fun LabeledTextField(
    label          : String,
    value          : String,
    onValueChange  : (String) -> Unit,
    modifier       : Modifier = Modifier,
    placeholder    : String   = "",
    singleLine     : Boolean  = true,
    minLines       : Int      = 1,
    maxLines       : Int      = 1,
    isError        : Boolean  = false,
    supportingText : String?  = null
) {
    OutlinedTextField(
        value          = value,
        onValueChange  = onValueChange,
        label          = { Text(label, fontSize = 13.sp) },
        placeholder    = if (placeholder.isNotEmpty()) {
            { Text(placeholder, fontSize = 13.sp) }
        } else null,
        singleLine     = singleLine,
        minLines       = minLines,
        maxLines       = maxLines,
        isError        = isError,
        supportingText = supportingText?.let {
            { Text(it, color = MaterialTheme.colorScheme.error, fontSize = 12.sp) }
        },
        modifier = modifier.fillMaxWidth(),
        shape    = RoundedCornerShape(12.dp)
    )
}

@Composable
private fun StatCard(value: String, label: String, modifier: Modifier = Modifier) {
    Card(
        modifier  = modifier,
        shape     = RoundedCornerShape(12.dp),
        colors    = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier            = Modifier.fillMaxWidth().padding(vertical = 14.dp)
        ) {
            Text(value,
                fontSize   = 20.sp,
                fontWeight = FontWeight.ExtraBold,
                color      = MaterialTheme.colorScheme.primary)
            Text(label,
                fontSize = 11.sp,
                color    = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

@Composable
private fun InfoItem(emoji: String, label: String, value: String) {
    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
        Box(
            contentAlignment = Alignment.Center,
            modifier         = Modifier
                .size(40.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(MaterialTheme.colorScheme.surfaceVariant)
        ) { Text(emoji, fontSize = 18.sp) }
        Spacer(Modifier.width(12.dp))
        Column {
            Text(label,
                fontSize = 11.sp,
                color    = MaterialTheme.colorScheme.onSurfaceVariant)
            Text(value,
                fontSize   = 13.sp,
                color      = MaterialTheme.colorScheme.onSurface,
                fontWeight = FontWeight.Medium)
        }
    }
}

@Composable
private fun HeroHeader(name: String, title: String, status: String) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(280.dp)
            .background(
                Brush.verticalGradient(colors = listOf(GradientStart, GradientEnd))
            ),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Box(contentAlignment = Alignment.BottomEnd) {
                Image(
                    painter            = painterResource(Res.drawable.profile_pict),
                    contentDescription = "Foto Profil",
                    contentScale       = ContentScale.Crop,
                    modifier           = Modifier
                        .size(110.dp)
                        .clip(CircleShape)
                        .border(3.dp, Color.White, CircleShape)
                )
                Box(
                    modifier = Modifier
                        .size(20.dp)
                        .background(GreenOnline, CircleShape)
                        .border(2.dp, Color.White, CircleShape)
                )
            }
            Spacer(Modifier.height(12.dp))
            Text(name,
                fontSize   = 20.sp,
                fontWeight = FontWeight.Bold,
                color      = Color.White,
                textAlign  = TextAlign.Center,
                modifier   = Modifier.padding(horizontal = 16.dp))
            Spacer(Modifier.height(4.dp))
            Text(title,
                fontSize  = 13.sp,
                color     = Color.White.copy(alpha = 0.85f),
                fontStyle = FontStyle.Italic)
            Spacer(Modifier.height(10.dp))
            Surface(
                shape = RoundedCornerShape(20.dp),
                color = Color.White.copy(alpha = 0.2f)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier          = Modifier.padding(horizontal = 12.dp, vertical = 5.dp)
                ) {
                    Box(modifier = Modifier.size(8.dp).background(GreenOnline, CircleShape))
                    Spacer(Modifier.width(6.dp))
                    Text(status,
                        fontSize   = 12.sp,
                        color      = Color.White,
                        fontWeight = FontWeight.Medium)
                }
            }
        }
    }
}

@Composable
private fun BioCard(text: String, modifier: Modifier = Modifier) {
    Card(
        modifier  = modifier.fillMaxWidth(),
        shape     = RoundedCornerShape(16.dp),
        colors    = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("👤", fontSize = 16.sp)
                Spacer(Modifier.width(8.dp))
                Text("Tentang Saya",
                    fontWeight = FontWeight.Bold,
                    fontSize   = 15.sp,
                    color      = MaterialTheme.colorScheme.onSurface)
            }
            Spacer(Modifier.height(8.dp))
            HorizontalDivider()
            Spacer(Modifier.height(8.dp))
            Text(text,
                fontSize  = 13.sp,
                color     = MaterialTheme.colorScheme.onSurfaceVariant,
                lineHeight = 20.sp,
                textAlign = TextAlign.Justify)
        }
    }
}

@Composable
private fun ContactCard(profile: UserProfile, modifier: Modifier = Modifier) {
    Card(
        modifier  = modifier.fillMaxWidth(),
        shape     = RoundedCornerShape(16.dp),
        colors    = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("📞", fontSize = 16.sp)
                Spacer(Modifier.width(8.dp))
                Text("Informasi Kontak",
                    fontWeight = FontWeight.Bold,
                    fontSize   = 15.sp,
                    color      = MaterialTheme.colorScheme.onSurface)
            }
            Spacer(Modifier.height(8.dp))
            HorizontalDivider()
            Spacer(Modifier.height(12.dp))
            InfoItem("📧", "Email",     profile.email)
            Spacer(Modifier.height(10.dp))
            InfoItem("📱", "Telepon",   profile.phone)
            Spacer(Modifier.height(10.dp))
            InfoItem("📍", "Lokasi",    profile.location)
            Spacer(Modifier.height(10.dp))
            InfoItem("🎓", "Institusi", profile.institution)
        }
    }
}
