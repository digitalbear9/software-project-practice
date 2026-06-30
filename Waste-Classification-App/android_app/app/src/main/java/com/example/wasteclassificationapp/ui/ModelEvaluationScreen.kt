package com.example.wasteclassificationapp.ui

import android.content.Context
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.wasteclassificationapp.model.ModelEvaluationItem
import com.example.wasteclassificationapp.model.ModelEvaluationRepository

@Composable
fun ModelEvaluationScreen(
    context: Context,
    currentModelName: String,
    onBackHome: () -> Unit
) {
    val evaluationItems = ModelEvaluationRepository.getEvaluationItems(context)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Text(
            text = "模型评估报告",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(12.dp))

        Text("本页面展示垃圾分类模型在移动端部署中的核心评估指标，包括模型类型、准确率、平均推理耗时、模型文件大小和推荐使用场景。")

        Spacer(modifier = Modifier.height(16.dp))

        Card(
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Text(
                    text = "当前使用模型",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text("当前模型：$currentModelName")
                Text("支持类别：can、food_box、food_packaging、fruit_peel、paper、paper_cup、plastic_bottle")
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        evaluationItems.forEach { item ->
            ModelEvaluationCard(item)
            Spacer(modifier = Modifier.height(12.dp))
        }

        Spacer(modifier = Modifier.height(16.dp))

        Card(
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Text(
                    text = "评估说明",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text("准确率来自测试集离线评估结果。")
                Text("平均推理耗时来自本项目 TFLite 测试脚本统计结果。")
                Text("模型文件大小由 APP 从 assets 目录自动读取。")
                Text("不同手机硬件、系统负载和运行环境可能导致实际推理耗时存在差异。")
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = onBackHome,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("返回首页")
        }
    }
}

@Composable
private fun ModelEvaluationCard(
    item: ModelEvaluationItem
) {
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = item.modelName,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text("模型文件：${item.fileName}")
            Text("模型类型：${item.modelType}")
            Text("测试准确率：${item.accuracyText}")
            Text("平均推理耗时：${item.avgInferenceTimeText}")
            Text("模型文件大小：${item.fileSizeText}")
            Text("输入尺寸：${item.inputShape}")
            Text("支持类别数：${item.classCount} 类")
            Text("是否量化：${if (item.isQuantized) "是" else "否"}")

            Spacer(modifier = Modifier.height(8.dp))

            Text("模型优势：")
            Text(item.advantage)

            Spacer(modifier = Modifier.height(8.dp))

            Text("模型局限：")
            Text(item.limitation)

            Spacer(modifier = Modifier.height(8.dp))

            Text("推荐场景：")
            Text(item.recommendedScenario)
        }
    }
}