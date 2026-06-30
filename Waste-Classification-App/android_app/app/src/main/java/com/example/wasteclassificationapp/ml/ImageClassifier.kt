package com.example.wasteclassificationapp.ml

import android.content.Context
import android.graphics.Bitmap
import com.example.wasteclassificationapp.model.WasteCategory
import org.tensorflow.lite.Interpreter
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.channels.FileChannel

class ImageClassifier(
    private val context: Context,
    private val modelFileName: String
) {
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
        /*
         * 先做中心裁剪：
         * CameraX 拍出来通常是 4:3 或 16:9，
         * 如果直接压缩成 224×224，物体会被拉伸，而且背景过多。
         * 中心裁剪可以让模型更接近训练时看到的图片形式。
         */
        val croppedBitmap = centerCropBitmap(bitmap)

        val resizedBitmap = Bitmap.createScaledBitmap(
            croppedBitmap,
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
             * 仍然保持 0~255 float32 输入。
             * 不要除以 255。
             * 不要写 r / 127.5f - 1.0f。
             */
            inputBuffer.putFloat(r.toFloat())
            inputBuffer.putFloat(g.toFloat())
            inputBuffer.putFloat(b.toFloat())
        }

        inputBuffer.rewind()
        return inputBuffer
    }

    private fun centerCropBitmap(bitmap: Bitmap): Bitmap {
        val width = bitmap.width
        val height = bitmap.height

        val cropSize = minOf(width, height)

        val xOffset = (width - cropSize) / 2
        val yOffset = (height - cropSize) / 2

        return Bitmap.createBitmap(
            bitmap,
            xOffset,
            yOffset,
            cropSize,
            cropSize
        )
    }

    fun close() {
        interpreter.close()
    }
}