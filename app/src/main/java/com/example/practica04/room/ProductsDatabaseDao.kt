package com.example.practica04.room

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.practica04.model.Producto
import kotlinx.coroutines.flow.Flow

@Dao
interface ProductsDatabaseDao {

    @Query("SELECT * FROM productos")
    fun getProducts(): Flow<List<Producto>>

    @Query("SELECT * FROM productos WHERE id = :id")
    fun getProductById(id: Int): Flow<Producto>

    @Insert(entity = Producto::class, onConflict = OnConflictStrategy.REPLACE)
    suspend fun addProduct(product: Producto)

    @Update(entity = Producto::class, onConflict = OnConflictStrategy.REPLACE)
    suspend fun updateProduct(product: Producto)

    @Delete
    suspend fun deleteProduct(product: Producto)

}