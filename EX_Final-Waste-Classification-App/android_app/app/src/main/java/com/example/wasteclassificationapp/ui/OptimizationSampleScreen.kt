package com.example.wasteclassificationapp.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.wasteclassificationapp.model.OptimizationSample

@Composable
fun OptimizationSampleScreen(
    samples: List<OptimizationSample>,
    onExportSamples: () -> Unit,
    onBackHome: () -> Unit
) {
    val wrongFeedbackCount = samples.count { it.source == "错误反馈" }
    val lowConfidenceCount = samples.count { it.source == "低置信度" }
    val uncertainCount = samples.count { it.source == "模型不确定" }
    val highPriorityCount = samples.count { it.priority == "高" }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .navigationBarsPadding()
            .padding(24.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Text(
            text = "模型优化样本池",
            style = MaterialTheme.typography.headlineLarge,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(12.dp))

        Card(
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Text("本页面自动收集用户反馈错误、低置信度、模型不确定和候选类别接近的样本，用于后续人工复核和模型再训练。")
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Card(
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Text(
                    text = "样本池统计",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text("样本总数：${samples.size}")
                Text("错误反馈样本：$wrongFeedbackCount")
                Text("低置信度样本：$lowConfidenceCount")
                Text("不确定样本：$uncertainCount")
                Text("高优先级样本：$highPriorityCount")
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedButton(
            onClick = onExportSamples,
            modifier = Modifier.fillMaxWidth(),
            enabled = samples.isNotEmpty()
        ) {
            Text("导出优化样本 CSV")
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (samples.isEmpty()) {
            Card(
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        text = "暂无待优化样本",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text("你可以通过低置信度识别、模型不确定结果或点击“识别错误”来生成候选样本。")
                }
            }
        } else {
            Text(
                text = "待优化样本列表",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(8.dp))

            samples.forEach { sample ->
                OptimizationSampleCard(sample)
                Spacer(modifier = Modifier.height(12.dp))
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = onBackHome,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("返回首页")
        }

        Spacer(modifier = Modifier.height(24.dp))
    }
}

@Composable
private fun OptimizationSampleCard(
    sample: OptimizationSample
) {
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "${sample.source} · ${sample.priority}优先级",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text("时间：${sample.timeText}")
            Text("预测类别：${sample.predictedLabelCn}（${sample.predictedLabel}）")
            Text("垃圾类别：${sample.wasteCategory}")
            Text("置信度：${String.format("%.2f", sample.confidence * 100)}%")
            Text("使用模型：${sample.modelName}")

            Spacer(modifier = Modifier.height(8.dp))

            Text("入池原因：")
            Text(sample.reason)

            if (sample.topCandidatesText.isNotBlank()) {
                Spacer(modifier = Modifier.height(8.dp))
                Text("Top-3 候选：")
                Text(sample.topCandidatesText)
            }

            if (sample.uncertaintyReason.isNotBlank()) {
                Spacer(modifier = Modifier.height(8.dp))
                Text("不确定性说明：")
                Text(sample.uncertaintyReason)
            }
        }
    }
}
