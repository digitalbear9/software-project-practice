package com.example.wasteclassificationapp.ml

import android.content.Context
import android.graphics.Bitmap
import com.example.wasteclassificationapp.model.WasteCategory
import org.tensorflow.lite.Interpreter
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.channels.FileChannel

class ImageClassifier(
    private val context: Context
) {
    private val modelFileName = "waste_classification_mobilenetv2_v1_float32.tflite"
    private val labelsFileName = "labels.txt"

    private val imageSize = 224
    private val numChannels = 3
    private val numClasses = 7

    private val interpreter: Interpreter
    private val labels: List<String>

    init {
        val modelBuffer = loadModelFile()
        interpreter = Interpreter(modelBuffer)
        labels = loadLabels()
    }

    fun classify(bitmap: Bitmap): RecognitionResult {
        val inputBuffer = preprocessBitmap(bitmap)

        val output = Array(1) { FloatArray(numClasses) }

        interpreter.run(inputBuffer, output)

        val scores = output[0]
        val maxIndex = scores.indices.maxBy { scores[it] }
        val confidence = scores[maxIndex]
        val label = labels[maxIndex]

        val info = WasteCategory.getInfo(label)

        return RecognitionResult(
            label = label,
            labelCn = info.labelCn,
            wasteCategory = info.wasteCategory,
            suggestion = info.suggestion,
            confidence = confidence
        )
    }

    private fun loadModelFile(): ByteBuffer {
        val assetFileDescriptor = context.assets.openFd(modelFileName)

        assetFileDescriptor.use { afd ->
            val inputStream = afd.createInputStream()
            val fileChannel = inputStream.channel

            return fileChannel.map(
                FileChannel.MapMode.READ_ONLY,
                afd.startOffset,
                afd.declaredLength
            )
        }
    }

    private fun loadLabels(): List<String> {
        return context.assets.open(labelsFileName).bufferedReader().useLines { lines ->
            lines.map { it.trim() }
                .filter { it.isNotEmpty() }
                .toList()
        }
    }

    private fun preprocessBitmap(bitmap: Bitmap): ByteBuffer {
        val resizedBitmap = Bitmap.createScaledBitmap(
            bitmap,
            imageSize,
            imageSize,
            true
        )

        val inputBuffer = ByteBuffer.allocateDirect(
            4 * imageSize * imageSize * numChannels
        )
        inputBuffer.order(ByteOrder.nativeOrder())

        val pixels = IntArray(imageSize * imageSize)

        resizedBitmap.getPixels(
            pixels,
            0,
            imageSize,
            0,
            0,
            imageSize,
            imageSize
        )

        for (pixel in pixels) {
            val r = (pixel shr 16) and 0xFF
            val g = (pixel shr 8) and 0xFF
            val b = pixel and 0xFF

            /*
             * 注意：
             * 你的训练模型内部已经包含 Rescaling(1/127.5, offset=-1)
             * 因此 Android 端输入 0~255 的 float32 即可。
             * 不要除以 255，也不要写 r / 127.5f - 1.0f。
             */
            inputBuffer.putFloat(r.toFloat())
            inputBuffer.putFloat(g.toFloat())
            inputBuffer.putFloat(b.toFloat())
        }

        inputBuffer.rewind()
        return inputBuffer
    }

    fun close() {
        interpreter.close()
    }
}