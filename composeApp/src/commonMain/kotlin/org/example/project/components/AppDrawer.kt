package org.example.project.components

// ================================================================
// AppDrawer.kt — GANTI YANG LAMA
// Lokasi: src/commonMain/kotlin/org/example/project/components/
//
// Perubahan: tambah item "Pengaturan" agar konsisten dengan bottom nav
// ================================================================

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

private data class DrawerItem(
    val icon  : ImageVector,
    val label : String,
    val route : String
)

@Composable
fun AppDrawerContent(
    currentRoute  : String?,
    onNavigate    : (String) -> Unit,
    onCloseDrawer : () -> Unit
) {
    val items = listOf(
        DrawerItem(Icons.Default.List,     "Catatan",     "note_list"),
        DrawerItem(Icons.Default.Favorite, "Favorit",     "favorites"),
        DrawerItem(Icons.Default.Settings, "Pengaturan",  "settings"),
        DrawerItem(Icons.Default.Person,   "Profil",      "profile"),
    )

    ModalDrawerSheet(
        drawerShape = RoundedCornerShape(topEnd = 24.dp, bottomEnd = 24.dp)
    ) {
        // ── Header gradien ────────────────────────────────────────
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(180.dp)
                .background(
                    Brush.verticalGradient(
                        colors = listOf(Color(0xFF1A73E8), Color(0xFF6C63FF))
                    )
                ),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Box(
                    modifier = Modifier
                        .size(72.dp)
                        .clip(CircleShape)
                        .background(Color.White.copy(alpha = 0.2f)),
                    contentAlignment = Alignment.Center
                ) {
                    Text("📝", fontSize = 34.sp)
                }
                Spacer(Modifier.height(10.dp))
                Text(
                    "Notes App",
                    color      = Color.White,
                    fontSize   = 20.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    "Tugas 7 PAM · ITERA",
                    color    = Color.White.copy(alpha = 0.75f),
                    fontSize = 12.sp
                )
            }
        }

        Spacer(Modifier.height(8.dp))

        // ── Label seksi ───────────────────────────────────────────
        Text(
            text       = "MENU UTAMA",
            fontSize   = 10.sp,
            fontWeight = FontWeight.Bold,
            color      = MaterialTheme.colorScheme.onSurfaceVariant,
            letterSpacing = 1.sp,
            modifier   = Modifier.padding(horizontal = 24.dp, vertical = 6.dp)
        )

        // ── Item navigasi ─────────────────────────────────────────
        items.forEach { item ->
            val selected = currentRoute == item.route
            NavigationDrawerItem(
                icon   = {
                    Icon(
                        item.icon,
                        contentDescription = item.label,
                        tint = if (selected) MaterialTheme.colorScheme.primary
                               else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                },
                label  = {
                    Text(
                        item.label,
                        fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Normal,
                        fontSize   = 15.sp
                    )
                },
                selected = selected,
                onClick  = { onNavigate(item.route); onCloseDrawer() },
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 2.dp),
                colors   = NavigationDrawerItemDefaults.colors(
                    selectedContainerColor   = MaterialTheme.colorScheme.primaryContainer,
                    selectedTextColor        = MaterialTheme.colorScheme.primary,
                    selectedIconColor        = MaterialTheme.colorScheme.primary,
                    unselectedContainerColor = Color.Transparent
                )
            )
        }

        Spacer(Modifier.weight(1f))

        // ── Footer ────────────────────────────────────────────────
        HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp))
        Row(
            modifier          = Modifier.fillMaxWidth().padding(20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                Icons.Default.Info, null,
                tint     = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(15.dp)
            )
            Spacer(Modifier.width(8.dp))
            Text(
                "v2.0.0 · SQLDelight + DataStore",
                fontSize = 11.sp,
                color    = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
