package com.example.af7.ui.settings

import android.Manifest
import android.os.Build
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.af7.ui.MainViewModel

@Composable
fun SettingsScreen(viewModel: MainViewModel) {
    var darkMode by remember { mutableStateOf(viewModel.preferencesManager.isDarkModeEnabled) }
    var notifsEnabled by remember { mutableStateOf(viewModel.preferencesManager.areNotificationsEnabled) }
    var username by remember { mutableStateOf(viewModel.preferencesManager.username) }

    val scannedDevices by viewModel.bluetoothScanner.scannedDevices.collectAsState()
    val isScanning = remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text("Preferencias", style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = username,
            onValueChange = {
                username = it
                viewModel.preferencesManager.username = it
            },
            label = { Text("Nombre de Usuario") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text("Activar Notificaciones")
            Switch(
                checked = notifsEnabled,
                onCheckedChange = {
                    notifsEnabled = it
                    viewModel.preferencesManager.areNotificationsEnabled = it
                }
            )
        }
        Spacer(modifier = Modifier.height(8.dp))

        Button(onClick = { viewModel.refreshTodos() }, modifier = Modifier.fillMaxWidth()) {
            Text("Forzar Sincronización")
        }

        Spacer(modifier = Modifier.height(24.dp))
        Text("Escáner Bluetooth", style = MaterialTheme.typography.titleLarge)
        Spacer(modifier = Modifier.height(8.dp))

        Button(
            onClick = {
                if (isScanning.value) {
                    viewModel.bluetoothScanner.stopScan()
                    isScanning.value = false
                } else {
                    viewModel.bluetoothScanner.startScan()
                    isScanning.value = true
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(if (isScanning.value) "Detener Escaneo" else "Iniciar Escaneo Bluetooth")
        }

        Spacer(modifier = Modifier.height(8.dp))
        Text("Dispositivos Encontrados (${scannedDevices.size})")
        LazyColumn(modifier = Modifier.fillMaxHeight()) {
            items(scannedDevices) { device ->
                Text("- $device", modifier = Modifier.padding(vertical = 4.dp))
            }
        }
    }
}
