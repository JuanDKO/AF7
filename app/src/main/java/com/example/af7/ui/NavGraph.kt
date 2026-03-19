package com.example.af7.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.af7.ui.home.HomeScreen
import com.example.af7.ui.reports.ReportsScreen
import com.example.af7.ui.settings.SettingsScreen
import com.example.af7.ui.stats.StatsScreen

sealed class Screen(val route: String, val title: String) {
    data object Home : Screen("home", "Inicio")
    data object Stats : Screen("stats", "Estadísticas")
    data object Reports : Screen("reports", "Informes")
    data object Settings : Screen("settings", "Ajustes")
}

@Composable
fun AppNavGraph(
    navController: NavHostController,
    viewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController,
        startDestination = Screen.Home.route,
        modifier = modifier
    ) {
        composable(Screen.Home.route) {
            HomeScreen(viewModel = viewModel)
        }
        composable(Screen.Stats.route) {
            StatsScreen(viewModel = viewModel)
        }
        composable(Screen.Reports.route) {
            ReportsScreen(viewModel = viewModel)
        }
        composable(Screen.Settings.route) {
            SettingsScreen(viewModel = viewModel)
        }
    }
}