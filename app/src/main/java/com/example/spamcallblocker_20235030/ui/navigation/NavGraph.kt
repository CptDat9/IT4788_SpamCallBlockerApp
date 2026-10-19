package com.example.spamcallblocker_20235030.ui.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.spamcallblocker_20235030.SpamBlockerApp
import com.example.spamcallblocker_20235030.ui.brand.BrandDetailScreen
import com.example.spamcallblocker_20235030.ui.brand.BrandDetailViewModel
import com.example.spamcallblocker_20235030.ui.brand.BrandListScreen
import com.example.spamcallblocker_20235030.ui.brand.BrandViewModel
import com.example.spamcallblocker_20235030.ui.dashboard.DashboardScreen
import com.example.spamcallblocker_20235030.ui.dashboard.DashboardViewModel
import com.example.spamcallblocker_20235030.ui.history.HistoryScreen
import com.example.spamcallblocker_20235030.ui.history.HistoryViewModel
import com.example.spamcallblocker_20235030.ui.settings.SettingsScreen
import com.example.spamcallblocker_20235030.ui.settings.SettingsViewModel

@Suppress("UNCHECKED_CAST")
class AppViewModelFactory(private val brandId: Long = -1L) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        val repository = SpamBlockerApp.instance.repository
        return when {
            modelClass.isAssignableFrom(DashboardViewModel::class.java) -> DashboardViewModel(repository) as T
            modelClass.isAssignableFrom(BrandViewModel::class.java) -> BrandViewModel(repository) as T
            modelClass.isAssignableFrom(BrandDetailViewModel::class.java) -> BrandDetailViewModel(repository, brandId) as T
            modelClass.isAssignableFrom(HistoryViewModel::class.java) -> HistoryViewModel(repository) as T
            modelClass.isAssignableFrom(SettingsViewModel::class.java) -> SettingsViewModel(repository) as T
            else -> throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
        }
    }
}

@Composable
fun MainAppNavigation(navController: NavHostController = rememberNavController()) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    // Hide bottom bar when in Brand Detail screen
    val shouldShowBottomBar = currentRoute in Screen.bottomNavItems.map { it.route }

    Scaffold(
        bottomBar = {
            if (shouldShowBottomBar) {
                NavigationBar {
                    Screen.bottomNavItems.forEach { screen ->
                        NavigationBarItem(
                            icon = {
                                screen.icon?.let { icon ->
                                    Icon(imageVector = icon, contentDescription = screen.title)
                                }
                            },
                            label = { Text(screen.title) },
                            selected = currentRoute == screen.route,
                            onClick = {
                                navController.navigate(screen.route) {
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
    ) { paddingValues ->
        NavHost(
            navController = navController,
            startDestination = Screen.Dashboard.route,
            modifier = Modifier.padding(paddingValues)
        ) {
            composable(Screen.Dashboard.route) {
                val vm: DashboardViewModel = viewModel(factory = AppViewModelFactory())
                DashboardScreen(
                    viewModel = vm,
                    onNavigateToBrands = { navController.navigate(Screen.BrandList.route) },
                    onNavigateToHistory = { navController.navigate(Screen.History.route) },
                    onNavigateToSettings = { navController.navigate(Screen.Settings.route) }
                )
            }

            composable(Screen.BrandList.route) {
                val vm: BrandViewModel = viewModel(factory = AppViewModelFactory())
                BrandListScreen(
                    viewModel = vm,
                    onBrandClick = { brandId ->
                        navController.navigate(Screen.BrandDetail.createRoute(brandId))
                    }
                )
            }

            composable(
                route = Screen.BrandDetail.route,
                arguments = listOf(navArgument("brandId") { type = NavType.LongType })
            ) { backStackEntry ->
                val brandId = backStackEntry.arguments?.getLong("brandId") ?: -1L
                val vm: BrandDetailViewModel = viewModel(factory = AppViewModelFactory(brandId))
                BrandDetailScreen(
                    viewModel = vm,
                    onBackClick = { navController.popBackStack() }
                )
            }

            composable(Screen.History.route) {
                val vm: HistoryViewModel = viewModel(factory = AppViewModelFactory())
                HistoryScreen(viewModel = vm)
            }

            composable(Screen.Settings.route) {
                val vm: SettingsViewModel = viewModel(factory = AppViewModelFactory())
                SettingsScreen(viewModel = vm)
            }
        }
    }
}
