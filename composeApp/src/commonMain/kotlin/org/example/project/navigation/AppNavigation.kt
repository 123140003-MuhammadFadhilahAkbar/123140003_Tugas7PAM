package org.example.project.navigation

// ================================================================
// AppNavigation.kt — GANTI YANG LAMA
// Lokasi: src/commonMain/kotlin/org/example/project/navigation/
//
// Perubahan utama:
//  - Menerima dataSource & settingsManager dari MainActivity
//  - Theme (dark/light) dikontrol oleh SettingsViewModel
//  - Settings screen masuk bottom nav & drawer
//  - Semua screen menerima onMenuClick → konsisten
// ================================================================

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.NavType
import androidx.navigation.compose.*
import androidx.navigation.navArgument
import kotlinx.coroutines.launch
import org.example.project.components.AppDrawerContent
import org.example.project.data.NoteLocalDataSource
import org.example.project.data.SettingsManager
import org.example.project.screens.*
import org.example.project.viewmodel.NoteViewModel
import org.example.project.viewmodel.ProfileViewModel
import org.example.project.viewmodel.SettingsViewModel
import org.example.project.provideDataSource
import org.example.project.provideSettingsManager

// ── Bottom Nav Items ──────────────────────────────────────────────
sealed class BottomNavItem(
    val screen       : Screen,
    val icon         : ImageVector,
    val selectedIcon : ImageVector,
    val label        : String
) {
    object Notes    : BottomNavItem(Screen.NoteList,  Icons.Default.List,           Icons.Default.List,      "Catatan")
    object Favorites: BottomNavItem(Screen.Favorites, Icons.Default.FavoriteBorder, Icons.Filled.Favorite,   "Favorit")
    object Settings : BottomNavItem(Screen.Settings,  Icons.Default.Settings,       Icons.Filled.Settings,   "Pengaturan")
    object Profile  : BottomNavItem(Screen.Profile,   Icons.Default.Person,         Icons.Filled.Person,     "Profil")
}

@Composable
fun AppNavigation() {
    val dataSource      = remember { provideDataSource() }
    val settingsManager = remember { provideSettingsManager() }

    val noteViewModel     = remember { NoteViewModel(dataSource, settingsManager) }
    val settingsViewModel = remember { SettingsViewModel(settingsManager) }
    val profileViewModel  = remember { ProfileViewModel() }

    val isDarkMode by settingsViewModel.isDarkMode.collectAsState()

    AnimatedContent(
        targetState    = isDarkMode,
        transitionSpec = { fadeIn() togetherWith fadeOut() },
        label          = "theme_transition"
    ) { isDark ->
        MaterialTheme(
            colorScheme = if (isDark) buildDarkColorScheme() else buildLightColorScheme()
        ) {
            val navController  = rememberNavController()
            val drawerState    = rememberDrawerState(DrawerValue.Closed)
            val scope          = rememberCoroutineScope()
            val navBackStack   by navController.currentBackStackEntryAsState()
            val currentRoute   = navBackStack?.destination?.route

            // Route yang menampilkan hamburger & bottom nav
            val primaryRoutes = listOf(
                Screen.NoteList.route,
                Screen.Favorites.route,
                Screen.Settings.route,
                Screen.Profile.route
            )

            ModalNavigationDrawer(
                drawerState     = drawerState,
                gesturesEnabled = currentRoute in primaryRoutes,
                drawerContent   = {
                    AppDrawerContent(
                        currentRoute  = currentRoute,
                        onNavigate    = { route ->
                            navController.navigate(route) {
                                popUpTo(navController.graph.startDestinationId) { saveState = true }
                                launchSingleTop = true
                                restoreState    = true
                            }
                        },
                        onCloseDrawer = { scope.launch { drawerState.close() } }
                    )
                }
            ) {
                Scaffold(
                    containerColor = MaterialTheme.colorScheme.background,
                    bottomBar      = { AppBottomNavBar(navController) }
                ) { innerPadding ->
                    NavHost(
                        navController    = navController,
                        startDestination = Screen.NoteList.route,
                        modifier         = Modifier.padding(innerPadding)
                    ) {

                        // ── Catatan ───────────────────────────────
                        composable(Screen.NoteList.route) {
                            NoteListScreen(
                                viewModel   = noteViewModel,
                                onNoteClick = { id -> navController.navigate(Screen.NoteDetail.createRoute(id)) },
                                onAddClick  = { navController.navigate(Screen.AddNote.route) },
                                onMenuClick = { scope.launch { drawerState.open() } }
                            )
                        }

                        // ── Favorit ───────────────────────────────
                        composable(Screen.Favorites.route) {
                            FavoritesScreen(
                                viewModel   = noteViewModel,
                                onNoteClick = { id -> navController.navigate(Screen.NoteDetail.createRoute(id)) },
                                onMenuClick = { scope.launch { drawerState.open() } }
                            )
                        }

                        // ── Pengaturan ────────────────────────────
                        composable(Screen.Settings.route) {
                            SettingsScreen(
                                viewModel   = settingsViewModel,
                                onMenuClick = { scope.launch { drawerState.open() } }
                            )
                        }

                        // ── Profil ────────────────────────────────
                        composable(Screen.Profile.route) {
                            ProfileScreenWrapper(
                                viewModel   = profileViewModel,
                                onMenuClick = { scope.launch { drawerState.open() } }
                            )
                        }

                        // ── Detail Catatan ────────────────────────
                        composable(
                            route     = Screen.NoteDetail.route,
                            arguments = listOf(navArgument("noteId") { type = NavType.IntType })
                        ) { back ->
                            val noteId = back.arguments?.getInt("noteId") ?: return@composable
                            LaunchedEffect(noteId) { noteViewModel.loadNoteById(noteId) }
                            NoteDetailScreen(
                                noteId    = noteId,
                                viewModel = noteViewModel,
                                onBack    = { navController.popBackStack() },
                                onEdit    = { id -> navController.navigate(Screen.EditNote.createRoute(id)) }
                            )
                        }

                        // ── Tambah Catatan ────────────────────────
                        composable(Screen.AddNote.route) {
                            AddNoteScreen(
                                viewModel = noteViewModel,
                                onBack    = { navController.popBackStack() },
                                onSaved   = { navController.popBackStack() }
                            )
                        }

                        // ── Edit Catatan ──────────────────────────
                        composable(
                            route     = Screen.EditNote.route,
                            arguments = listOf(navArgument("noteId") { type = NavType.IntType })
                        ) { back ->
                            val noteId = back.arguments?.getInt("noteId") ?: return@composable
                            EditNoteScreen(
                                noteId    = noteId,
                                viewModel = noteViewModel,
                                onBack    = { navController.popBackStack() },
                                onSaved   = {
                                    navController.popBackStack(
                                        route     = Screen.NoteList.route,
                                        inclusive = false
                                    )
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}

// ── Bottom Navigation Bar ─────────────────────────────────────────
@Composable
fun AppBottomNavBar(navController: NavController) {
    val items = listOf(
        BottomNavItem.Notes,
        BottomNavItem.Favorites,
        BottomNavItem.Settings,
        BottomNavItem.Profile
    )
    val navBackStack by navController.currentBackStackEntryAsState()
    val currentRoute  = navBackStack?.destination?.route

    if (currentRoute !in items.map { it.screen.route }) return

    NavigationBar(
        containerColor = MaterialTheme.colorScheme.surface,
        tonalElevation = 4.dp
    ) {
        items.forEach { item ->
            val selected = currentRoute == item.screen.route
            NavigationBarItem(
                selected = selected,
                onClick  = {
                    navController.navigate(item.screen.route) {
                        popUpTo(navController.graph.startDestinationId) { saveState = true }
                        launchSingleTop = true
                        restoreState    = true
                    }
                },
                icon   = { Icon(if (selected) item.selectedIcon else item.icon, item.label) },
                label  = { Text(item.label) },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor   = MaterialTheme.colorScheme.primary,
                    selectedTextColor   = MaterialTheme.colorScheme.primary,
                    unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    indicatorColor      = MaterialTheme.colorScheme.primaryContainer
                )
            )
        }
    }
}

// ── Color Schemes ─────────────────────────────────────────────────
private val PrimaryBlue    = Color(0xFF1A73E8)
private val AccentCyan     = Color(0xFF00BCD4)
private val SurfaceLight   = Color(0xFFF8FAFF)
private val CardWhite      = Color(0xFFFFFFFF)
private val TextPrimary    = Color(0xFF1C1C2E)
private val TextSecondary  = Color(0xFF6B7280)
private val DarkBackground = Color(0xFF0F1117)
private val DarkSurface    = Color(0xFF1A1D26)
private val DarkSurfaceVar = Color(0xFF252836)
private val DarkOnSurface  = Color(0xFFE8EAF0)
private val DarkOnSurfVar  = Color(0xFF9EA3B0)
private val DarkPrimary    = Color(0xFF7AB3F5)
private val DarkOutline    = Color(0xFF2E3347)

fun buildLightColorScheme() = lightColorScheme(
    primary          = PrimaryBlue,
    secondary        = AccentCyan,
    background       = SurfaceLight,
    surface          = CardWhite,
    surfaceVariant   = SurfaceLight,
    onPrimary        = Color.White,
    onBackground     = TextPrimary,
    onSurface        = TextPrimary,
    onSurfaceVariant = TextSecondary,
    error            = Color(0xFFBA1A1A),
    errorContainer   = Color(0xFFFFDAD6),
    onErrorContainer = Color(0xFF410002),
    outline          = Color(0xFFE0E4F0),
    primaryContainer = Color(0xFFD3E4FF)
)

fun buildDarkColorScheme() = darkColorScheme(
    primary          = DarkPrimary,
    secondary        = AccentCyan,
    background       = DarkBackground,
    surface          = DarkSurface,
    surfaceVariant   = DarkSurfaceVar,
    onPrimary        = Color(0xFF003060),
    onBackground     = DarkOnSurface,
    onSurface        = DarkOnSurface,
    onSurfaceVariant = DarkOnSurfVar,
    error            = Color(0xFFFFB4AB),
    errorContainer   = Color(0xFF93000A),
    onErrorContainer = Color(0xFFFFDAD6),
    outline          = DarkOutline,
    primaryContainer = Color(0xFF004496)
)
