package com.example.af7.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.af7.data.model.Todo

// Si haces cambios en el modelo Todo, puedes subir la version a 2
@Database(entities = [Todo::class], version = 2, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {

    abstract fun todoDao(): TodoDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            // Si la instancia ya existe, la devolvemos; si no, la creamos
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "af7_database" // Mantén un solo nombre aquí
                )
                    .fallbackToDestructiveMigration() // Esto evita que la app se cierre al cambiar el modelo
                    .build()

                INSTANCE = instance
                instance
            }
        }
    }
}
