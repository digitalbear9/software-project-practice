package com.example.wasteclassificationapp.ui

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun ModelHubScreen(
    onOpenModelSetting: () -> Unit,
    onOpenModelEvaluation: () -> Unit,
    onOpenOptimizationSamples: () -> Unit,
    onOpenStatistics: () -> Unit,
    onOpenFeedback: () -> Unit
) {
    HubPageContainer {
        PageHeader(
            title = "模型中心",
            subtitle = "管理模型版本、查看评估结果，并把反馈样本沉淀为优化依据。"
        )

        Spacer(modifier = Modifier.height(20.dp))

        FeatureCard(
            title = "模型设置",
            description = "在 Float32 和 Dynamic Range 量化模型之间切换对比。",
            buttonText = "打开设置",
            onClick = onOpenModelSetting
        )

        Spacer(modifier = Modifier.height(12.dp))

        FeatureCard(
            title = "模型评估报告",
            description = "查看准确率、推理耗时、模型大小和推荐使用场景。",
            buttonText = "查看报告",
            onClick = onOpenModelEvaluation
        )

        Spacer(modifier = Modifier.height(12.dp))

        FeatureCard(
            title = "模型优化样本池",
            description = "汇总错误反馈、低置信度和不确定结果，辅助后续复核。",
            buttonText = "查看样本池",
            onClick = onOpenOptimizationSamples
        )

        Spacer(modifier = Modifier.height(12.dp))

        FeatureCard(
            title = "识别统计",
            description = "按识别结果查看历史分类次数，了解模型使用情况。",
            buttonText = "查看统计",
            onClick = onOpenStatistics
        )

        Spacer(modifier = Modifier.height(12.dp))

        FeatureCard(
            title = "反馈记录",
            description = "查看用户对识别结果的正确或错误反馈记录。",
            buttonText = "查看反馈",
            onClick = onOpenFeedback
        )
    }
}
