package com.example.af7

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.MailOutline
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.core.content.ContextCompat
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.af7.ui.*
import com.example.af7.ui.theme.AF7Theme

class MainActivity : ComponentActivity() {

    // Usamos el delegado de forma segura
    private val viewModel: MainViewModel by viewModels {
        val app = application as MainApplication
        MainViewModelFactory(
            app.repository,
            app.preferencesManager,
            app.notificationManager,
            app.bluetoothScanner
        )
    }

    // Sistema moderno de solicitud de permisos
    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val granted = permissions.entries.all { it.value }
        if (granted) {
            // Si quieres que pase algo al aceptar, puedes llamar al viewModel aquí
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Lanzar solicitud de permisos inmediatamente
        checkAndRequestPermissions()

        enableEdgeToEdge()
        setContent {
            AF7Theme {
                val navController = rememberNavController()
                val screens = listOf(Screen.Home, Screen.Stats, Screen.Reports, Screen.Settings)

                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    bottomBar = {
                        val navBackStackEntry by navController.currentBackStackEntryAsState()
                        val currentDestination = navBackStackEntry?.destination

                        NavigationBar {
                            screens.forEach { screen ->
                                NavigationBarItem(
                                    icon = {
                                        when (screen) {
                                            Screen.Home -> Icon(Icons.Default.Home, contentDescription = null)
                                            Screen.Stats -> Icon(Icons.Default.Info, contentDescription = null)
                                            Screen.Reports -> Icon(Icons.Default.MailOutline, contentDescription = null)
                                            Screen.Settings -> Icon(Icons.Default.Settings, contentDescription = null)
                                        }
                                    },
                                    label = { Text(screen.title) },
                                    selected = currentDestination?.hierarchy?.any { it.route == screen.route } == true,
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
                ) { innerPadding ->
                    AppNavGraph(
                        navController = navController,
                        viewModel = viewModel,
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }

    private fun checkAndRequestPermissions() {
        val permissions = mutableListOf<String>()

        // Permisos según la versión de Android
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            permissions.add(Manifest.permission.POST_NOTIFICATIONS)
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            permissions.add(Manifest.permission.BLUETOOTH_SCAN)
            permissions.add(Manifest.permission.BLUETOOTH_CONNECT)
        } else {
            permissions.add(Manifest.permission.BLUETOOTH)
            permissions.add(Manifest.permission.BLUETOOTH_ADMIN)
        }

        permissions.add(Manifest.permission.ACCESS_FINE_LOCATION)

        // Solo pedir los que falten
        val missingPermissions = permissions.filter {
            ContextCompat.checkSelfPermission(this, it) != PackageManager.PERMISSION_GRANTED
        }.toTypedArray()

        if (missingPermissions.isNotEmpty()) {
            requestPermissionLauncher.launch(missingPermissions)
        }
    }
}