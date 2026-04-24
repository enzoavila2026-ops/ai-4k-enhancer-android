package com.tuapp.enhancer4k.model

import android.content.Context
import android.graphics.Bitmap
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.tensorflow.lite.Interpreter
import org.tensorflow.lite.gpu.CompatibilityList
import org.tensorflow.lite.gpu.GpuDelegate
import java.io.File
import java.io.FileOutputStream
import java.net.HttpURLConnection
import java.net.URL
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.MappedByteBuffer
import java.nio.channels.FileChannel

class SuperResolutionInterpreter(private val context: Context) {

    private var interpreter: Interpreter? = null
    private var gpuDelegate: GpuDelegate? = null

    private val inputSize = 128
    private val scaleFactor = 4
    private val modelFileName = "real_esrgan_x4plus.tflite"
    
    // Enlace directo al archivo .tflite del modelo Real-ESRGAN x4plus
    private val modelUrl = "https://github.com/PINTO0309/PINTO_model_zoo/raw/main/133_Real-ESRGAN/real_esrgan_x4plus.tflite"

    suspend fun initialize() {
        val modelFile = File(context.filesDir, modelFileName)

        // Verificación de integridad: si el archivo existe pero es demasiado pequeño, lo considera corrupto y lo borra.
        if (modelFile.exists() && modelFile.length() < 10_000_000) {
            modelFile.delete()
        }

        if (!modelFile.exists()) {
            withContext(Dispatchers.IO) {
                downloadModel(modelUrl, modelFile)
            }
        }

        // Validación final después de la descarga
        if (!modelFile.exists() || modelFile.length() < 10_000_000) {
            throw Exception("Error: La descarga del modelo no se completó correctamente. Verifica tu conexión a internet.")
        }

        val modelBuffer = loadModelFile(modelFile)
        val options = Interpreter.Options()
        if (CompatibilityList().isDelegateSupportedOnThisDevice) {
            gpuDelegate = GpuDelegate()
            options.addDelegate(gpuDelegate)
        }
        interpreter = Interpreter(modelBuffer, options)
    }

    private suspend fun downloadModel(urlString: String, destination: File) {
        withContext(Dispatchers.IO) {
            val url = URL(urlString)
            val connection = url.openConnection() as HttpURLConnection
            connection.connectTimeout = 30000
            connection.readTimeout = 120000 // Timeout de 2 minutos para archivos grandes
            connection.requestMethod = "GET"
            connection.connect()

            if (connection.responseCode != HttpURLConnection.HTTP_OK) {
                throw Exception("Error al descargar el modelo: ${connection.responseCode}")
            }

            // Primero se descarga en un archivo temporal. Si es exitoso, se renombra al final.
            val tempFile = File(destination.parent, "temp_$modelFileName")
            connection.inputStream.use { input ->
                FileOutputStream(tempFile).use { output ->
                    input.copyTo(output)
                }
            }
            connection.disconnect()

            // Solo conservamos el archivo si tiene un tamaño razonable
            if (tempFile.exists() && tempFile.length() > 10_000_000) {
                tempFile.renameTo(destination)
            } else {
                tempFile.delete()
                throw Exception("La descarga no se completó (archivo demasiado pequeño). Intenta de nuevo.")
            }
        }
    }

    private fun loadModelFile(file: File): MappedByteBuffer {
        val fileChannel = FileChannel.open(file.toPath())
        return fileChannel.map(FileChannel.MapMode.READ_ONLY, 0, fileChannel.size())
    }

    fun upscale(inputBitmap: Bitmap): Bitmap {
        val interpreter = this.interpreter
            ?: throw IllegalStateException("Intérprete no inicializado.")
        val resizedInput = Bitmap.createScaledBitmap(inputBitmap, inputSize, inputSize, true)
        val inputBuffer = bitmapToFloatBuffer(resizedInput)
        val outputWidth = inputSize * scaleFactor
        val outputHeight = inputSize * scaleFactor
        val outputBuffer = ByteBuffer.allocateDirect(outputWidth * outputHeight * 3 * 4)
            .order(ByteOrder.nativeOrder())
        interpreter.run(inputBuffer, outputBuffer)
        return floatBufferToBitmap(outputBuffer, outputWidth, outputHeight)
    }

    fun close() {
        interpreter?.close()
        gpuDelegate?.close()
    }

    private fun bitmapToFloatBuffer(bitmap: Bitmap): ByteBuffer {
        val pixels = IntArray(inputSize * inputSize)
        bitmap.getPixels(pixels, 0, inputSize, 0, 0, inputSize, inputSize)
        val buffer = ByteBuffer.allocateDirect(inputSize * inputSize * 3 * 4)
            .order(ByteOrder.nativeOrder())
        for (pixel in pixels) {
            val r = ((pixel shr 16) and 0xFF) / 255.0f
            val g = ((pixel shr 8) and 0xFF) / 255.0f
            val b = (pixel and 0xFF) / 255.0f
            buffer.putFloat(r)
            buffer.putFloat(g)
            buffer.putFloat(b)
        }
        buffer.rewind()
        return buffer
    }

    private fun floatBufferToBitmap(buffer: ByteBuffer, width: Int, height: Int): Bitmap {
        buffer.rewind()
        val pixels = IntArray(width * height)
        for (i in pixels.indices) {
            val r = (buffer.float.coerceIn(0f, 1f) * 255).toInt()
            val g = (buffer.float.coerceIn(0f, 1f) * 255).toInt()
            val b = (buffer.float.coerceIn(0f, 1f) * 255).toInt()
            pixels[i] = (0xFF shl 24) or (r shl 16) or (g shl 8) or b
        }
        return Bitmap.createBitmap(pixels, width, height, Bitmap.Config.ARGB_8888)
    }
}
