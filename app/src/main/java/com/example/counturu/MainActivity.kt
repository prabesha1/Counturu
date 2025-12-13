package com.example.counturu

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.counturu.navigation.Screen
import com.example.counturu.ui.components.EditCounterDialog
import com.example.counturu.ui.screens.AboutScreen
import com.example.counturu.ui.screens.CounterDetailScreen
import com.example.counturu.ui.screens.FavsScreen
import com.example.counturu.ui.screens.HomeScreen
import com.example.counturu.ui.screens.SettingsScreen
import com.example.counturu.ui.theme.CounturuTheme
import com.example.counturu.viewmodel.CounterViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            CounturuApp()
        }
    }
}

data class BottomNavItem(
    val route: String,
    val title: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CounturuApp() {
    val viewModel: CounterViewModel = viewModel()
    val isDarkMode by viewModel.isDarkMode.collectAsState()
    val navController = rememberNavController()

    val bottomNavItems = listOf(
        BottomNavItem(
            route = Screen.Favs.route,
            title = "Favs",
            selectedIcon = Icons.Filled.Star,
            unselectedIcon = Icons.Outlined.FavoriteBorder
        ),
        BottomNavItem(
            route = Screen.Home.route,
            title = "Home",
            selectedIcon = Icons.Filled.Home,
            unselectedIcon = Icons.Outlined.Home
        ),
        BottomNavItem(
            route = Screen.Settings.route,
            title = "Settings",
            selectedIcon = Icons.Filled.Settings,
            unselectedIcon = Icons.Outlined.Settings
        )
    )

    CounturuTheme(darkTheme = isDarkMode) {
        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val currentRoute = navBackStackEntry?.destination?.route
        val showBottomBar = currentRoute in bottomNavItems.map { it.route }

        Scaffold(
            modifier = Modifier.fillMaxSize(),
            bottomBar = {
                if (showBottomBar) {
                    NavigationBar(
                        containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.95f),
                        tonalElevation = 8.dp
                    ) {
                        val currentDestination = navBackStackEntry?.destination

                        bottomNavItems.forEach { item ->
                            val selected = currentDestination?.hierarchy?.any {
                                it.route == item.route
                            } == true

                            NavigationBarItem(
                                icon = {
                                    Icon(
                                        imageVector = if (selected) item.selectedIcon else item.unselectedIcon,
                                        contentDescription = item.title
                                    )
                                },
                                label = { Text(item.title) },
                                selected = selected,
                                onClick = {
                                    navController.navigate(item.route) {
                                        popUpTo(navController.graph.findStartDestination().id) {
                                            saveState = true
                                        }
                                        launchSingleTop = true
                                        restoreState = true
                                    }
                                }
                            )
                        }
                    }
                }
            }
        ) { innerPadding ->
            NavHost(
                navController = navController,
                startDestination = Screen.Home.route,
                modifier = Modifier.padding(innerPadding)
            ) {
                composable(Screen.Favs.route) {
                    FavsScreen(
                        viewModel = viewModel,
                        onCounterClick = { counter ->
                            navController.navigate("counter_detail/${counter.id}")
                        }
                    )
                }

                composable(Screen.Home.route) {
                    HomeScreen(
                        viewModel = viewModel,
                        onCounterClick = { counter ->
                            navController.navigate("counter_detail/${counter.id}")
                        }
                    )
                }

                composable(Screen.Settings.route) {
                    SettingsScreen(
                        viewModel = viewModel,
                        onAboutClick = { navController.navigate("about") }
                    )
                }

                composable("about") {
                    AboutScreen(
                        onBack = { navController.popBackStack() }
                    )
                }

                composable(
                    route = "counter_detail/{counterId}",
                    arguments = listOf(navArgument("counterId") { type = NavType.LongType })
                ) { backStackEntry ->
                    val counterId = backStackEntry.arguments?.getLong("counterId") ?: 0L
                    val counter = viewModel.allCounters.collectAsState().value.find { it.id == counterId }
                    var showEditDialog by remember { mutableStateOf(false) }

                    counter?.let {
                        CounterDetailScreen(
                            counter = it,
                            onBack = { navController.popBackStack() },
                            onEdit = { showEditDialog = true },
                            onAddRemark = { remark: String ->
                                // TODO: Implement add remark functionality
                            },
                            onUpdateCounter = { updatedCounter ->
                                viewModel.updateCounter(updatedCounter)
                            }
                        )

                        // Edit dialog
                        if (showEditDialog) {
                            EditCounterDialog(
                                counter = it,
                                onDismiss = { showEditDialog = false },
                                onSave = { updatedCounter ->
                                    viewModel.updateCounter(updatedCounter)
                                    showEditDialog = false
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}

