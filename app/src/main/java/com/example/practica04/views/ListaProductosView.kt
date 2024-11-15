package com.example.practica04.views

import android.graphics.BitmapFactory
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SmallFloatingActionButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCompositionContext
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.practica04.viewmodels.ProductoViewModel
import com.example.practica04.R
import com.example.practica04.dialog.Alerta
import com.example.practica04.model.Producto
import com.example.practica04.navigation.EditarProducto
import com.example.practica04.navigation.FormularioProductos
import com.example.practica04.navigation.Home
import kotlinx.coroutines.CoroutineStart
//import kotlin.io.encoding.Base64
import kotlin.io.encoding.ExperimentalEncodingApi
import android.util.Base64
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.graphicsLayer


@OptIn(ExperimentalEncodingApi::class, ExperimentalMaterial3Api::class)
@Composable
fun ListaProductosView(viewModel: ProductoViewModel, navController: NavController, modifier: Modifier = Modifier) {
    var productIdToDelete by remember { mutableStateOf(0) }
    var showDeleteDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                ),
                title = {
                    Text(
                        text = "Mis notas",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navController.navigate(Home) }) {
                        Icon(
                            imageVector = Icons.Filled.ArrowBack,
                            contentDescription = "Regresar",
                            tint = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { navController.navigate(FormularioProductos) },
                containerColor = MaterialTheme.colorScheme.secondary,
                contentColor = MaterialTheme.colorScheme.onSecondary
            ) {
                Icon(Icons.Filled.Add, contentDescription = "Agregar producto")
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(MaterialTheme.colorScheme.background),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            val estado = viewModel.estado
            if (estado.estaCargando) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
                }
            } else if (estado.productos.isEmpty()) {
                Column(
                    verticalArrangement = Arrangement.Center,
                    modifier = Modifier.fillMaxSize()
                ) {
                    Text(
                        text = "No hay Notas en existencia",
                        style = MaterialTheme.typography.bodyLarge,
                        textAlign = TextAlign.Center,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    items(estado.productos) { producto ->
                        // Convierte el color hexadecimal a un objeto Color
                        val color = try {
                            Color(android.graphics.Color.parseColor(producto.color))
                        } catch (e: IllegalArgumentException) {
                            MaterialTheme.colorScheme.surface // Color de respaldo si el formato no es válido
                        }

                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { navController.navigate(EditarProducto(productId = producto.id)) }
                                .shadow(4.dp, shape = RoundedCornerShape(16.dp)),
                            colors = CardDefaults.cardColors(
                                containerColor = color // Usa el color del producto
                            ),
                            shape = RoundedCornerShape(16.dp),
                            elevation = CardDefaults.cardElevation(4.dp)
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp)
                            ) {
                                // Muestra la imagen si existe
                                if (producto.imagen?.isNotBlank() == true) {
                                    // Decodifica la cadena base64 correctamente
                                    val imageBytes = Base64.decode(producto.imagen, Base64.DEFAULT)

                                    // Convierte los bytes en un ImageBitmap
                                    val imageBitmap = imageBytes.inputStream().use { BitmapFactory.decodeStream(it) }.asImageBitmap()

                                    Image(
                                        bitmap = imageBitmap,
                                        contentDescription = "Imagen del producto",
                                        modifier = Modifier
                                            .fillMaxWidth() // La imagen ocupará todo el ancho disponible de la tarjeta
                                            .height(200.dp) // Establece una altura fija o usa un valor adaptable
                                            .graphicsLayer {
                                                alpha = 0.5f // Aplica el difuminado con alpha
                                            }
                                            .padding(bottom = 8.dp),
                                        contentScale = ContentScale.Crop // Asegura que la imagen cubra todo el área de la tarjeta
                                    )
                                }

                                Text(
                                    text = producto.nombre,
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Spacer(modifier = Modifier.height(4.dp))

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.End
                                ) {
                                    SmallFloatingActionButton(
                                        onClick = {
                                            showDeleteDialog = true
                                            productIdToDelete = producto.id
                                        },
                                        containerColor = MaterialTheme.colorScheme.error,
                                        contentColor = MaterialTheme.colorScheme.onError
                                    ) {
                                        Icon(Icons.Filled.Delete, contentDescription = "Eliminar producto")
                                    }
                                }
                            }
                        }

                    }
                }

                // Alerta de confirmación de eliminación
                Alerta(
                    dialogTitle = "Eliminar",
                    dialogText = "¿Desea continuar?",
                    onDismissRequest = {
                        println(productIdToDelete)
                        showDeleteDialog = false
                    },
                    onConfirmation = {
                        viewModel.deleteProduct(Producto(productIdToDelete, "", "", color = "#FFFFFF")) // Ajusta según sea necesario
                        showDeleteDialog = false
                    },
                    show = showDeleteDialog
                )
            }
        }
    }
}





