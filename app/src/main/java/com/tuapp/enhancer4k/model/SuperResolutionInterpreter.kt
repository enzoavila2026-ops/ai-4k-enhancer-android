package com.tuapp.enhancer4k.model

import android.content.Context
import android.graphics.Bitmap
import org.tensorflow.lite.Interpreter
import org.tensorflow.lite.gpu.CompatibilityList
import org.tensorflow.lite.gpu.GpuDelegate
import java.io.FileInputStream
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.MappedByteBuffer
import java.nio.channels.FileChannel

class SuperResolutionInterpreter(private val context: Context) {

    private var interpreter: Interpreter? = null
    private var gpuDelegate: GpuDelegate? = null

    private val inputSize = 128
    private val scaleFactor = 4

    fun initialize() {
        val modelBuffer = loadModelFromAssets("Real-ESRGAN-x4plus.tflite")
        val options = Interpreter.Options()
        if (CompatibilityList().isDelegateSupportedOnThisDevice) {
            gpuDelegate = GpuDelegate()
            options.addDelegate(gpuDelegate)
        }
        interpreter = Interpreter(modelBuffer, options)
    }

    private fun loadModelFromAssets(filename: String): MappedByteBuffer {
        val fileDescriptor = context.assets.openFd(filename)
        val inputStream = FileInputStream(fileDescriptor.fileDescriptor)
        val fileChannel = inputStream.channel
        return fileChannel.map(
            FileChannel.MapMode.READ_ONLY,
            fileDescriptor.startOffset,
            fileDescriptor.declaredLength
        )
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
