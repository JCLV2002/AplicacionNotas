package com.example.practica04

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.practica04.navigation.NavManager
import com.example.practica04.room.ProductDatabase
import com.example.practica04.room.ProductDatabase_Impl
import com.example.practica04.room.ProductsDatabaseDao
import com.example.practica04.room.ProductsDatabaseDao_Impl
import com.example.practica04.ui.theme.Practica04Theme
import com.example.practica04.viewmodels.ProductoViewModel

class MainActivity : ComponentActivity() {

    private val db by lazy {
        Room.databaseBuilder(
            context = applicationContext,
            klass = ProductDatabase::class.java,
            name = "products.db"
        )
    }

    // Método que es llamado al crear la activity.
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()

        // Instancia del viewModel.
        val viewModel: ProductoViewModel = ProductoViewModel(db.build().productsDao())

        // Establecer el contenido de la aplicación.
        setContent {
            Practica04Theme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    NavManager(viewModel = viewModel, modifier = Modifier.padding(innerPadding))
                }
            }
        }
    }
}