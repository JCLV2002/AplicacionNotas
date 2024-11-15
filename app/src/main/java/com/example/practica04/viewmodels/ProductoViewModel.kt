package com.example.practica04.viewmodels

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.practica04.model.Producto
import com.example.practica04.room.ProductsDatabaseDao
import com.example.practica04.state.ProductoState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class ProductoViewModel(
    private val dao: ProductsDatabaseDao
) : ViewModel() {

    // Estado del modelo.
    var estado by mutableStateOf(ProductoState())
        private set

    // Inicializar del view model.
    init {
        viewModelScope.launch {
            estado = estado.copy(
                estaCargando = true
            )
            // Obtener la lista de productos en la base de datos.
            dao.getProducts().collectLatest {
                estado = estado.copy(
                    productos = it,
                    estaCargando = false
                )
            }

        }
    }

    fun getProductById(id: Int): Producto? {
        return estado.productos.find {
            id == it.id
        }
    }

    fun addProduct(product: Producto) {
        CoroutineScope(Dispatchers.IO).launch {
            dao.addProduct(product)
        }
    }

    fun updateProduct(product: Producto) {
        CoroutineScope(Dispatchers.IO).launch {
            dao.updateProduct(product)
        }
    }

    fun deleteProduct(product: Producto) {
        CoroutineScope(Dispatchers.IO).launch {
            dao.deleteProduct(product)
        }
    }
}