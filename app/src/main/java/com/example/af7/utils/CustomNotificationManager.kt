package com.example.af7.utils

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat

class CustomNotificationManager(private val context: Context) {

    companion object {
        const val CHANNEL_ID = "af7_notifs"
        const val NOTIFICATION_ID = 1001
    }

    init {
        createNotificationChannel()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            try {
                val name = "Sincronización"
                val descriptionText = "Notificaciones de estado de tareas"
                val importance = NotificationManager.IMPORTANCE_DEFAULT
                val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
                    description = descriptionText
                }

                val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
                notificationManager.createNotificationChannel(channel)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun showSyncNotification(title: String, message: String) {
        // 1. Verificar si las notificaciones están activadas en preferencias
        val prefs = PreferencesManager(context)
        if (!prefs.areNotificationsEnabled) return

        // 2. Verificar permiso para Android 13+
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                return
            }
        }

        try {
            // 3. Crear la notificación con un icono del sistema por seguridad
            // Usamos android.R.drawable.stat_sys_download_done para evitar errores de R.drawable
            val builder = NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(android.R.drawable.stat_sys_download_done)
                .setContentTitle(title)
                .setContentText(message)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setAutoCancel(true)

            // 4. Mostrar la notificación con manejo de errores
            with(NotificationManagerCompat.from(context)) {
                notify(NOTIFICATION_ID, builder.build())
            }
        } catch (e: Exception) {
            // Si algo falla aquí, imprimimos el error en Logcat pero NO cerramos la app
            e.printStackTrace()
        }
    }
}
