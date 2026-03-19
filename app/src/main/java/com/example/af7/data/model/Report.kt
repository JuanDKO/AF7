package com.example.af7.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

@Entity(tableName = "reports")
data class Report(
    @SerializedName("id")
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    @SerializedName("userId")
    val userId: Int? = 1,

    @SerializedName("description")
    val description: String = "",

    @SerializedName("resolved")
    val resolved: Boolean = false
)
