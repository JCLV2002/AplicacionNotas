package com.example.practica04.views

import android.app.Activity
import android.content.ContentResolver
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FabPosition
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Popup
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.practica04.R
import com.example.practica04.dialog.Alerta
import com.example.practica04.dialog.AlertaProductoActualizado
import com.example.practica04.dialog.AlertaProductoAgregado
import com.example.practica04.model.Producto
import com.example.practica04.navigation.FormularioProductos
import com.example.practica04.navigation.Home
import com.example.practica04.navigation.ListaProductos
import com.example.practica04.viewmodels.ProductoViewModel
import java.io.InputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import androidx.compose.ui.graphics.painter.BitmapPainter
import com.google.android.gms.cast.framework.media.ImagePicker
import android.graphics.Bitmap
import android.util.Base64
import android.util.Log
import android.util.Size
import android.widget.Toast
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.LocalLifecycleOwner
import java.io.ByteArrayOutputStream
import java.io.File
import android.Manifest
import android.os.Environment
import java.io.FileOutputStream


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FormularioProductosView(navController: NavController, viewModel: ProductoViewModel, modifier: Modifier = Modifier) {
    var name by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    val context = LocalContext.current

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
                        text = "Nueva Nota",
                        style = MaterialTheme.typography.headlineSmall.copy(
                            fontWeight = FontWeight.Bold
                        )
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.Filled.ArrowBack,
                            contentDescription = "Regresar",
                            tint = MaterialTheme.colorScheme.onPrimaryContainer
                        )
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
        },
    ) { innerPadding ->
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(MaterialTheme.colorScheme.background)
        ) {
            Formulario(viewModel, navController)
        }
    }
}



@Composable
fun CampoTexto(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    textArea: Boolean = false,
    icono: Int,
    modifier: Modifier = Modifier
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(text = label) },
        keyboardOptions = keyboardOptions,
        modifier = modifier
            .fillMaxWidth()
            .height(if (textArea) 200.dp else 80.dp), // Aumenta la altura según la necesidad
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = MaterialTheme.colorScheme.primary,
            unfocusedBorderColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f),
            focusedLabelColor = MaterialTheme.colorScheme.primary,
            unfocusedLabelColor = MaterialTheme.colorScheme.onSurface,
            unfocusedContainerColor = Color.Transparent,
            focusedContainerColor = Color.Transparent
        ),
        textStyle = TextStyle(
            color = MaterialTheme.colorScheme.onSurface,
            fontSize = 18.sp // Ajusta el tamaño de la fuente aquí
        )
    )
}

@Composable
fun campodescripcion(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    textArea: Boolean = false,
    icono: Int,
    modifier: Modifier = Modifier) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(text = label) },
        keyboardOptions = keyboardOptions,
        modifier = modifier
            .fillMaxWidth()
            .height(if (textArea) 200.dp else 300.dp), // Aumenta la altura según la necesidad
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = MaterialTheme.colorScheme.primary,
            unfocusedBorderColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f),
            focusedLabelColor = MaterialTheme.colorScheme.primary,
            unfocusedLabelColor = MaterialTheme.colorScheme.onSurface,
            unfocusedContainerColor = Color.Transparent,
            focusedContainerColor = Color.Transparent
        ),
        textStyle = TextStyle(
            color = MaterialTheme.colorScheme.onSurface,
            fontSize = 18.sp // Ajusta el tamaño de la fuente aquí
        )
    )

}

@Composable
fun ImagePickers(onImageSelected: (ImageBitmap) -> Unit) {
    val context = LocalContext.current
    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }
    var selectedImageBitmap by remember { mutableStateOf<ImageBitmap?>(null) }
    var showErrorDialog by remember { mutableStateOf(false) }

    // Define el tamaño máximo en bytes (por ejemplo, 2MB)
    val maxSizeInBytes = 2 * 1024 * 1024

    // ActivityResultLauncher para seleccionar una imagen
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val uri = result.data?.data
            uri?.let {
                val inputStream: InputStream? = context.contentResolver.openInputStream(it)
                val bitmap = BitmapFactory.decodeStream(inputStream)

                // Verifica el tamaño de la imagen
                val outputStream = ByteArrayOutputStream()
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
                val imageSizeInBytes = outputStream.size()

                if (imageSizeInBytes > maxSizeInBytes) {
                    showErrorDialog = true // Muestra el diálogo de error si la imagen es muy grande
                } else {
                    selectedImageUri = it
                    selectedImageBitmap = bitmap.asImageBitmap()
                    onImageSelected(selectedImageBitmap!!) // Pasa la imagen seleccionada
                }
            }
        }
    }

    Box(
        modifier = Modifier
            .size(150.dp)
            .clickable {
                val intent = Intent(Intent.ACTION_PICK).apply {
                    type = "image/*"
                }
                launcher.launch(intent)
            },
        contentAlignment = Alignment.Center
    ) {
        if (selectedImageBitmap != null) {
            Image(
                bitmap = selectedImageBitmap!!,
                contentDescription = "Imagen seleccionada",
                modifier = Modifier
                    .size(150.dp)
                    .alpha(0.5f)
            )
        } else {
            Text("Selecciona una imagen")
        }
    }

    // Diálogo de error
    if (showErrorDialog) {
        AlertDialog(
            onDismissRequest = { showErrorDialog = false },
            confirmButton = {
                TextButton(onClick = { showErrorDialog = false }) {
                    Text("Aceptar")
                }
            },
            title = { Text("Error") },
            text = { Text("La imagen seleccionada es demasiado grande. Selecciona una imagen de menor tamaño.") }
        )
    }
}


// Función para reducir la calidad de una imagen a un archivo en el almacenamiento interno
fun compressImage(context: Context, imageUri: Uri, quality: Int = 50): Bitmap {
    val resolver: ContentResolver = context.contentResolver
    val inputStream = resolver.openInputStream(imageUri)
    val originalBitmap = BitmapFactory.decodeStream(inputStream)

    // Crear un ByteArrayOutputStream para comprimir la imagen
    val outputStream = ByteArrayOutputStream()
    originalBitmap.compress(Bitmap.CompressFormat.JPEG, quality, outputStream) // Ajusta la calidad (de 0 a 100)

    // Convierte el ByteArrayOutputStream a un Bitmap comprimido
    val compressedByteArray = outputStream.toByteArray()
    return BitmapFactory.decodeByteArray(compressedByteArray, 0, compressedByteArray.size)
}


@Composable
fun ColorPickers(selectedColor: Color, onColorSelected: (Color) -> Unit) {
    // Define el color original
    val defaultColor = MaterialTheme.colorScheme.onSurface // Este es el color que actúa como 'por defecto'

    // Carga los colores desde colors.xml
    val colors = listOf(
        defaultColor, // Color original por defecto
        colorResource(id = R.color.pastel),
        colorResource(id = R.color.GRIS_PASTEL),
        colorResource(id = R.color.NARANJA_OSCURO),
        colorResource(id = R.color.PIEL_VERDOSA),
        colorResource(id = R.color.VERDE_PASTE),
        colorResource(id = R.color.azul_pastel),
        colorResource(id = R.color.azul_pastelc)
    )

    Row(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier.padding(vertical = 8.dp)
    ) {
        colors.forEach { color ->
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .background(color, shape = CircleShape)
                    .border(
                        width = if (color == selectedColor) 2.dp else 0.dp,
                        color = Color.Black,
                        shape = CircleShape
                    )
                    .clickable {
                        // Si seleccionas el color original, lo restableces a "por defecto"
                        onColorSelected(if (color == defaultColor) Color.Transparent else color)
                    }
            )
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Formulario(viewModel: ProductoViewModel, navController: NavController, modifier: Modifier = Modifier) {
    var name by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var selectedColor by remember { mutableStateOf(Color.White) }
    var backgroundImage by remember { mutableStateOf<ImageBitmap?>(null) }
    var useImage by remember { mutableStateOf(false) } // Nuevo estado para seleccionar entre color o imagen
    var errorMsg by remember { mutableStateOf("") }
    var showErrorDialog by remember { mutableStateOf(false) }
    var showImagePickerDialog by remember { mutableStateOf(false) } // Estado para mostrar el diálogo de selección de imagen

    Box(
        modifier = modifier
            .fillMaxSize()
    ) {
        // Fondo de imagen o color
        if (useImage && backgroundImage != null) {
            Image(
                painter = BitmapPainter(backgroundImage!!),
                contentDescription = null,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop // Ajusta cómo se muestra la imagen
            )
        } else {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(selectedColor)
            )
        }

        // Contenido sobre el fondo
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
                .align(Alignment.Center) // Asegura que el contenido esté centrado en la pantalla
            ,
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            CampoTexto(label = "Titulo", value = name, icono = R.color.pastel, onValueChange = { name = it })
            campodescripcion(label = "Contenido", value = description, icono = R.color.pastel, onValueChange = { description = it })

            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("Cargar Imagen")
                Switch(
                    checked = useImage,
                    onCheckedChange = { useImage = it },
                    modifier = Modifier.padding(start = 8.dp)
                )
            }

            if (useImage) {
                Text("Selecciona una imagen del dispositivo:")
                Button(
                    onClick = { showImagePickerDialog = true },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Cargar Imagen")
                }
            } else {
                Text("Selecciona un color:")
                ColorPickers(selectedColor = selectedColor) { color ->
                    selectedColor = color
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            // Botón para crear la nota
            Button(
                onClick = {
                    if (name.isBlank() || description.isBlank()) {
                        errorMsg = "Favor poner una descripción"
                        showErrorDialog = true
                    } else {
                        val colorHex = if (!useImage) String.format("#%06X", 0xFFFFFF and selectedColor.toArgb()) else null
                        val imageBase64 = if (useImage && backgroundImage != null) imageBitmapToBase64(backgroundImage!!) else null

                        viewModel.addProduct(
                            Producto(
                                nombre = name,
                                descripcion = description,
                                color = colorHex ?: "#FFFFFF",
                                imagen = imageBase64 ?: "" // Guarda la imagen como Base64 o una cadena vacía si no hay imagen
                            )
                        )
                        navController.popBackStack()
                    }
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                ),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(text = "Crear nota")
            }
        }
    }

    // Diálogo para seleccionar la imagen
    if (showImagePickerDialog) {
        AlertDialog(
            onDismissRequest = { showImagePickerDialog = false },
            title = { Text("Selecciona una imagen, No debe pesar mas de 3 MB") },
            text = {
                ImagePickers(onImageSelected = { bitmap ->
                    backgroundImage = bitmap // Establece la imagen seleccionada
                    showImagePickerDialog = false // Cierra el diálogo después de seleccionar la imagen
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



fun imageBitmapToBase64(imageBitmap: ImageBitmap): String {
    val bitmap = imageBitmap.asAndroidBitmap() // Convierte ImageBitmap a Bitmap
    val outputStream = ByteArrayOutputStream()
    bitmap.compress(Bitmap.CompressFormat.PNG, 10, outputStream) // Comprime a PNG
    val byteArray = outputStream.toByteArray()
    return Base64.encodeToString(byteArray, Base64.DEFAULT) // Codifica a Base64
}