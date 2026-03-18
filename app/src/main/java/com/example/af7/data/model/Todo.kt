package com.example.af7.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

@Entity(tableName = "todos")
data class Todo(
    @SerializedName("id")
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    @SerializedName("userId")
    val userId: Int? = 0, // El ? evita el crash si el servidor no lo envía

    @SerializedName("title")
    val title: String = "Sin título", // Valor por defecto

    @SerializedName("completed")
    val completed: Boolean = false
)
