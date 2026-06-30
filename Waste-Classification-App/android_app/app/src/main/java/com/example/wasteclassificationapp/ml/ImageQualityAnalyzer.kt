package com.example.wasteclassificationapp.ml

import android.graphics.Bitmap
import kotlin.math.sqrt

object ImageQualityAnalyzer {

    private const val ANALYZE_SIZE = 128

    private const val DARK_THRESHOLD = 55.0f
    private const val BRIGHT_THRESHOLD = 220.0f
    private const val LOW_CONTRAST_THRESHOLD = 25.0f
    private const val BLUR_THRESHOLD = 80.0f

    fun analyze(bitmap: Bitmap): ImageQualityResult {
        val resizedBitmap = Bitmap.createScaledBitmap(
            bitmap,
            ANALYZE_SIZE,
            ANALYZE_SIZE,
            true
        )

        val width = resizedBitmap.width
        val height = resizedBitmap.height

        val pixels = IntArray(width * height)

        resizedBitmap.getPixels(
            pixels,
            0,
            width,
            0,
            0,
            width,
            height
        )

        val grayValues = FloatArray(width * height)

        var brightnessSum = 0.0f

        for (i in pixels.indices) {
            val pixel = pixels[i]

            val r = (pixel shr 16) and 0xFF
            val g = (pixel shr 8) and 0xFF
            val b = pixel and 0xFF

            val gray = 0.299f * r + 0.587f * g + 0.114f * b

            grayValues[i] = gray
            brightnessSum += gray
        }

        val brightness = brightnessSum / grayValues.size

        var contrastSum = 0.0f

        for (gray in grayValues) {
            val diff = gray - brightness
            contrastSum += diff * diff
        }

        val contrast = sqrt(contrastSum / grayValues.size)

        val sharpness = calculateLaplacianVariance(
            grayValues = grayValues,
            width = width,
            height = height
        )

        val isTooDark = brightness < DARK_THRESHOLD
        val isTooBright = brightness > BRIGHT_THRESHOLD
        val isLowContrast = contrast < LOW_CONTRAST_THRESHOLD
        val isBlurry = sharpness < BLUR_THRESHOLD

        val problemCount = listOf(
            isTooDark,
            isTooBright,
            isLowContrast,
            isBlurry
        ).count { it }

        val qualityLevel = when {
            problemCount >= 2 -> "较差"
            problemCount == 1 -> "一般"
            else -> "良好"
        }

        val suggestions = buildList {
            if (isTooDark) {
                add("当前图像可能偏暗，建议移到光线更充足的位置重新拍摄。")
            }

            if (isTooBright) {
                add("当前图像可能过亮，建议避开强光或反光区域重新拍摄。")
            }

            if (isLowContrast) {
                add("当前图像对比度较低，建议选择背景更简单、物体更清晰的拍摄角度。")
            }

            if (isBlurry) {
                add("当前图像可能较模糊，建议保持手机稳定，让物体对焦后再拍摄。")
            }

            if (isEmpty()) {
                add("当前图像质量较好，可以作为模型识别输入。")
            }
        }

        return ImageQualityResult(
            brightness = brightness,
            contrast = contrast,
            sharpness = sharpness,
            isTooDark = isTooDark,
            isTooBright = isTooBright,
            isLowContrast = isLowContrast,
            isBlurry = isBlurry,
            qualityLevel = qualityLevel,
            suggestions = suggestions
        )
    }

    private fun calculateLaplacianVariance(
        grayValues: FloatArray,
        width: Int,
        height: Int
    ): Float {
        val laplacianValues = mutableListOf<Float>()

        for (y in 1 until height - 1) {
            for (x in 1 until width - 1) {
                val center = grayValues[y * width + x]
                val left = grayValues[y * width + (x - 1)]
                val right = grayValues[y * width + (x + 1)]
                val up = grayValues[(y - 1) * width + x]
                val down = grayValues[(y + 1) * width + x]

                val laplacian = 4 * center - left - right - up - down

                laplacianValues.add(laplacian)
            }
        }

        if (laplacianValues.isEmpty()) {
            return 0.0f
        }

        val mean = laplacianValues.sum() / laplacianValues.size

        var varianceSum = 0.0f

        for (value in laplacianValues) {
            val diff = value - mean
            varianceSum += diff * diff
        }

        return varianceSum / laplacianValues.size
    }
}