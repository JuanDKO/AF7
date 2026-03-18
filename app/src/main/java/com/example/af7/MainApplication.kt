package com.example.af7

import android.app.Application
import com.example.af7.data.local.AppDatabase
import com.example.af7.data.remote.RetrofitClient
import com.example.af7.data.repository.TodoRepositoryImpl
import com.example.af7.utils.BluetoothScanner
import com.example.af7.utils.CustomNotificationManager
import com.example.af7.utils.PreferencesManager

class MainApplication : Application() {

    val database by lazy { AppDatabase.getDatabase(this) }
    val repository by lazy { TodoRepositoryImpl(database.todoDao(), RetrofitClient.apiService) }
    val preferencesManager by lazy { PreferencesManager(this) }
    val notificationManager by lazy { CustomNotificationManager(this) }
    val bluetoothScanner by lazy { BluetoothScanner(this) }

    override fun onCreate() {
        super.onCreate()
    }
}
