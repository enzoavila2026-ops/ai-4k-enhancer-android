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
import java.util.zip.ZipInputStream

class SuperResolutionInterpreter(private val context: Context) {

    private var interpreter: Interpreter? = null
    private var gpuDelegate: GpuDelegate? = null

    private val inputSize = 128        // Entrada típica para ESRGAN x4 (128x128)
    private val scaleFactor = 4        // Factor de escala 4x → salida 512x512
    private val modelFileName = "real_esrgan_x4plus.tflite"
    private val modelZipUrl = "https://qaihub-public-assets.s3.us-west-2.amazonaws.com/qai-hub-models/models/real_esrgan_x4plus/releases/v0.51.0/real_esrgan_x4plus-tflite-float.zip"

    /**
     * Inicializa el intérprete. Si el modelo no existe localmente,
     * descarga el ZIP, lo extrae y guarda el .tflite.
     */
    suspend fun initialize() {
        val modelFile = File(context.filesDir, modelFileName)
        if (!modelFile.exists()) {
            withContext(Dispatchers.IO) {
                downloadAndExtractModel(modelFile)
            }
        }
        val modelBuffer = loadModelFile(modelFile)
        val options = Interpreter.Options()
        if (CompatibilityList().isDelegateSupportedOnThisDevice) {
            gpuDelegate = GpuDelegate()
            options.addDelegate(gpuDelegate)
        }
        interpreter = Interpreter(modelBuffer, options)
    }

    /**
     * Descarga el ZIP desde modelZipUrl, extrae el primer archivo .tflite
     * y lo guarda en el archivo de destino.
     */
    private fun downloadAndExtractModel(destination: File) {
        val url = URL(modelZipUrl)
        val connection = url.openConnection() as HttpURLConnection
        connection.connectTimeout = 60000
        connection.readTimeout = 60000
        connection.requestMethod = "GET"
        connection.connect()

        if (connection.responseCode != HttpURLConnection.HTTP_OK) {
            throw Exception("Error al descargar el modelo: ${connection.responseCode}")
        }

        ZipInputStream(connection.inputStream).use { zipStream ->
            var entry = zipStream.nextEntry
            while (entry != null) {
                if (entry.name.endsWith(".tflite")) {
                    // Encontramos el archivo del modelo
                    FileOutputStream(destination).use { fos ->
                        zipStream.copyTo(fos)
                    }
                    break
                }
                entry = zipStream.nextEntry
            }
        }
        connection.disconnect()

        if (!destination.exists()) {
            throw Exception("No se encontró ningún archivo .tflite dentro del ZIP")
        }
    }

    private fun loadModelFile(file: File): MappedByteBuffer {
        val fileChannel = FileChannel.open(file.toPath())
        return fileChannel.map(FileChannel.MapMode.READ_ONLY, 0, fileChannel.size())
    }

    fun upscale(inputBitmap: Bitmap): Bitmap {
        val interpreter = this.interpreter
            ?: throw IllegalStateException("Intérprete no inicializado. Llama a initialize() primero.")
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
