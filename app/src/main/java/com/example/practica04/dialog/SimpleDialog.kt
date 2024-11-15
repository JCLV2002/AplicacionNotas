package com.example.practica04.dialog

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import kotlinx.coroutines.delay

@Composable
fun Alerta(
    onDismissRequest: () -> Unit,
    onConfirmation: () -> Unit,
    dialogTitle: String,
    dialogText: String,
    show: Boolean
) {
    if (show) {
        AlertDialog(
            title = {
                Text(text = dialogTitle)
            },
            text = {
                Text(text = dialogText)
            },
            onDismissRequest = {
                onDismissRequest()
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        onConfirmation()
                    }
                ) {
                    Text("Confirmar")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        onDismissRequest()
                    }
                ) {
                    Text("Cancelar")
                }
            }
        )
    }
}

@Composable
fun AlertaProductoAgregado(
    show: Boolean,
    onDismissRequest: () -> Unit,
    durationMillis: Int = 5000
) {
    if (show){
        LaunchedEffect(Unit) {
            delay(durationMillis.toLong())
            onDismissRequest()
        }
        AlertDialog(
            onDismissRequest = { onDismissRequest() },
            title = { Text("Producto agregado") },
            text = { Text("El producto se ha agregado correctamente") },
            confirmButton = {})
    }
}


@Composable
fun AlertaProductoActualizado(
    show: Boolean,
    onDismissRequest: () -> Unit,
    durationMillis: Int = 5000
) {
    if (show){
        LaunchedEffect(Unit) {
            delay(durationMillis.toLong())
            onDismissRequest()
        }
        AlertDialog(
            onDismissRequest = { onDismissRequest() },
            title = { Text("Producto actualizado") },
            text = { Text("El producto se ha actualizado correctamente") },
            confirmButton = {})
    }
}
