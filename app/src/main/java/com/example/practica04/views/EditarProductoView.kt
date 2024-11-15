package com.example.practica04.views

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Environment
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.practica04.R
import com.example.practica04.dialog.Alerta
import com.example.practica04.dialog.AlertaProductoActualizado
import com.example.practica04.dialog.AlertaProductoAgregado
import com.example.practica04.model.Producto
import com.example.practica04.navigation.Home
import com.example.practica04.navigation.ListaProductos
import com.example.practica04.viewmodels.ProductoViewModel
import android.util.Base64
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.Switch
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
//import com.example.practica04.Manifest
import java.io.File
import java.io.FileOutputStream
import android.Manifest
import android.app.Activity
import androidx.activity.result.ActivityResultLauncher
import androidx.compose.material3.TextButton
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asAndroidBitmap
import java.io.ByteArrayOutputStream
import java.io.InputStream

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditarProductoView(
    productId: Int,
    navController: NavController,
    viewModel: ProductoViewModel,
    modifier: Modifier = Modifier
) {
    val producto = viewModel.getProductById(productId)
    val initialColor = producto?.color?.let { Color(android.graphics.Color.parseColor(it)) } ?: Color.White
    var backgroundColor by remember { mutableStateOf(initialColor) }
    var backgroundImage by remember { mutableStateOf(producto?.imagen ?: "") }
    var useImageBackground by remember { mutableStateOf(backgroundImage.isNotBlank()) }
    val context = LocalContext.current

    // Crear el launcher para seleccionar imagen de la galería
    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent(),
        onResult = { uri: Uri? ->
            uri?.let {
                val inputStream = context.contentResolver.openInputStream(it)
                val selectedBitmap = BitmapFactory.decodeStream(inputStream)
                inputStream?.close()

                // Convertir la imagen a Base64
                val outputStream = ByteArrayOutputStream()
                selectedBitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
                backgroundImage = Base64.encodeToString(outputStream.toByteArray(), Base64.DEFAULT)
                useImageBackground = true
            }
        }
    )

    // Crear el launcher para la cámara
    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicturePreview(),
        onResult = { bitmap ->
            if (bitmap != null) {
                try {
                    // Obtener el directorio de imágenes públicas
                    val picturesDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
                    val galleryDir = File(picturesDir, "MisFotos")

                    // Crear el directorio si no existe
                    if (!galleryDir.exists()) {
                        val created = galleryDir.mkdirs()
                        if (!created) {
                            Toast.makeText(context, "Error al crear el directorio", Toast.LENGTH_SHORT).show()
                            return@rememberLauncherForActivityResult
                        }
                    }

                    // Crear un archivo único para la imagen
                    val file = File(galleryDir, "captured_image_${System.currentTimeMillis()}.jpg")
                    var quality = 100 // Comenzar con la máxima calidad
                    var fileSize: Long

                    do {
                        // Comprimir y guardar la imagen
                        FileOutputStream(file).use { outputStream ->
                            bitmap.compress(Bitmap.CompressFormat.JPEG, quality, outputStream)
                        }

                        // Verificar el tamaño del archivo
                        fileSize = file.length()
                        quality -= 10 // Reducir calidad gradualmente

                    } while (fileSize > 5 * 1024 * 1024 && quality > 50) // Máx. 5 MB, min. calidad 50

                    // Validar si la imagen cumple con el tamaño esperado
                    if (fileSize <= 3 * 1024 * 1024) {
                        // Notificar a la galería sobre la nueva imagen
                        val mediaScanIntent = Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE).apply {
                            data = Uri.fromFile(file)
                        }
                        context.sendBroadcast(mediaScanIntent)

                        Toast.makeText(context, "Imagen capturada y guardada en la galería", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(context, "No se pudo reducir la imagen a menos de 3 MB", Toast.LENGTH_SHORT).show()
                    }
                } catch (e: Exception) {
                    Toast.makeText(context, "Error al guardar la imagen: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(context, "Captura cancelada", Toast.LENGTH_SHORT).show()
            }
        }
    )

    // Crear launcher para la solicitud de permiso
    val permisoCamaraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            cameraLauncher.launch(null) // Si el permiso es otorgado, abre la cámara
        } else {
            Toast.makeText(context, "Permiso de cámara denegado", Toast.LENGTH_SHORT).show()
        }
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                ),
                title = {
                    Text(
                        text = "Editar",
                        style = MaterialTheme.typography.headlineSmall.copy(
                            fontWeight = FontWeight.Bold
                        )
                    )
                },
                navigationIcon = {
                    IconButton(onClick = {
                        navController.popBackStack()
                    }) {
                        Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Volver")
                    }
                },
                actions = {
                    IconButton(onClick = {
                        // Solicitar permiso antes de abrir la cámara
                        permisoCamaraLauncher.launch(Manifest.permission.CAMERA)
                    }) {
                        Icon(
                            imageVector = Icons.Filled.MoreVert,
                            contentDescription = "Más opciones",
                            tint = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }
                }
            )
        }
    ) { innerPadding ->
        Box(
            modifier = modifier
                .fillMaxSize()
        ) {
            // Mostrar la imagen solo si se selecciona usar imagen de fondo y no está vacía
            if (useImageBackground && backgroundImage.isNotBlank()) {
                val imageBytes = Base64.decode(backgroundImage, Base64.DEFAULT)
                val imageBitmap = imageBytes.inputStream().use { BitmapFactory.decodeStream(it) }.asImageBitmap()

                Image(
                    bitmap = imageBitmap,
                    contentDescription = "Imagen del fondo",
                    modifier = Modifier
                        .fillMaxSize()
                        .align(Alignment.Center)
                        .alpha(0.5f),
                    contentScale = ContentScale.Crop
                )
            } else {
                // Mostrar solo el color de fondo
                Box(modifier = Modifier.fillMaxSize().background(backgroundColor))
            }

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Top,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(9.dp)
            ) {
                FormularioEditar(
                    producto = producto,
                    viewModel = viewModel,
                    navController = navController,
                    selectedColor = backgroundColor,
                    onColorChanged = { newColor ->
                        backgroundColor = newColor
                    },
                    backgroundImage = backgroundImage,
                    onImageSelected = { newImage ->
                        backgroundImage = newImage
                    },
                    useImageBackground = useImageBackground,
                    onBackgroundOptionChanged = { useImage ->
                        useImageBackground = useImage
                    },
                    galleryLauncher = galleryLauncher
                )

                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FormularioEditar(
    producto: Producto?,
    viewModel: ProductoViewModel,
    navController: NavController,
    selectedColor: Color,
    onColorChanged: (Color) -> Unit,
    backgroundImage: String,
    onImageSelected: (String) -> Unit,
    useImageBackground: Boolean,
    onBackgroundOptionChanged: (Boolean) -> Unit,
    galleryLauncher: ActivityResultLauncher<String>,
    modifier: Modifier = Modifier
) {
    var name by remember { mutableStateOf(producto?.nombre ?: "") }
    var description by remember { mutableStateOf(producto?.descripcion ?: "") }
    var selectedColorInternal by remember { mutableStateOf(selectedColor) }
    var showErrorDialog by remember { mutableStateOf(false) }
    var showImagePickerDialog by remember { mutableStateOf(false) }
    var updatedBackgroundImage by remember { mutableStateOf(backgroundImage) }

    LaunchedEffect(selectedColor) {
        selectedColorInternal = selectedColor
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(9.dp)
            .background(selectedColorInternal.copy(alpha = 0.6f)),
        verticalArrangement = Arrangement.spacedBy(9.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        CampoTexto(label = "Título", value = name, icono = R.color.pastel, onValueChange = { name = it })
        campodescripcion(label = "Contenido", value = description, icono = R.color.pastel, onValueChange = { description = it })

        Row(verticalAlignment = Alignment.CenterVertically) {
            Text("Editar Imagen", style = MaterialTheme.typography.bodyMedium)
            Switch(
                checked = useImageBackground,
                onCheckedChange = { onBackgroundOptionChanged(it) }
            )
        }

        // Botón para seleccionar la imagen, solo si el Switch está activado
        if (useImageBackground) {
            Button(
                onClick = { showImagePickerDialog = true }, // Abre el diálogo de selección de imagen
                modifier = Modifier.padding(vertical = 16.dp)
            ) {
                Text("Actualizar Imagen")
            }
        } else {
            // Si no se usa fondo de imagen, permite elegir el color
            Text("Color de la nota", style = MaterialTheme.typography.bodyMedium)
            ColorPickers(selectedColor = selectedColorInternal) { newColor ->
                selectedColorInternal = newColor
                onColorChanged(newColor)
            }
        }

        // Botón para actualizar el producto
        Button(
            onClick = {
                if (producto != null) {
                    try {
                        val colorHex = String.format("#%06X", 0xFFFFFF and selectedColorInternal.toArgb())
                        val updatedImage = if (!useImageBackground) "" else updatedBackgroundImage
                        viewModel.updateProduct(
                            Producto(id = producto.id, nombre = name, descripcion = description, color = colorHex, imagen = updatedImage)
                        )
                        navController.popBackStack()
                    } catch (e: Exception) {
                        showErrorDialog = true
                    }
                } else {
                    showErrorDialog = true
                }
            },
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Actualizar nota")
        }
    }

    // Diálogo de error si es necesario
    if (showErrorDialog) {
        Alerta(
            dialogTitle = "Error",
            dialogText = "Ocurrió un error al actualizar el producto.",
            onDismissRequest = { showErrorDialog = false },
            onConfirmation = { showErrorDialog = false },
            show = showErrorDialog
        )
    }

    // Diálogo de selección de imagen
    if (showImagePickerDialog) {
        AlertDialog(
            onDismissRequest = { showImagePickerDialog = false },
            title = { Text("Selecciona una imagen, No debe pesar más de 3 MB") },
            text = {
                ImagePickers(onImageSelected = { imageBitmap ->
                    val outputStream = ByteArrayOutputStream()
                    imageBitmap.asAndroidBitmap().compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
                    val encodedImage = Base64.encodeToString(outputStream.toByteArray(), Base64.DEFAULT)
                    updatedBackgroundImage = encodedImage // Establece la imagen seleccionada
                    showImagePickerDialog = false // Cierra el diálogo después de seleccionar la imagen
                    onImageSelected(encodedImage) // Informa al padre sobre la imagen seleccionada
                })
            },
            confirmButton = {
                Button(
                    onClick = { showImagePickerDialog = false },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                ) {
                    Text("Cancelar")
                }
            }
        )
    }
}
