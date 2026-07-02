package com.example.wasteclassificationapp.model

import android.content.Context
import java.text.DecimalFormat

data class ModelEvaluationItem(
    val modelName: String,
    val fileName: String,
    val modelType: String,
    val accuracyText: String,
    val avgInferenceTimeText: String,
    val inputShape: String,
    val classCount: Int,
    val isQuantized: Boolean,
    val fileSizeText: String,
    val advantage: String,
    val limitation: String,
    val recommendedScenario: String
)

object ModelEvaluationRepository {

    fun getEvaluationItems(context: Context): List<ModelEvaluationItem> {
        return listOf(
            ModelEvaluationItem(
                modelName = "Float32 模型",
                fileName = "waste_classification_mobilenetv2_v1_float32.tflite",
                modelType = "Float32 全精度模型",
                accuracyText = "94.15%",
                avgInferenceTimeText = "5.98 ms",
                inputShape = "224 × 224 × 3",
                classCount = 7,
                isQuantized = false,
                fileSizeText = getAssetFileSizeText(
                    context = context,
                    fileName = "waste_classification_mobilenetv2_v1_float32.tflite"
                ),
                advantage = "精度稳定，适合作为默认识别模型。",
                limitation = "模型文件通常比量化模型更大，占用存储空间更多。",
                recommendedScenario = "默认拍照识别、相册识别和对精度稳定性要求较高的场景。"
            ),

            ModelEvaluationItem(
                modelName = "Dynamic Range 量化模型",
                fileName = "waste_classification_mobilenetv2_v1_dynamic_range.tflite",
                modelType = "Dynamic Range 量化模型",
                accuracyText = "94.15%",
                avgInferenceTimeText = "26.01 ms",
                inputShape = "224 × 224 × 3",
                classCount = 7,
                isQuantized = true,
                fileSizeText = getAssetFileSizeText(
                    context = context,
                    fileName = "waste_classification_mobilenetv2_v1_dynamic_range.tflite"
                ),
                advantage = "模型文件更小，适合展示移动端模型压缩和部署优化思路。",
                limitation = "在当前测试环境下平均推理耗时不一定优于 Float32，需要结合设备实际表现分析。",
                recommendedScenario = "模型压缩对比、移动端部署实验和课程展示场景。"
            )
        )
    }

    private fun getAssetFileSizeText(
        context: Context,
        fileName: String
    ): String {
        return try {
            val afd = context.assets.openFd(fileName)
            val sizeBytes = afd.length
            afd.close()

            formatFileSize(sizeBytes)
        } catch (e: Exception) {
            "无法读取"
        }
    }

    private fun formatFileSize(sizeBytes: Long): String {
        val mb = sizeBytes / 1024.0 / 1024.0
        val formatter = DecimalFormat("#.##")
        return "${formatter.format(mb)} MB"
    }
}