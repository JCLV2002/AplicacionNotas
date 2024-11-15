package com.example.practica04.model

import android.graphics.Color
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.serialization.Serializable

@Serializable
@Entity(tableName = "productos")
data class Producto(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    @ColumnInfo(name = "nombre")
    val nombre: String = "",

    @ColumnInfo(name = "descripcion")
    val descripcion: String = "",

    @ColumnInfo(name = "color")
    val color: String? = null, // Color en hexadecimal o null si se usa imagen

    @ColumnInfo(name = "imagen")
    val imagen: String? = null // Ruta de la imagen o null si se usa color
)
