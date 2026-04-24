package com.tuapp.enhancer4k.model

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import org.tensorflow.lite.Interpreter
import org.tensorflow.lite.gpu.CompatibilityList
import org.tensorflow.lite.gpu.GpuDelegate
import org.tensorflow.lite.nnapi.NnApiDelegate
import java.io.FileInputStream
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.MappedByteBuffer
import java.nio.channels.FileChannel

class SuperResolutionInterpreter(private val context: Context) {

    private var interpreter: Interpreter? = null
    private var gpuDelegate: GpuDelegate? = null
    private var nnApiDelegate: NnApiDelegate? = null

    // Ajusta estos valores según el modelo TFLite que uses
    private val inputSize = 512    // Entrada 512x512
    private val scaleFactor = 4    // Escala x4 → salida 2048x2048

    /**
     * Inicializa el intérprete TFLite con aceleración por hardware.
     * @param useGPU true para intentar usar GPU, false para CPU/NNAPI.
     */
    fun initialize(useGPU: Boolean = true) {
        val modelBuffer = loadModelFile("realesrgan_x4plus.tflite")
        val options = Interpreter.Options()

        if (useGPU && CompatibilityList().isDelegateSupportedOnThisDevice) {
            gpuDelegate = GpuDelegate()
            options.addDelegate(gpuDelegate)
        } else {
            // Fallback a NNAPI o CPU pura
            nnApiDelegate = NnApiDelegate()
            options.addDelegate(nnApiDelegate)
        }

        interpreter = Interpreter(modelBuffer, options)
    }

    /**
     * Escala un Bitmap de entrada al tamaño [inputSize]x[inputSize],
     * ejecuta el modelo y devuelve el Bitmap mejorado a tamaño [inputSize*scaleFactor]x[inputSize*scaleFactor].
     * Para imágenes más grandes debe usarse tiling (procesamiento por parches).
     */
    fun upscale(inputBitmap: Bitmap): Bitmap {
        val interpreter = this.interpreter
            ?: throw IllegalStateException("Interpreter no inicializado. Llama a initialize() primero.")

        // 1. Redimensionar entrada al tamaño fijo del modelo
        val resizedInput = Bitmap.createScaledBitmap(inputBitmap, inputSize, inputSize, true)

        // 2. Convertir Bitmap a ByteBuffer normalizado [0, 1] en formato RGB
        val inputBuffer = bitmapToFloatBuffer(resizedInput)

        // 3. Crear buffer de salida
        val outputWidth = inputSize * scaleFactor
        val outputHeight = inputSize * scaleFactor
        val outputBuffer = ByteBuffer.allocateDirect(outputWidth * outputHeight * 3 * 4)
            .order(ByteOrder.nativeOrder())

        // 4. Ejecutar inferencia
        interpreter.run(inputBuffer, outputBuffer)

        // 5. Convertir ByteBuffer de salida a Bitmap RGB
        return floatBufferToBitmap(outputBuffer, outputWidth, outputHeight)
    }

    /**
     * Libera recursos del intérprete y delegados.
     */
    fun close() {
        interpreter?.close()
        gpuDelegate?.close()
        nnApiDelegate?.close()
    }

    // ------------------- Métodos privados -------------------

    /**
     * Carga el archivo .tflite desde assets/ y lo retorna como MappedByteBuffer.
     */
    private fun loadModelFile(filename: String): MappedByteBuffer {
        val fileDescriptor = context.assets.openFd(filename)
        val inputStream = FileInputStream(fileDescriptor.fileDescriptor)
        val fileChannel = inputStream.channel
        val startOffset = fileDescriptor.startOffset
        val declaredLength = fileDescriptor.declaredLength
        return fileChannel.map(FileChannel.MapMode.READ_ONLY, startOffset, declaredLength)
    }

    /**
     * Convierte un Bitmap RGB a ByteBuffer con floats normalizados [0,1] en orden HWC.
     */
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

    /**
     * Convierte un ByteBuffer de floats [0,1] (HWC, RGB) a Bitmap.
     */
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
