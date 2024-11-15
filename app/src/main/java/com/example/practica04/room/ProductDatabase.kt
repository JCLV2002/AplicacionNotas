package com.example.practica04.room

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.practica04.model.Producto

@Database(
    entities = [Producto::class],
    version = 3,
    exportSchema = false
)
abstract class ProductDatabase : RoomDatabase() {
    abstract fun productsDao(): ProductsDatabaseDao
}