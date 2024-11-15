package com.example.practica04.navigation

import kotlinx.serialization.Serializable

@Serializable
object Home

@Serializable
object ListaProductos

@Serializable
object FormularioProductos

@Serializable
data class EditarProducto(val productId: Int)
