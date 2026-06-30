package com.example.wasteclassificationapp.model

import com.example.wasteclassificationapp.data.FeedbackEntity
import com.example.wasteclassificationapp.data.HistoryEntity

data class OptimizationSample(
    val timeText: String,
    val source: String,
    val predictedLabel: String,
    val predictedLabelCn: String,
    val wasteCategory: String,
    val confidence: Float,
    val reason: String,
    val priority: String,
    val modelName: String,
    val topCandidatesText: String = "",
    val uncertaintyReason: String = ""
)

object OptimizationSampleRepository {

    fun buildSamples(
        historyList: List<HistoryEntity>,
        feedbackList: List<FeedbackEntity>
    ): List<OptimizationSample> {
        val wrongFeedbackSamples = feedbackList
            .filter { !it.isCorrect }
            .map { record ->
                OptimizationSample(
                    timeText = record.timeText,
                    source = "错误反馈",
                    predictedLabel = record.label,
                    predictedLabelCn = record.labelCn,
                    wasteCategory = record.wasteCategory,
                    confidence = record.confidence,
                    reason = "用户反馈识别错误，建议优先加入后续再训练候选集。",
                    priority = "高",
                    modelName = record.modelName
                )
            }

        val lowConfidenceSamples = historyList
            .filter { it.confidence < 0.70f }
            .map { record ->
                OptimizationSample(
                    timeText = record.timeText,
                    source = "低置信度",
                    predictedLabel = record.label,
                    predictedLabelCn = record.labelCn,
                    wasteCategory = record.wasteCategory,
                    confidence = record.confidence,
                    reason = "模型最高置信度低于 70%，该样本可能对模型优化有价值。",
                    priority = "中",
                    modelName = record.modelName,
                    topCandidatesText = record.topCandidatesText,
                    uncertaintyReason = record.uncertaintyReason
                )
            }

        val uncertainSamples = historyList
            .filter { it.isUncertain }
            .map { record ->
                OptimizationSample(
                    timeText = record.timeText,
                    source = "模型不确定",
                    predictedLabel = record.label,
                    predictedLabelCn = record.labelCn,
                    wasteCategory = record.wasteCategory,
                    confidence = record.confidence,
                    reason = if (record.uncertaintyReason.isNotBlank()) {
                        record.uncertaintyReason
                    } else {
                        "模型判断为不确定样本，建议人工复核。"
                    },
                    priority = if (record.confidence < 0.70f || record.top2Gap < 0.15f) {
                        "高"
                    } else {
                        "中"
                    },
                    modelName = record.modelName,
                    topCandidatesText = record.topCandidatesText,
                    uncertaintyReason = record.uncertaintyReason
                )
            }

        val smallGapSamples = historyList
            .filter { it.top2Gap < 0.15f }
            .map { record ->
                OptimizationSample(
                    timeText = record.timeText,
                    source = "候选类别接近",
                    predictedLabel = record.label,
                    predictedLabelCn = record.labelCn,
                    wasteCategory = record.wasteCategory,
                    confidence = record.confidence,
                    reason = "Top1 与 Top2 置信度差距小于 15%，模型可能在相近类别之间混淆。",
                    priority = "高",
                    modelName = record.modelName,
                    topCandidatesText = record.topCandidatesText,
                    uncertaintyReason = record.uncertaintyReason
                )
            }

        return (wrongFeedbackSamples + lowConfidenceSamples + uncertainSamples + smallGapSamples)
            .distinctBy { sample ->
                "${sample.timeText}_${sample.predictedLabel}_${sample.source}_${sample.reason}"
            }
            .sortedWith(
                compareByDescending<OptimizationSample> {
                    when (it.priority) {
                        "高" -> 3
                        "中" -> 2
                        else -> 1
                    }
                }.thenByDescending { it.timeText }
            )
    }

    fun buildCsv(samples: List<OptimizationSample>): String {
        val builder = StringBuilder()

        builder.append(
            "time,source,predictedLabel,predictedLabelCn,wasteCategory,confidence,reason,priority,modelName,topCandidates,uncertaintyReason\n"
        )

        samples.forEach { sample ->
            builder.append(csv(sample.timeText)).append(",")
            builder.append(csv(sample.source)).append(",")
            builder.append(csv(sample.predictedLabel)).append(",")
            builder.append(csv(sample.predictedLabelCn)).append(",")
            builder.append(csv(sample.wasteCategory)).append(",")
            builder.append(sample.confidence).append(",")
            builder.append(csv(sample.reason)).append(",")
            builder.append(csv(sample.priority)).append(",")
            builder.append(csv(sample.modelName)).append(",")
            builder.append(csv(sample.topCandidatesText)).append(",")
            builder.append(csv(sample.uncertaintyReason)).append("\n")
        }

        return builder.toString()
    }

    private fun csv(value: String): String {
        val escaped = value.replace("\"", "\"\"")
        return "\"$escaped\""
    }
}