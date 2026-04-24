package com.tuapp.enhancer4k.ui

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext

@Composable
fun ImagePickerDialog(
    show: Boolean,
    onDismiss: () -> Unit,
    onImageSelected: (Bitmap) -> Unit
) {
    val context = LocalContext.current

    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            val bitmap = uriToBitmap(context, it)
            if (bitmap != null) {
                onImageSelected(bitmap)
            }
        }
    }

    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicturePreview()
    ) { bitmap: Bitmap? ->
        bitmap?.let { onImageSelected(it) }
    }

    if (show) {
        AlertDialog(
            onDismissRequest = onDismiss,
            title = { Text("Seleccionar fuente") },
            text = { Text("Elige una imagen de tu galería o toma una foto.") },
            confirmButton = {
                TextButton(onClick = {
                    onDismiss()
                    galleryLauncher.launch("image/*")
                }) {
                    Text("Galería")
                }
            },
            dismissButton = {
                TextButton(onClick = {
                    onDismiss()
                    cameraLauncher.launch(null)
                }) {
                    Text("Cámara")
                }
            }
        )
    }
}

private fun uriToBitmap(context: Context, uri: Uri): Bitmap? {
    return try {
        val inputStream = context.contentResolver.openInputStream(uri)
        BitmapFactory.decodeStream(inputStream)
    } catch (e: Exception) {
        null
    }
}
